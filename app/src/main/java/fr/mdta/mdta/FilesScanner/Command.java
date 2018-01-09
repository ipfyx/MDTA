package fr.mdta.mdta.FilesScanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.telecom.Call;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.R;

/**
 * Created by manwefm on 04/12/17.
 */

/**
 * https://stackoverflow.com/questions/11451768/call-asynctask-from-static-class
 */
class Command extends AsyncTask<String, Void, String> {
    private ProgressDialog dialog = null;
    private Callback callback;
    private Context context;
    private boolean suAvailable = false;
    private List<String> suResult = null;
    private String[] command = null;

    public Command() {}

    public Command(Callback callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    public Command(Callback callback, Context context, String[] command) {
        this.callback = callback;
        this.context = context;
        this.command = command;
    }

    public Command(Context context, String[] command) {
        this.context = context;
        this.command = command;
    }

    protected void setCallback(Callback callback) {
        this.callback = callback;
    }

    protected void setContext(Context context) {
        this.context = context;
    }

    protected List<String> getSuResult(){
        return suResult;
    }

    protected String[] getCommand() {
        return command;
    }

    @Override
    protected void onPreExecute() {
        // We're creating a progress dialog here because we want the user to wait.
        // If in your app your user can just continue on with clicking other things,
        // don't do the dialog thing.

/*        dialog = new ProgressDialog(context);
        dialog.setTitle("Executing shell command");
        dialog.setMessage("Please wait");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();*/
    }

    @Override
    protected String doInBackground(String... params) {
        // Let's do some SU stuff
        suAvailable = Shell.SU.available();
        if (suAvailable) {
            suResult = Shell.SU.run(params);
        }
        return suResult.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        //dialog.dismiss();

        StringBuilder sb = (new StringBuilder()).append((char) 10);
        if (suResult != null) {
            for (String line : suResult) {
                sb.append(line).append((char) 10);
            }
        }
        else {
            sb.append("Error");
        }
        callback.OnTaskCompleted(sb.toString());

        //Log.d("CommandFactory", sb.toString());
    }
}
