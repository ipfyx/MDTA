package fr.mdta.mdta.Scans;

import android.content.Context;
import android.os.AsyncTask;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import eu.chainfire.libsuperuser.Shell;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Tools.CommandFactory;
import fr.mdta.mdta.Tools.DangerousMethodCall;
import fr.mdta.mdta.Tools.DangerousMethodPatternMap;

/**
 * Created by manwefm on 18/01/18.
 */

public class DexScan extends Scan {

    //description of the scan
    private final static String DEX_SCANNER_NAME = "Application Dex Scanner";
    private final static String DEX_SCANNER_DESCRIPTION = "This scan looks for dangerous" +
            "methods in the code of an application";

    //max values allowed for an app
    private final int MAX_NUMBER_SHELL_CALL = 4;
    private final int MAX_NUMBER_LOAD_CPP = 2;
    private final int MAX_NUMBER_SELINUX_CALL = 0;
    private final int MAX_NUMBER_REFLECTION = 2;

    //map of dangerous method called strings, associated with their danger (reflection, shell...)
    //its a singleton
    private final HashMap<String, DangerousMethodCall> mapDangerousMethodPattern =
            DangerousMethodPatternMap.getMapDangerousMethodPattern();

    //need root access
    private boolean suAvailable = false;

    //need uid of app, set properly in constructor
    private int my_uid = 0;

    //set beginning of name of the directory where app are unziped
    private String unzipApkToFolder = "unzipedApkDex";

    //list of app to scan
    private ArrayList<SimplifiedPackageInfo> listPackageInfo;

    //callback to call when scan is done
    private ScanCallback endScanCallback = null;

    //counter to browse listpackage
    private int listPackageInfoCounter = 0;

    //map to count number of called to reflection, shell command etc.
    private HashMap<DangerousMethodCall, Integer> mapDangerousMethodCall = new HashMap<>();

    //we need the file context required for the app to read files from java
    private String seLinuxFileContext;

    //there is always at least one dex file
    private int numberOfDexScan = 1;

    public DexScan(ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos, Context context) {
        super(DEX_SCANNER_NAME, DEX_SCANNER_DESCRIPTION, simplifiedPackageInfos);

        suAvailable = Shell.SU.available();

        my_uid = context.getApplicationInfo().uid;

        fr.mdta.mdta.Tools.CommandFactory.pathToApkUnzipFolder = context.getFilesDir().toString() + "/";

        seLinuxFileContext = getFileAppSELinuxContext();

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

    }

    protected void updateState() {
        float number_of_app_scanned = listPackageInfo.size();
        mState += (100/(number_of_app_scanned*numberOfDexScan));

    }

    /**
     * app scanning starts here
     * @param appInfo
     */

    private void scanApp(final SimplifiedPackageInfo appInfo) {

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
                    scanAppDexFile(appInfo);
                }
            }, appInfo, my_uid, seLinuxFileContext, unzipApkToFolder);
        } else {
            resultScanFail(appInfo,"Could not get MDTA SELinux file context",
                    "getFileAppSELinuxContext() return null");
        }
    }

    /**
     * end of scan, remove folder where app was unziped
     * launch next scan on next app
     * @param appInfo
     */

    private void endScanApp(SimplifiedPackageInfo appInfo) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there
        // could be a
        // risk to rm -rf /&

        fr.mdta.mdta.Tools.CommandFactory.endScanApp(appInfo,unzipApkToFolder);

        if ( listPackageInfo.contains(appInfo) ) {

            if ( mResults.get(appInfo) == null ) {
                resultScanOK(appInfo);
            }
            if ( listPackageInfoCounter < listPackageInfo.size() ) {
                scanApp(listPackageInfo.get(listPackageInfoCounter));
                listPackageInfoCounter+=1;
            } else {
                endScanCallback.OnScanTerminated();
            }
        } else if ( listPackageInfo.isEmpty() ) {
            endScanCallback.OnScanTerminated();
        }

    }

    private String getFileAppSELinuxContext() {

        /*
         * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/os/SELinux.java
         */

        final String fileName = CommandFactory.pathToApkUnzipFolder + "SELinuxTest.txt";

        Class seLinux;
        PrintWriter writer = null;
        try {

            writer = new PrintWriter(fileName, "UTF-8");
            writer.println(fileName);

            seLinux = Class.forName("android.os.SELinux");
            Method context = seLinux.getMethod("getFileContext", String.class);

            return (String) context.invoke(seLinux.newInstance(), fileName);

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
                | InstantiationException | InvocationTargetException | FileNotFoundException
                | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void scanAppDexFile(final SimplifiedPackageInfo appInfo) {

        //each attribut in mapDangerousMethodCall is set to 0 to count properly

        mapDangerousMethodCall.put(DangerousMethodCall.LOAD_CPP_LIBRARY, 0);
        mapDangerousMethodCall.put(DangerousMethodCall.REFLECTION, 0);
        mapDangerousMethodCall.put(DangerousMethodCall.SELINUX, 0);
        mapDangerousMethodCall.put(DangerousMethodCall.SHELL, 0);

        final String appDirectory = CommandFactory.pathToApkUnzipFolder + unzipApkToFolder + "_" +
        appInfo.getAppUid();

        //there can be more than one dexfile per app

        final ArrayList<File> listDexFile = getDexFiles(appDirectory);

        if ( listDexFile.isEmpty() ) {
            //update graphics when one dexfile is done scanning
            updateState();
            endScanApp(appInfo);
        } else {
            numberOfDexScan = listDexFile.size();

            final int[] numberOfDexFileScanned = {0};

            for (int i = 0; i < listDexFile.size(); i++) {
                scanDexFile(listDexFile.get(i), appInfo, new Callback() {
                    @Override
                    public void OnErrorHappended() {

                    }

                    @Override
                    public void OnErrorHappended(String error) {

                    }

                    @Override
                    public void OnTaskCompleted(Object object) {

                        updateState();

                        if ((Boolean) object.equals(true)) {
                            numberOfDexFileScanned[0]++;
                            if (numberOfDexFileScanned[0] >= listDexFile.size()) {
                                //end scan app if all dex files where scanned
                                endScanApp(appInfo);
                            }
                        } else {
                            endScanApp(appInfo);
                        }


                    }
                });
            }
        }

    }

    /**
     * launch the process to scan the dex file
     * @param file
     * @param appInfo
     * @param callback
     */
    private void scanDexFile(File file, SimplifiedPackageInfo appInfo, Callback callback) {

        DexFileScanner dexFileScanner = new DexFileScanner(file, appInfo, callback);
        dexFileScanner.execute();
    }

    /**
     * get the list of dex files to scan
     * @param appDirectory
     * @return
     */

    private ArrayList<File> getDexFiles(String appDirectory) {
        File classesDex = new File(appDirectory + "/classes.dex");
        int count = 1;

        ArrayList<File> listDexFile = new ArrayList<>();

        while (classesDex.exists()) {
            count += 1;
            listDexFile.add(classesDex);
            classesDex = new File(appDirectory + "/classes" + count + ".dex");
        }
        return listDexFile;
    }

    /**
     * the scan is successful
     * @param appInfo
     */

    private void resultScanOK(SimplifiedPackageInfo appInfo) {

        if (mapDangerousMethodCall.get(DangerousMethodCall.SHELL) > MAX_NUMBER_SHELL_CALL ||
                mapDangerousMethodCall.get(DangerousMethodCall.REFLECTION) > MAX_NUMBER_REFLECTION ||
                mapDangerousMethodCall.get(DangerousMethodCall.LOAD_CPP_LIBRARY) > MAX_NUMBER_LOAD_CPP ||
                mapDangerousMethodCall.get(DangerousMethodCall.SELINUX) > MAX_NUMBER_SELINUX_CALL) {
            SpecificResult result = new SpecificResult(false,
                    "This application is dangerous",
                    mapDangerousMethodCall.toString());
            mResults.put(appInfo,result);
        } else {
            SpecificResult result = new SpecificResult(true,
                    "Dangerous Method Call",
                    mapDangerousMethodCall.toString());
            mResults.put(appInfo,result);
        }

    }

    /**
     * the scan failed
     * @param appInfo
     * @param reason
     * @param detail
     */

    private void resultScanFail(SimplifiedPackageInfo appInfo, String reason, String detail) {
        SpecificResult result = new SpecificResult(true,
                reason,
                detail);
        mResults.put(appInfo, result);
    }

    /**
     * asyncTash to scan a dex file
     */

    private class DexFileScanner extends AsyncTask<Void, Void, Boolean> {
        private Callback callback;
        private File file;
        private SimplifiedPackageInfo simplifiedPackageInfo;


        public DexFileScanner(File file, SimplifiedPackageInfo simplifiedPackageInfo, Callback callback) {
            this.callback = callback;
            this.file = file;
            this.simplifiedPackageInfo = simplifiedPackageInfo;
        }


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            Boolean result = true;

            try {

                //TODO give non null opcode
                DexBackedDexFile dexFile = DexFileFactory.loadDexFile(file, null);
                for (Object o : dexFile.getMethods()) {
                    String a = o.toString();
                    for (String pattern : mapDangerousMethodPattern.keySet()) {
                        if (a.toLowerCase().contains(pattern)) {
                            mapDangerousMethodCall.put(
                                    mapDangerousMethodPattern.get(pattern),
                                    mapDangerousMethodCall.get(mapDangerousMethodPattern.get(pattern)
                                    ) + 1
                            );
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                resultScanFail(simplifiedPackageInfo,file.getAbsolutePath()+" : Fail to read this dex file",e.getMessage());
                result  = false;
            } finally {
                return result;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            callback.OnTaskCompleted(result);
        }
    }
}
