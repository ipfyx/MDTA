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
        CommandFactory.execCommand("ls");

    }
}
