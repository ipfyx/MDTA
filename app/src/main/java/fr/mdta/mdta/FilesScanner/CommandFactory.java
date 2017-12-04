package fr.mdta.mdta.FilesScanner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by manwefm on 04/12/17.
 */

public final class CommandFactory {

    public static void execCommand (String command) {
        Command ls = new Command();
        ls.execute(command);
    }

    private static class Command extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog = null;
        private Context context = null;
        private boolean suAvailable = false;
        private String suVersion = null;
        private String suVersionInternal = null;
        private List<String> suResult = null;

        public Command setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        protected void onPreExecute() {
            // We're creating a progress dialog here because we want the user to wait.
            // If in your app your user can just continue on with clicking other things,
            // don't do the dialog thing.

            //dialog = new ProgressDialog(context);
            //dialog.setTitle("Some title");
            //dialog.setMessage("Doing something interesting ...");
            //dialog.setIndeterminate(true);
            //dialog.setCancelable(false);
            //dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            // Let's do some SU stuff
            suAvailable = Shell.SU.available();
            if (suAvailable) {
                suVersion = Shell.SU.version(false);
                suVersionInternal = Shell.SU.version(true);
                suResult = Shell.SU.run(new String[]{
                        params[0],
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //dialog.dismiss();

            // output
            StringBuilder sb = (new StringBuilder()).
                    append("Root? ").append(suAvailable ? "Yes" : "No").append((char) 10).
                    append("Version: ").append(suVersion == null ? "N/A" : suVersion).append((char) 10).
                    append("Version (internal): ").append(suVersionInternal == null ? "N/A" : suVersionInternal).append((char) 10).
                    append((char) 10);
            if (suResult != null) {
                for (String line : suResult) {
                    sb.append(line).append((char) 10);
                }
            }
            Log.d("CommandFactory", sb.toString());
        }
    }

}
