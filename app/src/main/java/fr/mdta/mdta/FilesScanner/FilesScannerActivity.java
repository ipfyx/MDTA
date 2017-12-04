package fr.mdta.mdta.FilesScanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import eu.chainfire.libsuperuser.Shell;
import fr.mdta.mdta.FilesScanner.CommandFactory;

import fr.mdta.mdta.R;

public class FilesScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_scanner);

        boolean suAvailable = Shell.SU.available();

        /**
         * https://stackoverflow.com/questions/2634991/android-1-6-android-view-windowmanagerbadtokenexception-unable-to-add-window
         */
        if ( suAvailable ) {
            CommandFactory.execCommand("ls",this);
        }
        else {
            TextView tv = (TextView) findViewById(R.id.sample_text);
            tv.setText("Non rooted phone");
        }
    }

    protected String getSuVersion() {
        return Shell.SU.version(false);
    }

    protected String getSuVersionInternal() {
        return Shell.SU.version(true);
    }
}
