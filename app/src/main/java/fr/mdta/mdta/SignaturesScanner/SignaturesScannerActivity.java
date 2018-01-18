package fr.mdta.mdta.SignaturesScanner;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
                    getListSystemApps();
                    getListNonSystemApps();

                    for (int i = 0; i < installedApplications.size(); i++) {
                        if (!SignaturesInfoFactory.verifyCertificat(installedApplications.get(i))) {
                            warnUser(installedApplications.get(i));
                        }
                    }

                    //From there you can have access to the good object to make something with signature
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void getListSystemApps() {
        for (int i = 0; i < installedApplications.size(); i++) {
            if ((installedApplications.get(i).getmFlag() & ApplicationInfo.FLAG_SYSTEM) != 0) {
                systemApps.add(installedApplications.get(i));
            }
        }
    }

    protected void getListNonSystemApps() {
        for (int i = 0; i < installedApplications.size(); i++) {
            if ((installedApplications.get(i).getmFlag() & ApplicationInfo.FLAG_SYSTEM) == 0) {
                nonSystemApps.add(installedApplications.get(i));
            }
        }
    }

    public void warnUser(PackageSignaturesInfo pi) {
        //TODO
        Log.d(pi.getmAppName(), "InvalidCertificat");
    }
}
