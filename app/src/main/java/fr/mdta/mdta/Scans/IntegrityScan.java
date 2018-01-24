package fr.mdta.mdta.Scans;

import android.content.Context;

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

    private boolean suAvailable = false;
    private int my_uid = 0;

    private String unzipApkToFolder = "unzipedApkIntegrity";

    private ArrayList<SimplifiedPackageInfo> listPackageInfo;

    private ScanCallback endScanCallback = null;

    private int listPackageInfoCounter = 0;


    private String sha256DigestManifest = "SHA-256-Digest-Manifest";
    private String sha1DigestManifest = "SHA1-Digest-Manifest";

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

        fr.mdta.mdta.Tools.CommandFactory.pathToApkUnzipFolder = context.getFilesDir().toString() + "/";

    }

    @Override
    public void launchScan(ScanCallback callback) {
        listPackageInfo = getmSimplifiedPackageInfos();

        this.endScanCallback = callback;

        if ( suAvailable && listPackageInfoCounter < listPackageInfo.size() ) {
            scanApp(listPackageInfo.get(listPackageInfoCounter));
            listPackageInfoCounter+=1;
        } else {
            endScanCallback.OnScanTerminated();
        }
    }


    @Override
    public void cancelScan(ScanCallback callback) {
        for (int i = 0; i < CommandFactory.listProcessIntegrity.size(); i++) {
            CommandFactory.listProcessIntegrity.get(i).cancel(true);
        }
        CommandFactory.listProcessIntegrity.clear();

        fr.mdta.mdta.Tools.CommandFactory.brutallyEndScanApp(unzipApkToFolder);

        callback.OnScanTerminated();
    }

    //protected void updateStateEndScanApp() {
    //    float number_of_app_scanned = listPackageInfo.size();
    //    mState += (100/number_of_app_scanned);
    //}

    protected void updateStateHashCalculated() {
        float sizeListProcess = CommandFactory.listProcessIntegrity.size();
        float number_of_app_scanned = listPackageInfo.size();
        mState += 100/(sizeListProcess*number_of_app_scanned);

    }

    private void scanApp(final SimplifiedPackageInfo appInfo) {

        if ( getFileAppSELinuxContext() != null ) {
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
            }, appInfo, my_uid, getFileAppSELinuxContext(), unzipApkToFolder);
        } else {
            resultScanFail(appInfo,"Could not get MDTA SELinux file context",
                    "getFileAppSELinuxContext() return null");
        }
    }

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
            updateStateHashCalculated();
            if ( listPackageInfoCounter < listPackageInfo.size() ) {
                scanApp(listPackageInfo.get(listPackageInfoCounter));
                listPackageInfoCounter+=1;
            } else {
                endScanCallback.OnScanTerminated();
            }
        }

    }

    private void addFileToListVerification(final String filePath, final String hash, final
    SimplifiedPackageInfo appInfo, final String hashMethod, final ArrayList<Command> listProcess) {

        final String[] commandToExecute = new String[]{hashMethod + " -b " + fr.mdta.mdta
                .Tools.CommandFactory
                .pathToApkUnzipFolder +
                unzipApkToFolder + "_" +
                Integer.toString(appInfo.getAppUid()) + "/" + filePath + "| xxd -r -p | base64"};

        Command command = new Command(new Callback() {
            @Override
            public void OnErrorHappended() {

            }

            @Override
            public void OnErrorHappended(String error) {

            }

            @Override
            public void OnTaskCompleted(Object object) {
                fr.mdta.mdta.Tools.CommandFactory.COUNT -= 1;
                fr.mdta.mdta.Tools.CommandFactory.removeCommandIntegrity(commandToExecute);
                //fr.mdta.mdta.Tools.CommandFactory.launchVerification(mycallback, appInfo);

                String calculatedHash = ((String) object).replaceAll("\\n", "")
                        .replaceAll("\\r", "");

                if (hash.equals(calculatedHash)) {
                    updateStateHashCalculated();
                } else {
                    resultScanAppTempered(appInfo,filePath,hashMethod,calculatedHash,hash);
                }
            }
        }, commandToExecute);

        listProcess.add(command);
    }

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
            CommandFactory.launchVerification(mycallback, appInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cancelVerification(SimplifiedPackageInfo appInfo, String filepath) {
        for (int i = 0; i < CommandFactory.listProcessIntegrity.size(); i++) {
            CommandFactory.listProcessIntegrity.get(i).cancel(true);
        }
        CommandFactory.listProcessIntegrity.clear();
        mycallback.OnTaskCompleted(appInfo);
    }

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

    private void openCERTSF(SimplifiedPackageInfo appInfo, ArrayList<Command> listProcess) {

        String certPath = fr.mdta.mdta.Tools.CommandFactory.pathToApkUnzipFolder +
                unzipApkToFolder + "_" + Integer.toString(appInfo.getAppUid()) +
                "/META-INF/CERT.SF";

        //the full path is generated in addFileToListVerification
        String manifestPath = "META-INF/MANIFEST.MF";
        String[] hashEntryManifest;
        String hashManifest = "";
        try {
            File certFile = new File(certPath);
            BufferedReader reader = new BufferedReader(new FileReader(certFile));
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                //String trimmedLine = currentLine.trim();
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

    private void resultScanAppOK(SimplifiedPackageInfo appInfo) {
        SpecificResult result = new SpecificResult(true,
                "This application was not tampered",
                "This application was not tampered");
        mResults.put(appInfo,result);
    }

    private void resultScanAppTempered(SimplifiedPackageInfo appInfo, String filePath,
                                      String hashMethod, String calculatedHash,
                                      String hash) {
        SpecificResult result = new SpecificResult(false,
                "InvalidHash",filePath + " " + hashMethod + " is not valid : " +
                calculatedHash + " != " + hash);
        mResults.put(appInfo,result);

        cancelVerification(appInfo, filePath);
    }

    private void resultScanFail(SimplifiedPackageInfo appInfo, String reason, String detail) {
        SpecificResult result = new SpecificResult(true,
                reason,
                detail);
        mResults.put(appInfo,result);
    }

}
