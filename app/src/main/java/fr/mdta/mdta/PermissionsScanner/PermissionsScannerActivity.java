package fr.mdta.mdta.PermissionsScanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.MalformedURLException;

import fr.mdta.mdta.API.APIModel.ReceivedItem.BasicScanResultItem;
import fr.mdta.mdta.API.APIModel.SentItem.PackagesList;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.BasicScanRequester;
import fr.mdta.mdta.R;

public class PermissionsScannerActivity extends AppCompatActivity implements Callback {

    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_scanner);

        mResultTextView = (TextView) findViewById(R.id.displayPermissionScanner);

        final Button launchButton = (Button) findViewById(R.id.launchButton);
        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchButton.setClickable(false);
                PackagesList packagesList = new PackagesList(PackageInfoFactory.getInstalledPackages(getApplicationContext()));

                try {
                    BasicScanRequester request = new BasicScanRequester(PermissionsScannerActivity.this.getApplicationContext(),
                            false, PermissionsScannerActivity.this, packagesList);
                    request.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void OnErrorHappended() {

    }

    @Override
    public void OnErrorHappended(String error) {

    }

    @Override
    public void OnTaskCompleted(Object object) {
        Log.d("result", object.toString());
        mResultTextView.setText(((BasicScanResultItem) object).toString());
    }
}
