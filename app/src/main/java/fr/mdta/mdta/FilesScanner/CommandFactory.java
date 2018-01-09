package fr.mdta.mdta.FilesScanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import fr.mdta.mdta.API.Callback.Callback;

/**
 * Created by manwefm on 04/12/17.
 */

public final class CommandFactory {

    public static ArrayList<Command> listProcess = new ArrayList<Command>();

    static final int MAX_PROCESS = 5;

    static int COUNT = 0;

    public static void execCommand (String[] command, Callback callback, Context context) {
        Command exec_command = new Command(callback, context, command);
        exec_command.execute(command);
    }

    public static void addCommandToExecute (final String[] command, Context context, Callback callback) {
        Command exec_command = new Command(callback, context, command);
        listProcess.add(exec_command);
        Log.d("size",Integer.toString(listProcess.size()));
        Log.d("command",exec_command.getCommand()[0]);
    }

    public static void removeCommand(String[] command) {
        for ( int i =0; i < listProcess.size(); i++) {
            if ( listProcess.get(i).getCommand().equals(command) ) {
                listProcess.remove(i);
                Log.d("CommandFactoryremoved",command[0]);
                return;
            }
        }
    }

    public static void cancelCommand(String[] command) {
        for ( int i =0; i < listProcess.size(); i++) {
            if ( listProcess.get(i).getCommand().equals(command) ) {
                listProcess.get(i).cancel(true);
                return;
            }
        }
    }

    public static void addCommand(Command command) {
        listProcess.add(command);
    }

    public static void launchVerification() {

        COUNT = 0;
        for (int i = 0; i < listProcess.size(); i++ ) {
            System.out.println(listProcess.get(i).getCommand()[0]);
            System.out.println(listProcess.get(i).getStatus());
            Log.d(Integer.toString(i),Integer.toString(COUNT));
            if ( COUNT < MAX_PROCESS && listProcess.get(i).getStatus() == AsyncTask.Status.PENDING ) {
                listProcess.get(i).execute(listProcess.get(i).getCommand());
                COUNT+=1;
            }else {
                Log.d("MAX_PROCESS","reached");
                return;
            }
        }
    }

}
