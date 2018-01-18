package fr.mdta.mdta.Tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

import fr.mdta.mdta.API.Callback.Callback;

/**
 * Created by manwefm on 04/12/17.
 */

public final class CommandFactory {

    static final int MAX_PROCESS = 5;
    public static ArrayList<Command> listProcess = new ArrayList<Command>();
    public static HashMap<DangerousMethodCall, Integer> mapDangerousMethodCall =
            new HashMap<DangerousMethodCall, Integer>();
    public static HashMap<String, DangerousMethodCall> mapDangerousMethodPattern =
            new HashMap<String, DangerousMethodCall>();
    static int COUNT = 0;
    static String pathToApkUnzipFolder = "/data/local";
    static String unzipApkToFolder = "unzipedApk";

    public static void execCommand(String[] command, Callback callback, Context context) {
        Command exec_command = new Command(callback, context, command);
        exec_command.execute(command);
    }

    public static void unzipCommand(Callback callback, Context context, ApplicationInfo app, int
            my_uid, String SELinuxContext) {

        String[] listCommand = new String[]{
                "cd " + pathToApkUnzipFolder,
                "rm -rRf " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer.toString
                        (app.uid),
                "mkdir -p " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer
                        .toString(app.uid),
                "unzip " + app.sourceDir + " -d " + pathToApkUnzipFolder + unzipApkToFolder + "_"
                        + Integer.toString(app.uid),
                "chown -R " + my_uid + ":" + my_uid + " " + pathToApkUnzipFolder +
                        unzipApkToFolder + "_" + Integer.toString(app.uid),
                "chcon -R " + SELinuxContext + " " + pathToApkUnzipFolder + unzipApkToFolder +
                        "_" + Integer.toString(app.uid)
                /* "echo " + app.packageName + " " + Integer.toString(app.uid)+">> "+pathToApkUnzipFolder+"test",
                "ls -lh " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer
                        .toString(app.uid)+">> "+pathToApkUnzipFolder+"test"
                */

        };

        //Log.d("CommandFactory",listCommand[6]);

        Command exec_command = new Command(callback, context, listCommand);
        exec_command.execute(listCommand);
    }

    public static void endScanApp(Callback callback, Context context, ApplicationInfo app) {

        String[] listCommand = new String[]{
                "cd /data/local",
                "rm -rRf " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer.toString(app
                        .uid)
        };

        Command exec_command = new Command(callback, context, listCommand);
        exec_command.execute(listCommand);
    }

    public static void addCommandToExecute(final String[] command, Context context, Callback
            callback) {
        Command exec_command = new Command(callback, context, command);
        listProcess.add(exec_command);
    }

    public static void removeCommand(String[] command) {
        for (int i = 0; i < listProcess.size(); i++) {
            if (listProcess.get(i).getCommand().equals(command)) {
                listProcess.remove(i);
                return;
            }
        }
    }

    public static void cancelCommand(String[] command) {
        for (int i = 0; i < listProcess.size(); i++) {
            if (listProcess.get(i).getCommand().equals(command)) {
                listProcess.get(i).cancel(true);
                return;
            }
        }
    }

    public static void addCommand(Command command) {
        listProcess.add(command);
    }

    public static void launchVerification(Callback callback, ApplicationInfo app) {

        COUNT = 0;
        if (listProcess.isEmpty()) {
            callback.OnTaskCompleted(app);
        } else {
            for (int i = 0; i < listProcess.size(); i++) {
                if (COUNT < MAX_PROCESS && listProcess.get(i).getStatus() == AsyncTask.Status.PENDING) {
                    listProcess.get(i).execute(listProcess.get(i).getCommand());
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
        Command exec_command = new Command(callback, context, listCommand);
        exec_command.execute(listCommand);
    }

    public enum DangerousMethodCall {
        REFLECTION,
        SHELL,
        LOAD_CPP_LIBRARY,
        SELINUX
    }
}
