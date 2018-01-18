package fr.mdta.mdta.Tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;

/**
 * Created by manwefm on 04/12/17.
 */

public final class CommandFactory {

    public static final int MAX_PROCESS_INTEGRITY = 5;
    public static ArrayList<Command> listProcessIntegrity = new ArrayList<Command>();
    public static HashMap<DangerousMethodCall, Integer> mapDangerousMethodCall =
            new HashMap<DangerousMethodCall, Integer>();
    public static HashMap<String, DangerousMethodCall> mapDangerousMethodPattern =
            new HashMap<String, DangerousMethodCall>();
    public static int COUNT = 0;
    public static String pathToApkUnzipFolder = "/data/local";
    public static String unzipApkToFolder = "unzipedApk";

    public static void execCommand(String[] command, Callback callback, Context context) {
        Command exec_command = new Command(callback, command);
        exec_command.execute(command);
    }

    public static void unzipCommand(Callback callback, SimplifiedPackageInfo appInfo, int
            my_uid, String SELinuxContext) {

        String[] listCommand = new String[]{
                "cd " + pathToApkUnzipFolder,
                "rm -rRf " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer.toString
                        (appInfo.getAppUid()),
                "mkdir -p " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer
                        .toString(appInfo.getAppUid()),
                "unzip " + appInfo.getApkSourceDir() + " -d " + pathToApkUnzipFolder + unzipApkToFolder + "_"
                        + Integer.toString(appInfo.getAppUid()),
                "chown -R " + my_uid + ":" + my_uid + " " + pathToApkUnzipFolder +
                        unzipApkToFolder + "_" + Integer.toString(appInfo.getAppUid()),
                "chcon -R " + SELinuxContext + " " + pathToApkUnzipFolder + unzipApkToFolder +
                        "_" + Integer.toString(appInfo.getAppUid())
                /* "echo " + app.packageName + " " + Integer.toString(app.uid)+">> "+pathToApkUnzipFolder+"test",
                "ls -lh " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer
                        .toString(app.uid)+">> "+pathToApkUnzipFolder+"test"
                */

        };

        //Log.d("CommandFactory",listCommand[6]);

        Command exec_command = new Command(callback, listCommand);
        exec_command.execute(listCommand);
    }

    public static void endScanApp(ApplicationInfo app) {

        String[] listCommand = new String[]{
                "cd /data/local",
                "rm -rRf " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer.toString(app
                        .uid)
        };

        Command exec_command = new Command(listCommand);
        exec_command.execute(listCommand);
    }

    public static void addCommandToExecute(final String[] command, Callback
            callback) {
        Command exec_command = new Command(callback, command);
        listProcessIntegrity.add(exec_command);
    }

    public static void removeCommandIntegrity(String[] command) {
        for (int i = 0; i < listProcessIntegrity.size(); i++) {
            if (listProcessIntegrity.get(i).getCommand().equals(command)) {
                listProcessIntegrity.remove(i);
                return;
            }
        }
    }

    public static void cancelCommand(String[] command) {
        for (int i = 0; i < listProcessIntegrity.size(); i++) {
            if (listProcessIntegrity.get(i).getCommand().equals(command)) {
                listProcessIntegrity.get(i).cancel(true);
                return;
            }
        }
    }

    public static void addCommand(Command command) {
        listProcessIntegrity.add(command);
    }

    public static void launchVerification(Callback callback, SimplifiedPackageInfo appInfo) {

        COUNT = 0;
        if (listProcessIntegrity.isEmpty()) {
            callback.OnTaskCompleted(appInfo);
        } else {
            for (int i = 0; i < listProcessIntegrity.size(); i++) {
                if (COUNT < MAX_PROCESS_INTEGRITY && listProcessIntegrity.get(i).getStatus() == AsyncTask.Status.PENDING) {
                    listProcessIntegrity.get(i).execute(listProcessIntegrity.get(i).getCommand());
                    COUNT += 1;
                } else {
                    return;
                }
            }
        }
    }

    public static void changeDirectoryContext(Callback callback, Context context, String directoryPath, String SELinuxContext) {

        String[] listCommand = new String[]{
                "chcon -R " + SELinuxContext + " " + directoryPath
        };
        Command exec_command = new Command(callback, listCommand);
        exec_command.execute(listCommand);
    }
}
