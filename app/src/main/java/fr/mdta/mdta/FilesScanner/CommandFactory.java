package fr.mdta.mdta.FilesScanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import fr.mdta.mdta.API.Callback.Callback;

/**
 * Created by manwefm on 04/12/17.
 */

public final class CommandFactory {

    public static void execCommand (String command, Callback callback, Context context) {
        Command exec_command = new Command();
        exec_command.setCallback(callback);
        exec_command.setContext(context);
        exec_command.execute(command);
    }

}
