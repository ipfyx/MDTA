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
import java.util.Date;

import fr.mdta.mdta.Model.Result;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;


public class ResultSpecificAppActivity extends AppCompatActivity {

    //static value
    public final static String KEY_RESULT = "result";
    private final static String APPLICATION_DATE_FORMAT = "MM/dd/yyyy HH:mm";

    //Model
    private Result mResult;

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
    private Button mBackButton;
    private RecyclerView mRecyclerView;
    private ResultSpecificAppAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultspecificapp);

        //Retrieve serializable extra
        mResult = (Result) getIntent().getSerializableExtra(KEY_RESULT);

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
        mBackButton = (Button) findViewById(R.id.backButton);

        //Fill application data
        SimpleDateFormat df = new SimpleDateFormat(APPLICATION_DATE_FORMAT);
        try {
            SimplifiedPackageInfo simplifiedPackageInfo = mResult.getmSimplifiedPackageInfo();
            Drawable icon = getPackageManager().getApplicationIcon(simplifiedPackageInfo.getPackageName());
            mIconImageView.setImageDrawable(icon);
            mAppNameTextView.setText(simplifiedPackageInfo.getAppName());
            mPackageNameTextView.setText(simplifiedPackageInfo.getPackageName());
            mVersionNameTextView.setText(getResources().getString(R.string.title_version_name) + simplifiedPackageInfo.getVersionName());
            mVersioncodeTextView.setText(getResources().getString(R.string.title_version_code) + Integer.toString(simplifiedPackageInfo.getVersionCode()));
            mFirstInstallTimeTextView.setText(getResources().getString(R.string.title_installing_date) + df.format(new Date(simplifiedPackageInfo.getFirstInstallTime())));
            mLastUpdateTimeTextView.setText(getResources().getString(R.string.title_last_update) + df.format(new Date(simplifiedPackageInfo.getLastUpdateTime())));
            mPermissionsNumberTextView.setText(getResources().getString(R.string.title_permissions_number) + Integer.toString(simplifiedPackageInfo.getPermissions().size()));
            mIsApplicationSystemTextView.setText(getResources().getString(R.string.title_is_system_app) + Boolean.toString(simplifiedPackageInfo.isSystemApp()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Fill adapter with values
        mAdapter = new ResultSpecificAppAdapter(this, mResult.getmScanResults());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        //Set interaction to back on main menu
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ResultSpecificAppActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        });
    }
}
