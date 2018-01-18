package fr.mdta.mdta;

import android.content.Intent;
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
import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
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
    private ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos = new ArrayList<>();
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
                                             //TODO replace fake animation by an update with scanlist and state arguments
                                             if (mCounter < mProgressBarMaxValue) {
                                                 mProgressBar.setProgress(mCounter);
                                                 int percent_num = (mCounter * 100 / mProgressBarMaxValue);
                                                 mPercentTextView.setText(percent_num + "%");
                                                 mHandler.postDelayed(this, 20);
                                             } else {
                                                 mPercentTextView.setText("100% ");
                                             }
                                         }
                                     }, 20);

        //Scan preparation according to type of scan
        switch (mTypeOfScan) {
            case WHOLESYSTEMSCAN:
                simplifiedPackageInfos = PackageInfoFactory.getInstalledPackages(this);
                //TODO replace fake scan by the legitimate one
                /**
                 * FAKE VALUES to proof the UI interface
                 */
                mScans.add(new Scan("permissionscanner", "descriptionperm", PackageInfoFactory.getInstalledPackages(this)) {

                    @Override
                    public void launchScan(ScanCallback callback) {

                    }

                    @Override
                    public void cancelScan(ScanCallback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                mScans.add(new Scan("integrityscanner", "descriptionintegrity", PackageInfoFactory.getInstalledPackages(this)) {

                    @Override
                    public void launchScan(ScanCallback callback) {

                    }

                    @Override
                    public void cancelScan(ScanCallback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                mScans.add(new Scan("signaturescanner", "descriptionsignature", PackageInfoFactory.getInstalledPackages(this)) {
                    @Override
                    public void launchScan(ScanCallback callback) {

                    }

                    @Override
                    public void cancelScan(ScanCallback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                /**
                 * END FAKE VALUES to proof the UI interface
                 */
                break;
            case APPLICATIONSSCAN:
                simplifiedPackageInfos = PackageInfoFactory.getInstalledPackages(this, false);
                //TODO replace fake scan by the legitimate one
                /**
                 * FAKE VALUES to proof the UI interface
                 */
                mScans.add(new Scan("permissionscanner", "descriptionperm", PackageInfoFactory.getInstalledPackages(this)) {
                    @Override
                    public void launchScan(ScanCallback callback) {

                    }

                    @Override
                    public void cancelScan(ScanCallback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                mScans.add(new Scan("integrityscanner", "descriptionintegrity", PackageInfoFactory.getInstalledPackages(this)) {
                    @Override
                    public void launchScan(ScanCallback callback) {

                    }

                    @Override
                    public void cancelScan(ScanCallback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                mScans.add(new Scan("signaturescanner", "descriptionsignature", PackageInfoFactory.getInstalledPackages(this)) {
                    @Override
                    public void launchScan(ScanCallback callback) {

                    }

                    @Override
                    public void cancelScan(ScanCallback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                /**
                 * END FAKE VALUES to proof the UI interface
                 */
                break;
            case CUSTOMSCAN:
                //Retrieve scanlist from serializable extra
                mScans.addAll((ArrayList<Scan>) getIntent().getSerializableExtra(KEY_CUSTOM_SCAN_SCANLIST));
                try {
                    ScanLauncher.getInstance().launchScansSerial(mScans, new ScanLauncher.ScanLauncherCallback() {
                        @Override
                        public void OnScansTerminated(ArrayList<Scan> arrayListScanWithResult) {
                            updateCounter();
                            fillResultList(arrayListScanWithResult);
                        }
                    });
                } catch (ScanLauncher.ScanLauncherException e) {
                    e.printStackTrace();
                }
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
        simplifiedPackageInfos = scanArrayList.get(0).getmSimplifiedPackageInfos();

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
     * Method to combine all the scans state counter to update
     */
    private void updateCounter() {
        //TODO by mixing scans
        mCounter = 100;
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
