package fr.mdta.mdta;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.mdta.mdta.Model.Result;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Scans.CertificateScan;
import fr.mdta.mdta.Scans.DexScan;
import fr.mdta.mdta.Scans.IntegrityScan;
import fr.mdta.mdta.Scans.PermissionScan;
import fr.mdta.mdta.Scans.Scan;
import fr.mdta.mdta.Tools.PackageInfoFactory;
import fr.mdta.mdta.Tools.ScanLauncher;


public class ScanActivity extends AppCompatActivity {

    //static values
    public final static String KEY_TYPE_OF_SCAN = "typeofscan";
    public final static String KEY_CUSTOM_SCAN_SCANLIST = "customscanscanlist";
    private final static String TIMER_FORMAT = "mm:ss";
    private static final int mProgressBarMaxValue = 100;
    private final ArrayList<Scan> mScans = new ArrayList<>();
    private final ArrayList<Result> mResults = new ArrayList<>();
    private Handler mHandler = new Handler();
    //Model
    private TypeOfScan mTypeOfScan;
    private int mCounter;
    private Date mStartingTime;
    private Handler mTimerHandler = new Handler();
    //UI components
    private TextView mTimerTextView;
    private TextView mPercentTextView;
    private Button mResultButton;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //Retrieve Extras
        mTypeOfScan = (TypeOfScan) getIntent().getSerializableExtra(KEY_TYPE_OF_SCAN);

        //Retrieve UI components
        mTimerTextView = (TextView) findViewById(R.id.timer);
        mResultButton = (Button) findViewById(R.id.resultButton);
        mPercentTextView = (TextView) findViewById(R.id.progessbarPercent);
        mProgressBar = (ProgressBar) findViewById(R.id.scanProgressBar);


        //Scan Timer Animation
        mStartingTime = new Date();
        final SimpleDateFormat df = new SimpleDateFormat(TIMER_FORMAT);
        long dif = (new Date()).getTime() - mStartingTime.getTime();
        Date difDate = new Date(dif);
        mTimerTextView.setText(df.format(difDate));
        mTimerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long dif = (new Date()).getTime() - mStartingTime.getTime();
                Date difDate = new Date(dif);
                mTimerTextView.setText(df.format(difDate));
                if (mCounter < mProgressBarMaxValue) {
                    mTimerHandler.postDelayed(this, 1000);
                }
            }
        }, 1000);

        //Progressbar animation
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setMax(mProgressBarMaxValue);
        mCounter = 0;
        mHandler.postDelayed(new

                                     Runnable() {

                                         public void run() {

                                             mCounter = ScanLauncher.getInstance().getScansGlobalState();
                                             //TODO replace fake animation by an update with scanlist and state arguments
                                             if (mCounter < mProgressBarMaxValue) {
                                                 mProgressBar.setProgress(mCounter);
                                                 int percent_num = (mCounter * 100 / mProgressBarMaxValue);
                                                 mPercentTextView.setText(percent_num + "%");
                                                 mHandler.postDelayed(this, 500);
                                             } else {
                                                 mPercentTextView.setText("100% ");
                                                 mProgressBar.setProgress(mProgressBarMaxValue);
                                             }
                                         }
                                     }, 500);

        //Scan preparation according to type of scan
        switch (mTypeOfScan) {
            case WHOLESYSTEMSCAN:
                //TODO add other scans
                mScans.add(new PermissionScan(PackageInfoFactory.getInstalledPackages(this)));
                mScans.add(new CertificateScan(PackageInfoFactory.getInstalledPackages(this)));
                mScans.add(new DexScan(PackageInfoFactory.getInstalledPackages(this),this));
                mScans.add(new IntegrityScan(PackageInfoFactory.getInstalledPackages(this),this));
                break;
            case APPLICATIONSSCAN:
                //TODO add other scans
                mScans.add(new PermissionScan(PackageInfoFactory.getInstalledPackages(this, false)));
                mScans.add(new CertificateScan(PackageInfoFactory.getInstalledPackages(this, false)));
                mScans.add(new DexScan(PackageInfoFactory.getInstalledPackages(this,false),this));
                mScans.add(new IntegrityScan(PackageInfoFactory.getInstalledPackages(this,false),this));
                break;
            case CUSTOMSCAN:
                //Retrieve scanlist from serializable extra
                mScans.addAll((ArrayList<Scan>) getIntent().getSerializableExtra(KEY_CUSTOM_SCAN_SCANLIST));
        }

        try {
            ScanLauncher.getInstance().launchScansParallel(mScans, new ScanLauncher.ScanLauncherCallback() {
                @Override
                public void OnScansTerminated(ArrayList<Scan> arrayListScanWithResult) {
                    fillResultList(arrayListScanWithResult);
                }
            });
        } catch (ScanLauncher.ScanLauncherException e) {
            e.printStackTrace();
        }

        //Set button action
        mResultButton.setClickable(false);
        mResultButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (mResults.size() > 0) {
                    Intent myIntent = new Intent(ScanActivity.this, ResultActivity.class);
                    myIntent.putExtra(ResultActivity.KEY_RESULT_LIST, mResults);
                    startActivity(myIntent);
                    finish();
                }
            }

        });
    }

    private void fillResultList(ArrayList<Scan> scanArrayList) {
        ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos = scanArrayList.get(0).getmSimplifiedPackageInfos();

        for (int i = 0; i < simplifiedPackageInfos.size(); i++) {
            ArrayList<Result.ScanResult> scanResults = new ArrayList<>();
            for (int j = 0; j < scanArrayList.size(); j++) {
                scanResults.add(scanArrayList.get(j).getScanResult(simplifiedPackageInfos.get(i)));
            }
            Result result = new Result(simplifiedPackageInfos.get(i), scanResults);
            mResults.add(result);
        }
        mResultButton.setClickable(true);
    }

    /**
     * Enum to represents every case which allows us to go though this activity
     */
    public enum TypeOfScan {
        WHOLESYSTEMSCAN,
        APPLICATIONSSCAN,
        CUSTOMSCAN
    }
}
