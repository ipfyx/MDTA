package fr.mdta.mdta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.mdta.mdta.FilesScanner.FilesScannerActivity;
import fr.mdta.mdta.PermissionsScanner.PermissionsScannerActivity;
import fr.mdta.mdta.SignaturesScanner.SignaturesScannerActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        Button button = (Button) findViewById(R.id.permissionsScanner);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PermissionsScannerActivity.class);
                startActivity(i);
            }
        });

        Button buttonFilesScanner = (Button) findViewById(R.id.filesScanner);
        buttonFilesScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FilesScannerActivity.class);
                startActivity(i);
            }
        });

        Button signatureButton = (Button) findViewById(R.id.signaturesScanner);
        signatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignaturesScannerActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
