package fr.mdta.mdta.Model;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
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

        if ( suAvailable ) {
            for ( int i = 0; i < listPackageInfo.size(); i++){
                scanApp(listPackageInfo.get(i));
            }
        } else {
            //TODO
        }

        callback.OnScanTerminated();
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

    @Override
    protected void updateState() {
        mState += 1;
    }

    private void scanApp(final SimplifiedPackageInfo appInfo) {

        //Log.d("scan",app.packageName);

        //unzipApk(app.uid, app);

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
            //TODO
        }
    }

    private void endScanApp(SimplifiedPackageInfo appInfo) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there
        // could be a
        // risk to rm -rf /&

        //Log.d("ending",app.packageName);
        fr.mdta.mdta.Tools.CommandFactory.endScanApp(appInfo,unzipApkToFolder);

        if ( listPackageInfo.contains(appInfo) ) {


            if ( mResults.get(appInfo).ismStatus() ) {
                endScanAppOK(appInfo);
            }

            listPackageInfo.remove(appInfo);
            updateState();
            if ( !listPackageInfo.isEmpty() ) {
                scanApp(listPackageInfo.get(0));
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
                fr.mdta.mdta.Tools.CommandFactory.launchVerification(mycallback, appInfo);

                String calculatedHash = ((String) object).replaceAll("\\n", "")
                        .replaceAll("\\r", "");

                if (hash.equals(calculatedHash)) {
                    Log.d(filePath, hash + " / " + calculatedHash);
                } else {

                    endScanAppTempered(appInfo,filePath,hashMethod,calculatedHash,hash);

                    //Log.d("false", "calc: " + calculatedHash + hashMethod + " " + hash + " " + filePath + " " + appInfo.getAppUid());
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

            for (Map.Entry<String, Attributes> entry : map.entrySet()) {

                String filePath = entry.getKey();

                String fileHash = entry.getValue().getValue("SHA-256-Digest");

                if (fileHash == null) {
                    fileHash = entry.getValue().getValue("SHA1-Digest");
                    if (fileHash == null) {
                        //MD5 or somethingElse ?
                    } else {
                        addFileToListVerification(filePath, fileHash, appInfo, "sha1sum", listProcess);
                    }
                } else {
                    addFileToListVerification(filePath, fileHash, appInfo, "sha256sum", listProcess);
                }
            }
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
        Log.d("FileScan", filepath + " hash is wrong");
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
            Method context = seLinux.getMethod("getFileContext", new Class[]{String.class});
            String result = (String) context.invoke(seLinux.newInstance(), new Object[]{fileName});

            return result;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } finally {
            writer.close();
        }
    }

    private void endScanAppOK(SimplifiedPackageInfo appInfo) {
        SpecificResult result = new SpecificResult(true,
                "This application was not tampered",
                "This application was not tampered");
        mResults.put(appInfo,result);
    }

    private void endScanAppTempered(SimplifiedPackageInfo appInfo, String filePath,
                                      String hashMethod, String calculatedHash,
                                      String hash) {
        SpecificResult result = new SpecificResult(false,
                "InvalidHash",filePath + " " + hashMethod + " is not valid : " +
                calculatedHash + " != " + hash);
        mResults.put(appInfo,result);

        cancelVerification(appInfo, filePath);
    }

}
