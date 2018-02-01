package fr.mdta.mdta.Tools;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;

/**
 * Created by manwefm on 04/12/17.
 */

public final class CommandFactory {

    //private static final int MAX_PROCESS_INTEGRITY = 5;
    //list of asynctask to process
    public static ArrayList<Command> listProcessIntegrity = new ArrayList<>();

    //public static int COUNT = 0;

    //path to unzip directory, set in constructor of DexScanner and IntegrityScanner
    public static String pathToApkUnzipFolder = "/data/local";

    /**
     * shell command to execute to unzip an apk
     * @param callback
     * @param appInfo
     * @param my_uid
     * @param SELinuxContext
     * @param unzipDirectoryName
     */
    public static void unzipCommand(Callback callback, SimplifiedPackageInfo appInfo, int
            my_uid, String SELinuxContext, String unzipDirectoryName) {

        String[] listCommand = new String[]{
                //we move to pathtoApkUnzipFolder to know where we are
                "cd " + pathToApkUnzipFolder,
                //we clean the directory that could already exist
                "rm -rRf " + pathToApkUnzipFolder + unzipDirectoryName + "_" + Integer.toString
                        (appInfo.getAppUid()),
                //create the directory we unzip to
                "mkdir -p " + pathToApkUnzipFolder + unzipDirectoryName + "_" + Integer
                        .toString(appInfo.getAppUid()),
                //unzip the apk
                "unzip " + appInfo.getApkSourceDir() + " -d " + pathToApkUnzipFolder + unzipDirectoryName + "_"
                        + Integer.toString(appInfo.getAppUid()),
                //change owner of files
                "chown -R " + my_uid + ":" + my_uid + " " + pathToApkUnzipFolder +
                        unzipDirectoryName + "_" + Integer.toString(appInfo.getAppUid()),
                //change files context
                "chcon -R " + SELinuxContext + " " + pathToApkUnzipFolder + unzipDirectoryName +
                        "_" + Integer.toString(appInfo.getAppUid())
                /* "echo " + app.packageName + " " + Integer.toString(app.uid)+">> "+pathToApkUnzipFolder+"test",
                "ls -lh " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer
                        .toString(app.uid)+">> "+pathToApkUnzipFolder+"test"
                */

        };

        Command exec_command = new Command(callback, listCommand);
        exec_command.execute(listCommand);
    }

    public static void endScanApp(SimplifiedPackageInfo app, String unzipDirectoryName) {

        //remove folder when scan app is done
        String[] listCommand = new String[]{
                "cd /data/local",
                "rm -rRf " + pathToApkUnzipFolder + unzipDirectoryName + "_" + Integer.toString(app
                        .getAppUid())
        };

        Command exec_command = new Command(listCommand);
        exec_command.execute(listCommand);
    }

    public static void removeCommandIntegrity(String[] command) {
        for (int i = 0; i < listProcessIntegrity.size(); i++) {
            if (Arrays.equals(listProcessIntegrity.get(i).getCommand(), command)) {
                listProcessIntegrity.remove(i);
                return;
            }
        }
    }

    /**
     * Launch every asynTask in listProcess
     * @param callback
     * @param appInfo
     */

    public static void launchVerification(Callback callback, SimplifiedPackageInfo appInfo) {

        //COUNT = 0;
        if (listProcessIntegrity.isEmpty()) {
            callback.OnTaskCompleted(appInfo);
        } else {
            for (int i = 0; i < listProcessIntegrity.size(); i++) {
                if (listProcessIntegrity.get(i).getStatus() != AsyncTask.Status.RUNNING) {
                    //if (COUNT < MAX_PROCESS_INTEGRITY && listProcessIntegrity.get(i).getStatus() == AsyncTask.Status.PENDING) {
                    listProcessIntegrity.get(i).execute(listProcessIntegrity.get(i).getCommand());
                    //COUNT += 1;
                }
            }
        }
    }


    /**
     * remove every possible folder where an app could have been unziped
     * @param unzipDirectoryName
     */
    public static void brutallyEndScanApp(String unzipDirectoryName) {

        String[] listCommand = new String[]{
                "cd /data/local",
                "rm -rRf " + pathToApkUnzipFolder + unzipDirectoryName + "_*"
        };

        Command exec_command = new Command(listCommand);
        exec_command.execute(listCommand);
    }


    /**
     * ugly method to get the exact name of *.SF and *.RSA
     * @param pathToMETAINF
     * @param callback
     */
    public static void getCertNames(String pathToMETAINF,Callback callback) {

        String[] listCommand = new String[]{
                "ls " + pathToMETAINF+"| grep \\\\.SF | cut -d \".\" -f1"
        };

        Command exec_command = new Command(callback, listCommand);
        exec_command.execute(listCommand);
    }
}
