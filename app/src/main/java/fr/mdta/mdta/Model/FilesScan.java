package fr.mdta.mdta.Model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

public class FilesScan extends Scan {

    //static values
    private final static String FILES_SCANNER_NAME = "Application Files Scanner";
    private final static String FILES_SCANNER_DESCRIPTION = "This scan can verify the integrity" +
            "of each file contained in an apk";

    private boolean suAvailable = false;
    private int my_uid = 0;

    private ArrayList<SimplifiedPackageInfo> listPackageInfo;


    private Callback mycallback = new Callback() {
        @Override
        public void OnErrorHappended() {

        }

        @Override
        public void OnErrorHappended(String error) {

        }

        @Override
        public void OnTaskCompleted(Object object) {
            endScanApp((ApplicationInfo) object);
        }
    };

    /**
     * @param simplifiedPackageInfos
     */
    public FilesScan(ArrayList<SimplifiedPackageInfo>
            simplifiedPackageInfos, Context context) {
        super(FILES_SCANNER_NAME, FILES_SCANNER_DESCRIPTION, simplifiedPackageInfos);

        suAvailable = Shell.SU.available();

        my_uid = context.getApplicationInfo().uid;

        fr.mdta.mdta.Tools.CommandFactory.pathToApkUnzipFolder = context.getFilesDir().toString() + "/";

    }

    @Override
    public void launchScan(Callback callback) {
        listPackageInfo = getmSimplifiedPackageInfos();

        for ( int i = 0; i < listPackageInfo.size(); i++){
            scanApp(listPackageInfo.get(i));
        }



    }

    @Override
    protected void updateState() {

    }

    protected void scanApp(final SimplifiedPackageInfo appInfo) {

        //Log.d("scan",app.packageName);

        //unzipApk(app.uid, app);

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
        }, appInfo, my_uid, getFileAppSELinuxContext());

        //TODO : Manage AsyncTask properly
    }

    protected void endScanApp(ApplicationInfo appInfo) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there
        // could be a
        // risk to rm -rf /&

        //Log.d("ending",app.packageName);
        fr.mdta.mdta.Tools.CommandFactory.endScanApp(appInfo);

        if ( listPackageInfo.contains(appInfo) ) {
            listPackageInfo.remove(appInfo);
            if ( !listPackageInfo.isEmpty() ) {
                scanApp(listPackageInfo.get(0));
            }
        }

    }

    protected void addFileToListVerification(final String filePath, final String hash, final
    SimplifiedPackageInfo appInfo, final String hashMethod, final ArrayList<Command> listProcess) {

        final String[] commandToExecute = new String[]{hashMethod + " -b " + fr.mdta.mdta
                .Tools.CommandFactory
                .pathToApkUnzipFolder +
                fr.mdta.mdta.Tools.CommandFactory.unzipApkToFolder + "_" +
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
                fr.mdta.mdta.Tools.CommandFactory.removeCommand(commandToExecute);
                fr.mdta.mdta.Tools.CommandFactory.launchVerification(mycallback, appInfo);

                String calculatedHash = (String) ((String) object).replaceAll("\\n", "")
                        .replaceAll("\\r", "");

                if (hash.equals(calculatedHash)) {
                    Log.d(filePath, hash + " / " + calculatedHash);
                } else {
                    cancelVerification(appInfo, filePath);
                    Log.d("false", "calc: " + calculatedHash + hashMethod + " " + hash + " " +
                            filePath + " " + appInfo.getAppUid());
                }
            }
        }, commandToExecute);

        listProcess.add(command);
    }

    protected void verifyHashesManifest(SimplifiedPackageInfo appInfo) {
        try {

            /**
             * https://stackoverflow.com/questions/3392189/reading-android-manifest-mf-file
             */

            JarFile jar = new JarFile(appInfo.getApkSourceDir());
            Manifest mf = jar.getManifest();
            Map<String, Attributes> map = mf.getEntries();

            ArrayList<Command> listProcess = new ArrayList<Command>();

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
            CommandFactory.listProcess = listProcess;
            CommandFactory.launchVerification(mycallback, appInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void cancelVerification(SimplifiedPackageInfo appInfo, String filepath) {
        for (int i = 0; i < CommandFactory.listProcess.size(); i++) {
            CommandFactory.listProcess.get(i).cancel(true);
        }
        CommandFactory.listProcess.clear();
        mycallback.OnTaskCompleted(appInfo);
        Log.d("FileScan", filepath + " hash is wrong");
    }

    protected String getFileAppSELinuxContext() {

        /**
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
            writer.flush();
            writer.close();
        }
    }

}
