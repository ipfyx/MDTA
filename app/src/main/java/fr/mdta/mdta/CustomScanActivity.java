package fr.mdta.mdta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import java.io.IOException;
import java.util.ArrayList;

import eu.chainfire.libsuperuser.Shell;
import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Scans.BlacklistedDevelopperScan;
import fr.mdta.mdta.Scans.CertificateScan;
import fr.mdta.mdta.Scans.DexScan;
import fr.mdta.mdta.Scans.IntegrityScan;
import fr.mdta.mdta.Scans.PermissionScan;
import fr.mdta.mdta.Tools.CacheStorage;
import fr.mdta.mdta.Tools.PackageInfoFactory;


public class CustomScanActivity extends AppCompatActivity {

    //Model
    private ArrayList<Scan> mScans = new ArrayList<>();
    private ArrayList<Scan> mScansWholeSystem = new ArrayList<>();
    private ArrayList<Scan> mScansApplications = new ArrayList<>();


    //UI components
    private Button mLaunchScanButton;
    private CheckBox mWholeSystemCheckbox;
    private CheckBox mDownloadedApplicationsCheckbox;
    private CustomScanAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customscan);

        //Retrieve UI components
        mLaunchScanButton = (Button) findViewById(R.id.launchCustomScan);
        mWholeSystemCheckbox = (CheckBox) findViewById(R.id.wholeSystemCheckbox);
        mDownloadedApplicationsCheckbox = (CheckBox) findViewById(R.id.applicationsCheckbox);
        mRecyclerView = (RecyclerView) findViewById(R.id.scansRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //TODO replace fake scan by the legitimate one

        mScansApplications.add(new PermissionScan(PackageInfoFactory.getInstalledPackages(this, false)));
        mScansApplications.add(new CertificateScan(PackageInfoFactory.getInstalledPackages(this, false)));
        mScansApplications.add(new BlacklistedDevelopperScan(PackageInfoFactory.getInstalledPackages(this, false)));
        if (Shell.SU.available()) {
            mScansApplications.add(new IntegrityScan(PackageInfoFactory.getInstalledPackages(this, false), this));
            mScansApplications.add(new DexScan(PackageInfoFactory.getInstalledPackages(this, false), this));
        }

        mScansWholeSystem.add(new PermissionScan(PackageInfoFactory.getInstalledPackages(this)));
        mScansWholeSystem.add(new CertificateScan(PackageInfoFactory.getInstalledPackages(this)));
        mScansWholeSystem.add(new BlacklistedDevelopperScan(PackageInfoFactory.getInstalledPackages(this)));
        if (Shell.SU.available()) {
            mScansWholeSystem.add(new IntegrityScan(PackageInfoFactory.getInstalledPackages(this), this));
            mScansWholeSystem.add(new DexScan(PackageInfoFactory.getInstalledPackages(this), this));
        }


        //CustomScan Interraction
        mWholeSystemCheckbox.setOnClickListener(new OnCheckboxClickedListener());
        mDownloadedApplicationsCheckbox.setOnClickListener(new OnCheckboxClickedListener());
        mLaunchScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScans.addAll(mAdapter.getmChoosenScans());
                if (mScans.size() > 0) {
                    Intent myIntent = new Intent(CustomScanActivity.this, ScanActivity.class);
                    myIntent.putExtra(ScanActivity.KEY_TYPE_OF_SCAN, ScanActivity.TypeOfScan.CUSTOMSCAN);
                    //write scans to cache
                    try {
                        CacheStorage.clearCache(getApplicationContext());
                        CacheStorage.writeObject(getApplicationContext(), ScanActivity.KEY_CUSTOM_SCAN_SCANLIST, mScans);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivity(myIntent);
                    finish();
                }
            }
        });

    }

    /**
     * This OnClickListener implements the logic to create the use the good scans according to the top checkbox
     */
    private class OnCheckboxClickedListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == mWholeSystemCheckbox && mWholeSystemCheckbox.isChecked()) {
                mDownloadedApplicationsCheckbox.setChecked(false);
                mAdapter = new CustomScanAdapter(getApplicationContext(), mScansWholeSystem);
                mRecyclerView.setAdapter(mAdapter);
            } else if (v == mWholeSystemCheckbox && !mWholeSystemCheckbox.isChecked()) {
                mDownloadedApplicationsCheckbox.setChecked(false);
                mAdapter = new CustomScanAdapter(getApplicationContext(), new ArrayList<Scan>());
                mRecyclerView.setAdapter(mAdapter);
            } else if (v == mDownloadedApplicationsCheckbox && mDownloadedApplicationsCheckbox.isChecked()) {
                mWholeSystemCheckbox.setChecked(false);
                mAdapter = new CustomScanAdapter(getApplicationContext(), mScansApplications);
                mRecyclerView.setAdapter(mAdapter);
            } else if (v == mDownloadedApplicationsCheckbox && !mDownloadedApplicationsCheckbox.isChecked()) {
                mWholeSystemCheckbox.setChecked(false);
                mAdapter = new CustomScanAdapter(getApplicationContext(), new ArrayList<Scan>());
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}
