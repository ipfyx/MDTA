package fr.mdta.mdta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //UI components
    private Button mScanApplicationsButton;
    private Button mScanAllSystemButton;
    private Button mScanSpecificAppButton;
    private Button mCustomScanButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Retrieve UI components and set every related click option
        mScanApplicationsButton = (Button) findViewById(R.id.scanApplications);
        mScanApplicationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ScanActivity.class);
                myIntent.putExtra(ScanActivity.KEY_TYPE_OF_SCAN, ScanActivity.TypeOfScan.APPLICATIONSSCAN);
                startActivity(myIntent);
                finish();
            }
        });

        mScanAllSystemButton = (Button) findViewById(R.id.scanAllSystem);
        mScanAllSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ScanActivity.class);
                myIntent.putExtra(ScanActivity.KEY_TYPE_OF_SCAN, ScanActivity.TypeOfScan.WHOLESYSTEMSCAN);
                startActivity(myIntent);
                finish();
            }
        });

        mScanSpecificAppButton = (Button) findViewById(R.id.scanSpecificApp);
        mScanSpecificAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, SpecifyAppActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        mCustomScanButton = (Button) findViewById(R.id.customScanButton);
        mCustomScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CustomScanActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

    }


}
