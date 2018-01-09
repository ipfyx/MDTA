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

    public static Command execCommand (String[] command, Callback callback, Context context) {
        Command exec_command = new Command(callback, context, command);
        exec_command.execute(command);
        listProcess.add(exec_command);
        return exec_command;
    }

    public static void addCommandToExecute (String[] command, Callback callback, Context context) {
        Command exec_command = new Command(callback, context, command);
        listProcess.add(exec_command);
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

}
