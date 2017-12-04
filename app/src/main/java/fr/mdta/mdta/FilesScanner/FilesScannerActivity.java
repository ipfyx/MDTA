package fr.mdta.mdta.FilesScanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import fr.mdta.mdta.FilesScanner.CommandFactory;

import fr.mdta.mdta.R;

public class FilesScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_scanner);
        /**
         * https://stackoverflow.com/questions/2634991/android-1-6-android-view-windowmanagerbadtokenexception-unable-to-add-window
         */
        CommandFactory.execCommand("ls",this);
    }
}
