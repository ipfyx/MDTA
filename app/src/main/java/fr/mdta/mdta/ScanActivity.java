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

import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.Model.Result;
import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Tools.PackageInfoFactory;


public class ScanActivity extends AppCompatActivity {

    //static values
    public final static String KEY_TYPE_OF_SCAN = "typeofscan";
    public final static String KEY_CUSTOM_SCAN_SCANLIST = "customscanscanlist";
    private final static String TIMER_FORMAT = "mm:ss";
    private static final int mProgressBarMaxValue = 100;
    private Handler mHandler = new Handler();
    //Model
    private TypeOfScan mTypeOfScan;
    private int mCounter;
    private Date mStartingTime;
    private ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos = new ArrayList<>();
    private Handler mTimerHandler = new Handler();
    private final ArrayList<Scan> mScans = new ArrayList<>();
    private final ArrayList<Result> mResults = new ArrayList<>();
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
                                             mCounter++;
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
                    public void launchScan(Callback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                mScans.add(new Scan("integrityscanner", "descriptionintegrity", PackageInfoFactory.getInstalledPackages(this)) {
                    @Override
                    public void launchScan(Callback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                mScans.add(new Scan("signaturescanner", "descriptionsignature", PackageInfoFactory.getInstalledPackages(this)) {
                    @Override
                    public void launchScan(Callback callback) {

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
                    public void launchScan(Callback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                mScans.add(new Scan("integrityscanner", "descriptionintegrity", PackageInfoFactory.getInstalledPackages(this)) {
                    @Override
                    public void launchScan(Callback callback) {

                    }

                    @Override
                    public void updateState() {

                    }
                });
                mScans.add(new Scan("signaturescanner", "descriptionsignature", PackageInfoFactory.getInstalledPackages(this)) {
                    @Override
                    public void launchScan(Callback callback) {

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
        }


        /**
         * FAKE VALUES to proof the UI interface
         */
        simplifiedPackageInfos = mScans.get(0).getmSimplifiedPackageInfos();
        ArrayList<Result.ScanResult> scanResults0 = new ArrayList<>();
        Result.ScanResult scanResult01 = new Result.ScanResult("integrityscan", "lescanquitueetquiprenddeuxplombes", new Scan.SpecificResult(true, "impeccable", "RAS"));
        scanResults0.add(scanResult01);
        Result.ScanResult scanResult02 = new Result.ScanResult("dexfilescanner", "lescanquitueetquiprenduneplombes", new Scan.SpecificResult(true, "impeccable", "RAS"));
        scanResults0.add(scanResult02);
        Result.ScanResult scanResult03 = new Result.ScanResult("permissionscan", "superrapide", new Scan.SpecificResult(true, "impeccable", "RAS"));
        scanResults0.add(scanResult03);
        Result result0 = new Result(simplifiedPackageInfos.get(0), scanResults0);
        mResults.add(result0);

        ArrayList<Result.ScanResult> scanResults1 = new ArrayList<>();
        Result.ScanResult scanResult11 = new Result.ScanResult("integrityscan", "lescanquitueetquiprenddeuxplombes", new Scan.SpecificResult(false, "impeccable", "RAS"));
        scanResults1.add(scanResult11);
        Result.ScanResult scanResult12 = new Result.ScanResult("dexfilescanner", "lescanquitueetquiprenduneplombes", new Scan.SpecificResult(false, "impeccable", "RAS"));
        scanResults1.add(scanResult12);
        Result.ScanResult scanResult13 = new Result.ScanResult("permissionscan", "superrapide", new Scan.SpecificResult(false, "impeccable", "RAS"));
        scanResults1.add(scanResult13);
        Result result1 = new Result(simplifiedPackageInfos.get(1), scanResults1);
        mResults.add(result1);

        ArrayList<Result.ScanResult> scanResults2 = new ArrayList<>();
        Result.ScanResult scanResult21 = new Result.ScanResult("integrityscan", "lescanquitueetquiprenddeuxplombes", new Scan.SpecificResult(false, "impeccable", "RAS"));
        scanResults2.add(scanResult21);
        Result.ScanResult scanResult22 = new Result.ScanResult("dexfilescanner", "lescanquitueetquiprenduneplombes", new Scan.SpecificResult(true, "impeccable", "RAS"));
        scanResults2.add(scanResult22);
        Result.ScanResult scanResult23 = new Result.ScanResult("permissionscan", "superrapide", new Scan.SpecificResult(true, "impeccable", "RAS"));
        scanResults2.add(scanResult23);
        Result result2 = new Result(simplifiedPackageInfos.get(2), scanResults2);
        mResults.add(result2);

        ArrayList<Result.ScanResult> scanResults3 = new ArrayList<>();
        Result.ScanResult scanResult31 = new Result.ScanResult("integrityscan", "lescanquitueetquiprenddeuxplombes", new Scan.SpecificResult(true, "impeccable", "RAS"));
        scanResults3.add(scanResult31);
        Result.ScanResult scanResult32 = new Result.ScanResult("dexfilescanner", "lescanquitueetquiprenduneplombes", new Scan.SpecificResult(false, "impeccable", "RAS"));
        scanResults3.add(scanResult32);
        Result.ScanResult scanResult33 = new Result.ScanResult("permissionscan", "superrapide", new Scan.SpecificResult(true, "impeccable", "RAS"));
        scanResults3.add(scanResult33);
        Result result3 = new Result(simplifiedPackageInfos.get(3), scanResults3);
        mResults.add(result3);

        ArrayList<Result.ScanResult> scanResults4 = new ArrayList<>();
        Result.ScanResult scanResult41 = new Result.ScanResult("integrityscan", "lescanquitueetquiprenddeuxplombes", new Scan.SpecificResult(true, "impeccable", "RAS"));
        scanResults4.add(scanResult41);
        Result.ScanResult scanResult42 = new Result.ScanResult("dexfilescanner", "lescanquitueetquiprenduneplombes", new Scan.SpecificResult(false, "impeccable", "RAS"));
        scanResults4.add(scanResult42);
        Result.ScanResult scanResult43 = new Result.ScanResult("permissionscan", "superrapide", new Scan.SpecificResult(false, "impeccable", "RAS"));
        scanResults4.add(scanResult43);
        Result result4 = new Result(simplifiedPackageInfos.get(4), scanResults4);
        mResults.add(result4);

        /**
         * END FAKE VALUES to proof the UI interface
         */

        //Set button action
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

    /**
     * Method to combine all the scans state counter to update
     */
    private void updateCounter() {
        //TODO by mixing scans
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
