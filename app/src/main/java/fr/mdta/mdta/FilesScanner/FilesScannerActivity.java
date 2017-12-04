package fr.mdta.mdta.FilesScanner;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

import fr.mdta.mdta.R;

public class FilesScannerActivity extends AppCompatActivity {

    List<ApplicationInfo> installedApplications = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_scanner);

        installedApplications = this.getPackageManager().getInstalledApplications(PackageManager.GET_SHARED_LIBRARY_FILES);

        boolean suAvailable = Shell.SU.available();

        Log.d("system",getListNonSystemApps().toString());

        /**
         * https://stackoverflow.com/questions/2634991/android-1-6-android-view-windowmanagerbadtokenexception-unable-to-add-window
         */
        if ( suAvailable ) {
            //CommandFactory.execCommand("ls",this);
        }
        else {
            TextView tv = (TextView) findViewById(R.id.sample_text);
            tv.setText("Non rooted phone");
        }
    }

    protected String getSuVersion() {
        return Shell.SU.version(false);
    }

    protected String getSuVersionInternal() {
        return Shell.SU.version(true);
    }

    protected List<ApplicationInfo> getListSystemApps() {
        List<ApplicationInfo> systemApps = new ArrayList<ApplicationInfo>();
        for (int i = 0; i < installedApplications.size(); i++) {
            if ( ( installedApplications.get(i).flags & ApplicationInfo.FLAG_SYSTEM ) != 0 ) {
                systemApps.add(installedApplications.get(i));
            }
        }
        return systemApps;
    }

    protected List<ApplicationInfo> getListNonSystemApps() {
        List<ApplicationInfo> nonSystemApps = new ArrayList<ApplicationInfo>();
        for (int i = 0; i < installedApplications.size(); i++) {
            if (  ( installedApplications.get(i).flags & ApplicationInfo.FLAG_SYSTEM ) == 0 ) {
                nonSystemApps.add(installedApplications.get(i));
            }
        }
        return nonSystemApps;
    }

}
