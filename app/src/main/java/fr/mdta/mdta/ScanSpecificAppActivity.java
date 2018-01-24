package fr.mdta.mdta;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import eu.chainfire.libsuperuser.Shell;
import fr.mdta.mdta.Model.Result;
import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Scans.BlacklistedCertificateScan;
import fr.mdta.mdta.Scans.BlacklistedDeveloperScan;
import fr.mdta.mdta.Scans.CertificateScan;
import fr.mdta.mdta.Scans.DexScan;
import fr.mdta.mdta.Scans.IntegrityScan;
import fr.mdta.mdta.Scans.PermissionScan;
import fr.mdta.mdta.Tools.ScanLauncher;


public class ScanSpecificAppActivity extends AppCompatActivity {

    //static value
    public final static String CURRENT_SIMPLIFIED_APP_PACKAGE_INFO = "currentsimplifiedpackageinfo";
    private final static String APPLICATION_DATE_FORMAT = "MM/dd/yyyy HH:mm";
    private final static String TIMER_FORMAT = "mm:ss";


    //Model
    private SimplifiedPackageInfo mSimplifiedPackageInfo;
    private ArrayList<Scan> mScans = new ArrayList<>();
    private Result result;
    private Date mStartingTime;
    private Handler mHandler = new Handler();


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
    private TextView mTimerTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanspecificapp);

        //Retrieve serializable extra
        mSimplifiedPackageInfo = (SimplifiedPackageInfo) getIntent().getSerializableExtra
                (CURRENT_SIMPLIFIED_APP_PACKAGE_INFO);

        //Retrieve UI components
        mIconImageView = (ImageView) findViewById(R.id.icon);
        mTimerTextView = (TextView) findViewById(R.id.timer);
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


        //Progressbar and Scan Timer animation
        mStartingTime = new Date();
        final SimpleDateFormat tf = new SimpleDateFormat(TIMER_FORMAT);
        long dif = (new Date()).getTime() - mStartingTime.getTime();
        Date difDate = new Date(dif);
        mTimerTextView.setText(tf.format(difDate));
        mHandler.postDelayed(new Runnable() {

            public void run() {

                if (result == null) {
                    long dif = (new Date()).getTime() - mStartingTime.getTime();
                    Date difDate = new Date(dif);
                    mTimerTextView.setText(tf.format(difDate));
                    mHandler.postDelayed(this, 50);
                }
            }
        }, 50);

        //Fill application data

        SimpleDateFormat df = new SimpleDateFormat(APPLICATION_DATE_FORMAT);
        try

        {
            Drawable icon = getPackageManager().getApplicationIcon(mSimplifiedPackageInfo
                    .getPackageName());
            mIconImageView.setImageDrawable(icon);
            mAppNameTextView.setText(mSimplifiedPackageInfo.getAppName());
            mPackageNameTextView.setText(mSimplifiedPackageInfo.getPackageName());
            mVersionNameTextView.setText(getResources().getString(R.string.title_version_name) +
                    mSimplifiedPackageInfo.getVersionName());
            mVersioncodeTextView.setText(getResources().getString(R.string.title_version_code) +
                    Integer.toString(mSimplifiedPackageInfo.getVersionCode()));
            mFirstInstallTimeTextView.setText(getResources().getString(R.string
                    .title_installing_date) + df.format(new Date(mSimplifiedPackageInfo
                    .getFirstInstallTime())));
            mLastUpdateTimeTextView.setText(getResources().getString(R.string.title_last_update)
                    + df.format(new Date(mSimplifiedPackageInfo.getLastUpdateTime())));
            mPermissionsNumberTextView.setText(getResources().getString(R.string
                    .title_permissions_number) + Integer.toString(mSimplifiedPackageInfo
                    .getPermissions().size()));
            mIsApplicationSystemTextView.setText(getResources().getString(R.string
                    .title_is_system_app) + Boolean.toString(mSimplifiedPackageInfo.isSystemApp()));
        } catch (
                PackageManager.NameNotFoundException e)

        {
            e.printStackTrace();
        }

        //TODO add other scans
        ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos = new ArrayList<>();
        simplifiedPackageInfos.add(mSimplifiedPackageInfo);
        mScans.add(new

                PermissionScan(simplifiedPackageInfos));
        mScans.add(new

                CertificateScan(simplifiedPackageInfos));
        mScans.add(new

                BlacklistedDeveloperScan(simplifiedPackageInfos));
        mScans.add(new

                BlacklistedCertificateScan(simplifiedPackageInfos));
        if (Shell.SU.available())

        {
            //mScans.add(new IntegrityScan(simplifiedPackageInfos, this));
            mScans.add(new DexScan(simplifiedPackageInfos, this));
        }
        try

        {
            ScanLauncher.getInstance().launchScansParallel(mScans, new ScanLauncher
                    .ScanLauncherCallback() {
                @Override
                public void OnScansTerminated(ArrayList<Scan> arrayListScanWithResult) {
                    ArrayList<Result.ScanResult> scanResults = new ArrayList<Result.ScanResult>();
                    for (int i = 0; i < arrayListScanWithResult.size(); i++) {
                        scanResults.add(arrayListScanWithResult.get(i).getScanResult
                                (mSimplifiedPackageInfo));
                    }
                    result = new Result(mSimplifiedPackageInfo, scanResults);
                    mResultButton.setClickable(true);
                }
            });
        } catch (
                ScanLauncher.ScanLauncherException e)

        {
            e.printStackTrace();
        }

        //Fill adapter with values
        mAdapter = new

                ScanSpecificAppAdapter(this, mScans);
        mRecyclerView.setLayoutManager(new

                LinearLayoutManager(this, LinearLayoutManager
                .VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        //Access result interaction
        mResultButton.setClickable(false);
        mResultButton.setOnClickListener(
                new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        if (result != null) {
                            Intent myIntent = new Intent(ScanSpecificAppActivity.this,
                                    ResultSpecificAppActivity.class);
                            myIntent.putExtra(ResultSpecificAppActivity.KEY_RESULT, result);
                            startActivity(myIntent);
                            finish();
                        }
                    }
                }
        );
    }
}
