package fr.mdta.mdta.SignaturesScanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import fr.mdta.mdta.API.APIModel.SentItem.DeveloperSignature;
import fr.mdta.mdta.API.APIModel.SentItem.DeveloperSignatureList;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.BlacklistDeveloperSignatureRequester;
import fr.mdta.mdta.API.Requester.DeveloperSignatureScanRequester;
import fr.mdta.mdta.R;
import fr.mdta.mdta.SignaturesScanner.Model.PackageSignaturesInfo;

public class SignaturesScannerActivity extends AppCompatActivity {

    private TextView mResultTextView;

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
                    final ArrayList<PackageSignaturesInfo> result = SignaturesInfoFactory.getInstalledPackages(SignaturesScannerActivity.this);
                    //From there you can have access to the good object to make something with signature

                    BlacklistDeveloperSignatureRequester blacklister = new BlacklistDeveloperSignatureRequester(getApplicationContext(), false, new Callback() {
                        @Override
                        public void OnErrorHappended() {
                            Log.d("error", "without message");
                        }

                        @Override
                        public void OnErrorHappended(String error) {
                            Log.d("error", "without message");
                        }

                        @Override
                        public void OnTaskCompleted(Object object) {
                            ArrayList<DeveloperSignatureList.DeveloperSignatureListElement> list = new ArrayList<DeveloperSignatureList.DeveloperSignatureListElement>();
                            for (int i = 0; i < 10; i++) {
                                DeveloperSignatureList.DeveloperSignatureListElement developerSignatureListElement = new DeveloperSignatureList.DeveloperSignatureListElement(result.get(i).getmPackageName(),
                                        result.get(i).getmAppDeveloperCertificate().getPublicKey().getAlgorithm(),
                                        result.get(i).getmAppDeveloperBase64Key());
                                list.add(developerSignatureListElement);
                            }

                            DeveloperSignatureList testlist = new DeveloperSignatureList(list);
                            DeveloperSignatureScanRequester test = null;
                            try {
                                test = new DeveloperSignatureScanRequester(getApplicationContext(), false, new Callback() {
                                    @Override
                                    public void OnErrorHappended() {
                                        Log.d("error", "without message");
                                    }

                                    @Override
                                    public void OnErrorHappended(String error) {
                                        Log.d("error", "without message");
                                    }

                                    @Override
                                    public void OnTaskCompleted(Object object) {

                                    }
                                }, testlist);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            test.execute();
                        }
                    }, new DeveloperSignature(result.get(0).getmPackageName(),
                            result.get(0).getmAppDeveloperCertificate().getPublicKey().getAlgorithm(),
                            result.get(0).getmAppDeveloperBase64Key()));
                    blacklister.execute();







                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
