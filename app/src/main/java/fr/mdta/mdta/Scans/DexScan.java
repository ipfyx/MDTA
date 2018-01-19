package fr.mdta.mdta.Scans;

import android.content.Context;
import android.util.Log;

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
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Tools.CommandFactory;
import fr.mdta.mdta.Tools.DangerousMethodCall;
import fr.mdta.mdta.Tools.DangerousMethodPatternMap;

/**
 * Created by manwefm on 18/01/18.
 */

public class DexScan extends Scan {

    //static values
    private final static String DEX_SCANNER_NAME = "Application Dex Scanner";
    private final static String DEX_SCANNER_DESCRIPTION = "This scan looks for dangerous" +
            "methods in the code of an application";

    private boolean suAvailable = false;
    private int my_uid = 0;

    private String unzipApkToFolder = "unzipedApkDex";

    private ArrayList<SimplifiedPackageInfo> listPackageInfo;

    private ScanCallback endScanCallback = null;

    private HashMap<DangerousMethodCall, Integer> mapDangerousMethodCall = new HashMap<>();

    private final HashMap<String, DangerousMethodCall> mapDangerousMethodPattern =
            DangerousMethodPatternMap.getMapDangerousMethodPattern();

    public DexScan(ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos, Context context) {
        super(DEX_SCANNER_NAME, DEX_SCANNER_DESCRIPTION, simplifiedPackageInfos);

        suAvailable = Shell.SU.available();

        my_uid = context.getApplicationInfo().uid;

        fr.mdta.mdta.Tools.CommandFactory.pathToApkUnzipFolder = context.getFilesDir().toString() + "/";

    }

    @Override
    public void launchScan(ScanCallback callback) {
        listPackageInfo = getmSimplifiedPackageInfos();

        this.endScanCallback = callback;

        if ( suAvailable && !listPackageInfo.isEmpty() ) {
            scanApp(listPackageInfo.get(0));
        } else {
            endScanCallback.OnScanTerminated();
        }

    }

    @Override
    public void cancelScan(ScanCallback callback) {

    }

    @Override
    protected void updateState() {

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
                    scanAppDexFile(appInfo);
                }
            }, appInfo, my_uid, getFileAppSELinuxContext(), unzipApkToFolder);
        } else {
            resultScanFail(appInfo,"Could not get MDTA SELinux file context",
                    "getFileAppSELinuxContext() return null");
        }
    }

    private void endScanApp(SimplifiedPackageInfo appInfo) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there
        // could be a
        // risk to rm -rf /&

        //Log.d("ending",app.packageName);
        fr.mdta.mdta.Tools.CommandFactory.endScanApp(appInfo,unzipApkToFolder);

        if ( listPackageInfo.contains(appInfo) ) {

            if ( mResults.get(appInfo) == null ) {
                resultScanOK(appInfo);
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

    private void scanAppDexFile(SimplifiedPackageInfo appInfo) {

        mapDangerousMethodCall.put(DangerousMethodCall.LOAD_CPP_LIBRARY, 0);
        mapDangerousMethodCall.put(DangerousMethodCall.REFLECTION, 0);
        mapDangerousMethodCall.put(DangerousMethodCall.SELINUX, 0);
        mapDangerousMethodCall.put(DangerousMethodCall.SHELL, 0);

        final String appDirectory = CommandFactory.pathToApkUnzipFolder + unzipApkToFolder + "_" +
        appInfo.getAppUid();

        ArrayList<File> listDexFile = getDexFiles(appDirectory);

        for (int i = 0; i < listDexFile.size(); i++) {
            scanDexFile(listDexFile.get(i),appInfo);
        }

        Log.d("mapMethodCall", mapDangerousMethodCall.toString());

        endScanApp(appInfo);

    }

    private void scanDexFile(File file, SimplifiedPackageInfo appInfo) {

        try {

            //TODO give non null opcode
            DexBackedDexFile dexFile = DexFileFactory.loadDexFile(file, null);
            Log.d("scanning", file.getPath());
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
            resultScanFail(appInfo,file.getAbsolutePath()+" : Fail to read this dex file",e.getMessage());
        }
    }

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

    private void resultScanOK(SimplifiedPackageInfo appInfo) {
        SpecificResult result = new SpecificResult(true,
                mapDangerousMethodCall.toString(),
                mapDangerousMethodCall.toString());
        mResults.put(appInfo,result);
    }

    private void resultScanFail(SimplifiedPackageInfo appInfo, String reason, String detail) {
        SpecificResult result = new SpecificResult(true,
                reason,
                detail);
        mResults.put(appInfo, result);
    }
}
