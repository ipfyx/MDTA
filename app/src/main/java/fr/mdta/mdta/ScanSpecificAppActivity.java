package fr.mdta.mdta;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.mdta.mdta.Model.Result;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Scans.CertificateScan;
import fr.mdta.mdta.Scans.PermissionScan;
import fr.mdta.mdta.Scans.Scan;
import fr.mdta.mdta.Tools.ScanLauncher;


public class ScanSpecificAppActivity extends AppCompatActivity {

    //static value
    public final static String CURRENT_SIMPLIFIED_APP_PACKAGE_INFO = "currentsimplifiedpackageinfo";
    private final static String APPLICATION_DATE_FORMAT = "MM/dd/yyyy HH:mm";

    //Model
    private SimplifiedPackageInfo mSimplifiedPackageInfo;
    private ArrayList<Scan> mScans = new ArrayList<>();
    private Result result;

    //UI components
    private ImageView mIconImageView;
    private TextView mAppNameTextView;
    private TextView mPackageNameTextView;
    private TextView mVersionNameTextView;
    private TextView mVersioncodeTextView;
    private TextView mFirstInstallTimeTextView;
    private TextView mLastUpdateTimeTextView;
    private TextView mPermissionsNumberTextView;
    private TextView mIsApplicationSystemTextView;
    private RecyclerView mRecyclerView;
    private Button mResultButton;
    private ScanSpecificAppAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanspecificapp);

        //Retrieve serializable extra
        mSimplifiedPackageInfo = (SimplifiedPackageInfo) getIntent().getSerializableExtra(CURRENT_SIMPLIFIED_APP_PACKAGE_INFO);

        //Retrieve UI components
        mIconImageView = (ImageView) findViewById(R.id.icon);
        mAppNameTextView = (TextView) findViewById(R.id.appName);
        mPackageNameTextView = (TextView) findViewById(R.id.packageName);
        mVersionNameTextView = (TextView) findViewById(R.id.versionName);
        mVersioncodeTextView = (TextView) findViewById(R.id.versionCode);
        mFirstInstallTimeTextView = (TextView) findViewById(R.id.firstInstallTime);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.lastUpdateTime);
        mPermissionsNumberTextView = (TextView) findViewById(R.id.permissionsNumber);
        mIsApplicationSystemTextView = (TextView) findViewById(R.id.isApplicationSystem);
        mRecyclerView = (RecyclerView) findViewById(R.id.scansRecyclerView);
        mResultButton = (Button) findViewById(R.id.accessResult);

        //Fill application data

        SimpleDateFormat df = new SimpleDateFormat(APPLICATION_DATE_FORMAT);
        try {
            Drawable icon = getPackageManager().getApplicationIcon(mSimplifiedPackageInfo.getPackageName());
            mIconImageView.setImageDrawable(icon);
            mAppNameTextView.setText(mSimplifiedPackageInfo.getAppName());
            mPackageNameTextView.setText(mSimplifiedPackageInfo.getPackageName());
            mVersionNameTextView.setText(getResources().getString(R.string.title_version_name) + mSimplifiedPackageInfo.getVersionName());
            mVersioncodeTextView.setText(getResources().getString(R.string.title_version_code) + Integer.toString(mSimplifiedPackageInfo.getVersionCode()));
            mFirstInstallTimeTextView.setText(getResources().getString(R.string.title_installing_date) + df.format(new Date(mSimplifiedPackageInfo.getFirstInstallTime())));
            mLastUpdateTimeTextView.setText(getResources().getString(R.string.title_last_update) + df.format(new Date(mSimplifiedPackageInfo.getLastUpdateTime())));
            mPermissionsNumberTextView.setText(getResources().getString(R.string.title_permissions_number) + Integer.toString(mSimplifiedPackageInfo.getPermissions().size()));
            mIsApplicationSystemTextView.setText(getResources().getString(R.string.title_is_system_app) + Boolean.toString(mSimplifiedPackageInfo.isSystemApp()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //TODO add other scans
        ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos = new ArrayList<>();
        simplifiedPackageInfos.add(mSimplifiedPackageInfo);
        mScans.add(new PermissionScan(simplifiedPackageInfos));
        mScans.add(new CertificateScan(simplifiedPackageInfos));
        try {
            ScanLauncher.getInstance().launchScansSerial(mScans, new ScanLauncher.ScanLauncherCallback() {
                @Override
                public void OnScansTerminated(ArrayList<Scan> arrayListScanWithResult) {
                    ArrayList<Result.ScanResult> scanResults = new ArrayList<Result.ScanResult>();
                    for (int i = 0; i < arrayListScanWithResult.size(); i++) {
                        scanResults.add(arrayListScanWithResult.get(i).getScanResult(mSimplifiedPackageInfo));
                    }
                    result = new Result(mSimplifiedPackageInfo, scanResults);
                }
            });
        } catch (ScanLauncher.ScanLauncherException e) {
            e.printStackTrace();
        }

        //Fill adapter with values
        mAdapter = new ScanSpecificAppAdapter(this, mScans);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        //Access result interraction
        mResultButton.setClickable(true);
        mResultButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(ScanSpecificAppActivity.this, ResultSpecificAppActivity.class);
                        myIntent.putExtra(ResultSpecificAppActivity.KEY_RESULT, result);
                        startActivity(myIntent);
                        finish();
                    }
                }
        );
    }
}
