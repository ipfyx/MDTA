package fr.mdta.mdta.SignaturesScanner;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import fr.mdta.mdta.R;
import fr.mdta.mdta.SignaturesScanner.Model.PackageSignaturesInfo;

public class SignaturesScannerActivity extends AppCompatActivity {

    private TextView mResultTextView;

    private ArrayList<PackageSignaturesInfo> installedApplications = new ArrayList<PackageSignaturesInfo>();
    private List<PackageSignaturesInfo> systemApps = new ArrayList<PackageSignaturesInfo>();
    private List<PackageSignaturesInfo> nonSystemApps = new ArrayList<PackageSignaturesInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signatures_scanner);

        mResultTextView = (TextView) findViewById(R.id.displaySignatureScanner);

        final Button launchButton = (Button) findViewById(R.id.launchButton);

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    installedApplications = SignaturesInfoFactory.getInstalledPackages(SignaturesScannerActivity.this);

                    //From there you can have access to the good object to make something with signature
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
