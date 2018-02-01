package fr.mdta.mdta.Scans;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import eu.chainfire.libsuperuser.Shell;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Tools.Command;
import fr.mdta.mdta.Tools.CommandFactory;

/**
 * Created by manwefm on 18/01/18.
 */

public class IntegrityScan extends Scan {

    //static values
    private final static String FILES_SCANNER_NAME = "Application Files Scanner";
    private final static String FILES_SCANNER_DESCRIPTION = "This scan can verify the integrity" +
            "of each file contained in an apk";


    //We need not be root
    private boolean suAvailable = false;

    //uid of mdta application
    private int my_uid = 0;

    //begining of the name of the directory where each apk will be unziped
    private String unzipApkToFolder = "unzipedApkIntegrity";

    //list of app to be scanned
    private ArrayList<SimplifiedPackageInfo> listPackageInfo;

    //callback called when integrityScan is down
    private ScanCallback endScanCallback = null;

    //counter to browse listPackageInfo
    private int listPackageInfoCounter = 0;

    //number of file to process set for each app
    private float sizeListProcess = 0;

    private String sha256DigestManifest = "SHA-256-Digest-Manifest";
    private String sha1DigestManifest = "SHA1-Digest-Manifest";

    //number of file to process set for each app
    private int numberOfEntriesInManifest;

    //file context needed to read file from java mdta
    private String seLinuxFileContext;

    //callback called when the verification of one app is done
    private Callback mycallback = new Callback() {
        @Override
        public void OnErrorHappended() {

        }

        @Override
        public void OnErrorHappended(String error) {

        }

        @Override
        public void OnTaskCompleted(Object object) {
            endScanApp((SimplifiedPackageInfo) object);
        }
    };

    public IntegrityScan(ArrayList<SimplifiedPackageInfo>
            simplifiedPackageInfos, Context context) {
        super(FILES_SCANNER_NAME, FILES_SCANNER_DESCRIPTION, simplifiedPackageInfos);

        suAvailable = Shell.SU.available();

        my_uid = context.getApplicationInfo().uid;

        //Lets unzip in our app directory
        fr.mdta.mdta.Tools.CommandFactory.pathToApkUnzipFolder = context.getFilesDir().toString() + "/";

        seLinuxFileContext = getFileAppSELinuxContext();

    }

    /**
     * method called to launch the scan by the activity
     **/

    @Override
    public void launchScan(ScanCallback callback) {

        //lets get the list of app to scan
        listPackageInfo = getmSimplifiedPackageInfos();

        this.endScanCallback = callback;

        if ( suAvailable && listPackageInfoCounter < listPackageInfo.size() ) {
            //scan the first app and increase counter to scan the following app to scan when done
            scanApp(listPackageInfo.get(listPackageInfoCounter));
            listPackageInfoCounter+=1;
        } else {
            endScanCallback.OnScanTerminated();
        }
    }


    /**
     * method called to cancel this scan, not used
     **/
    @Override
    public void cancelScan(ScanCallback callback) {
        for (int i = 0; i < CommandFactory.listProcessIntegrity.size(); i++) {
            CommandFactory.listProcessIntegrity.get(i).cancel(true);
        }
        CommandFactory.listProcessIntegrity.clear();

        fr.mdta.mdta.Tools.CommandFactory.brutallyEndScanApp(unzipApkToFolder);

        callback.OnScanTerminated();
    }

    /**
     * method called to update the graphics
     */

    protected void updateStateHashCalculated() {
        float number_of_app_scanned = listPackageInfo.size();
        mState += 100/(sizeListProcess*number_of_app_scanned);
    }

    /**
     * the scan of an app starts here
     * @param appInfo
     */

    private void scanApp(final SimplifiedPackageInfo appInfo) {

        //let's first unzip the apk to a directory we control

        if ( seLinuxFileContext != null ) {
            fr.mdta.mdta.Tools.CommandFactory.unzipCommand(new Callback() {
                @Override
                public void OnErrorHappended() {

                }

                @Override
                public void OnErrorHappended(String error) {

                }

                @Override
                public void OnTaskCompleted(Object object) {
                    verifyHashesManifest(appInfo);
                }
            }, appInfo, my_uid, seLinuxFileContext, unzipApkToFolder);
        } else {
            resultScanFail(appInfo,"Could not get MDTA SELinux file context",
                    "getFileAppSELinuxContext() return null");
        }
    }

    /**
     * end scan app
     * remove folder where app was unziped
     * launch scan next application
     * @param simplifiedPackageInfo
     */

    private void endScanApp(SimplifiedPackageInfo simplifiedPackageInfo) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there
        // could be a
        // risk to rm -rf /&

        CommandFactory.endScanApp(simplifiedPackageInfo,unzipApkToFolder);

        if ( listPackageInfo.contains(simplifiedPackageInfo) ) {

            if ( mResults.get(simplifiedPackageInfo) == null ) {
                this.resultScanAppOK(simplifiedPackageInfo);
            }

            //listPackageInfo.remove(simplifiedPackageInfo);
            //updateStateHashCalculated();
            if ( listPackageInfoCounter < listPackageInfo.size() ) {
                scanApp(listPackageInfo.get(listPackageInfoCounter));
                listPackageInfoCounter+=1;
            } else {
                endScanCallback.OnScanTerminated();
            }
        }

    }

    /**
     * for a file, creates asyncTask to launch to process the verification of the
     * hash of the file in the manifest
     * @param filePath
     * @param hash
     * @param appInfo
     * @param hashMethod
     * @param listProcess
     */

    private void addFileToListVerification(final String filePath, final String hash, final
    SimplifiedPackageInfo appInfo, final String hashMethod, final ArrayList<Command> listProcess) {

        //beautiful command to execute to get the hash of a file in base64

        final String[] commandToExecute = new String[]{hashMethod + " -b " + "\'"+fr.mdta.mdta
                .Tools.CommandFactory
                .pathToApkUnzipFolder +
                unzipApkToFolder + "_" +
                Integer.toString(appInfo.getAppUid()) + "/" + filePath + "\'| xxd -r -p | base64"};

        Command command = new Command(new Callback() {
            @Override
            public void OnErrorHappended() {

            }

            @Override
            public void OnErrorHappended(String error) {

            }

            @Override
            public void OnTaskCompleted(Object object) {
                //remove asyncTask from list because finished
                fr.mdta.mdta.Tools.CommandFactory.removeCommandIntegrity(commandToExecute);

                //relaunch verification or scan stops
                //TODO: fix
                fr.mdta.mdta.Tools.CommandFactory.launchVerification(mycallback, appInfo);

                //remove carriage return
                String calculatedHash = ((String) object).replaceAll("\\n", "")
                        .replaceAll("\\r", "");

                if (hash.equals(calculatedHash)) {
                    updateStateHashCalculated();
                    //end scan if all process are done
                    if (CommandFactory.listProcessIntegrity.isEmpty()) {
                        mycallback.OnTaskCompleted(appInfo);
                    }

                } else {
                    //if one file was tempered, end all scan and flag app as malicious
                    resultScanAppTempered(appInfo,filePath,hashMethod,calculatedHash,hash);
                }
            }
        }, commandToExecute);

        listProcess.add(command);
    }

    /**
     * for each file in the manifest, creates the asyncTask to launch
     * to verify the hash, store them in listProcess
     * then set CommandFactory.listProcessIntegrity = listProcess
     * and launchVerification
     * @param appInfo
     */

    private void verifyHashesManifest(SimplifiedPackageInfo appInfo) {
        try {

            /*
             * https://stackoverflow.com/questions/3392189/reading-android-manifest-mf-file
             */

            JarFile jar = new JarFile(appInfo.getApkSourceDir());
            Manifest mf = jar.getManifest();

            Map<String, Attributes> map = mf.getEntries();

            ArrayList<Command> listProcess = new ArrayList<>();

            openCERTSF(appInfo, listProcess);

            numberOfEntriesInManifest = map.size();

            for (Map.Entry<String, Attributes> entry : map.entrySet()) {

                String filePath = entry.getKey();

                String fileHash = entry.getValue().getValue("SHA-256-Digest");

                if (fileHash == null) {
                    fileHash = entry.getValue().getValue("SHA1-Digest");
                    if (fileHash == null) {
                        resultScanFail(appInfo,"Unknown Hash Method","This app is not " +
                                "using sha1 digest or sha256 digest for file "+filePath+", fileHash = " + fileHash);
                    } else {
                        addFileToListVerification(filePath, fileHash, appInfo, "sha1sum", listProcess);
                    }
                } else {
                    addFileToListVerification(filePath, fileHash, appInfo, "sha256sum", listProcess);
                }
            }
            CommandFactory.listProcessIntegrity.clear();
            CommandFactory.listProcessIntegrity = listProcess;
            sizeListProcess = listProcess.size();
            //Launch every asynTask in listProcess
            CommandFactory.launchVerification(mycallback, appInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * cancelVerification if a hash is wrong
     * @param appInfo
     */

    private void cancelVerification(SimplifiedPackageInfo appInfo) {
        for (int i = 0; i < CommandFactory.listProcessIntegrity.size(); i++) {
            CommandFactory.listProcessIntegrity.get(i).cancel(true);
        }
        CommandFactory.listProcessIntegrity.clear();
        mycallback.OnTaskCompleted(appInfo);
    }

    /**
     * we need the file context needed to allow our application
     * to read files from java
     * @return
     */

    private String getFileAppSELinuxContext() {

        /*
         * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/os/SELinux.java
         */

        final String fileName = CommandFactory.pathToApkUnzipFolder + "SELinuxTest.txt";

        Class seLinux = null;
        PrintWriter writer = null;
        try {

            writer = new PrintWriter(fileName, "UTF-8");
            writer.println(fileName);

            //we needed to use reflection to call getFilesContext
            //we were not able to import android.os.SELinux from androidStudio

            seLinux = Class.forName("android.os.SELinux");
            Method context = seLinux.getMethod("getFileContext", String.class);

            return (String) context.invoke(seLinux.newInstance(), fileName);

        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException | FileNotFoundException
                | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * open file *.sf
     * @param appInfo
     * @param listProcess
     */
    private void openCERTSF(final SimplifiedPackageInfo appInfo, final ArrayList<Command> listProcess) {

        //we know the path to *.sf but not is real name
        final String pathMETAINF = fr.mdta.mdta.Tools.CommandFactory.pathToApkUnzipFolder +
                unzipApkToFolder + "_" + Integer.toString(appInfo.getAppUid()) +
                "/META-INF/";

        //lets use a beautiful grep to get the name of this file in order to read it
        CommandFactory.getCertNames(pathMETAINF, new Callback() {
            @Override
            public void OnErrorHappended() {

            }

            @Override
            public void OnErrorHappended(String error) {

            }

            @Override
            public void OnTaskCompleted(Object object) {

                //awesome, we now have the name of *.SF, which can be WHATSAPP.SF, toto.SF etc.
                String nameCERT = ((String) object).replaceAll("\\n", "")
                        .replaceAll("\\r", "");
                String certPath = pathMETAINF+nameCERT+".SF";

                //the full path is generated in addFileToListVerification
                String manifestPath = "META-INF/MANIFEST.MF";
                String[] hashEntryManifest;
                String hashManifest = "";
                try {
                    File certFile = new File(certPath);
                    BufferedReader reader = new BufferedReader(new FileReader(certFile));
                    String currentLine;

                    //lets read the file to find the right hash to extract
                    //we are looking for the hash of *.MF
                    while((currentLine = reader.readLine()) != null) {
                        if(currentLine.contains(sha256DigestManifest)){
                            hashEntryManifest = currentLine.split(":");
                            hashManifest = hashEntryManifest[1].trim();
                            addFileToListVerification(manifestPath, hashManifest, appInfo, "sha256sum", listProcess);
                            break;
                        } else if (currentLine.contains(sha1DigestManifest)) {
                            hashEntryManifest = currentLine.split(":");
                            hashManifest = hashEntryManifest[1].trim();
                            addFileToListVerification(manifestPath, hashManifest, appInfo, "sha1sum", listProcess);
                            break;
                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * app was not tempered
     * @param appInfo
     */

    private void resultScanAppOK(SimplifiedPackageInfo appInfo) {
        SpecificResult result = new SpecificResult(true,
                "This application was not tampered",
                "This application was not tampered, number of files scanned : "+
        Integer.toString(numberOfEntriesInManifest));
        mResults.put(appInfo,result);
    }

    /**
     * app was tempered
     * @param appInfo
     * @param filePath
     * @param hashMethod
     * @param calculatedHash
     * @param hash
     */
    private void resultScanAppTempered(SimplifiedPackageInfo appInfo, String filePath,
                                      String hashMethod, String calculatedHash,
                                      String hash) {
        SpecificResult result = new SpecificResult(false,
                "InvalidHash",filePath + " " + hashMethod + " is not valid : " +
                calculatedHash + " != " + hash+", number of files scanned : "+
                Integer.toString(numberOfEntriesInManifest));
        mResults.put(appInfo,result);

        cancelVerification(appInfo);
    }

    /**
     * something went wrong during the scan
     * @param appInfo
     * @param reason
     * @param detail
     */

    private void resultScanFail(SimplifiedPackageInfo appInfo, String reason, String detail) {
        SpecificResult result = new SpecificResult(true,
                reason,
                detail+", number of files scanned : "+ Integer.toString(numberOfEntriesInManifest));
        mResults.put(appInfo,result);
    }

}
