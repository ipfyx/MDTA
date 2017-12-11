package fr.mdta.mdta.FilesScanner;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

import fr.mdta.mdta.FilesScanner.HashFile.HashGenerationException;
import fr.mdta.mdta.FilesScanner.HashFile.HashGeneratorUtils;
import fr.mdta.mdta.R;

public class FilesScannerActivity extends AppCompatActivity {

    List<ApplicationInfo> installedApplications = new ArrayList<ApplicationInfo>();
    List<ApplicationInfo> systemApps = new ArrayList<ApplicationInfo>();
    List<ApplicationInfo> nonSystemApps = new ArrayList<ApplicationInfo>();

    //TODO:need to agree on a syntax on variable containing path, should they all finish with a / or not

    private String pathToApkUnzipFolder="/data/local";
    private String unzipApkToFolder="unzipedApk";

    private int my_uid = 0;

    boolean suAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_scanner);

        Button button = (Button) findViewById(R.id.scanApp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getListNonSystemApps();
            for ( int i = 0; i < nonSystemApps.size(); i++)
                scanApp(nonSystemApps.get(i));
            }
        });

        installedApplications = this.getPackageManager().getInstalledApplications(PackageManager.GET_SHARED_LIBRARY_FILES);

        suAvailable = Shell.SU.available();

        pathToApkUnzipFolder=getFilesDir().toString()+"/";
    }

    protected String getSuVersion() {
        return Shell.SU.version(false);
    }

    protected String getSuVersionInternal() {
        return Shell.SU.version(true);
    }


    /**
     * https://stackoverflow.com/questions/8784505/how-do-i-check-if-an-app-is-a-non-system-app-in-android
     */

    protected void getListSystemApps() {
        for (int i = 0; i < installedApplications.size(); i++) {
            if ( ( installedApplications.get(i).flags & ApplicationInfo.FLAG_SYSTEM ) != 0 ) {
                systemApps.add(installedApplications.get(i));
            }
            if ( installedApplications.get(i).packageName.equals(this.getPackageName()) ) {
                my_uid = installedApplications.get(i).uid;
            }
        }
    }

    protected void getListNonSystemApps() {
        for (int i = 0; i < installedApplications.size(); i++) {
            if (  ( installedApplications.get(i).flags & ApplicationInfo.FLAG_SYSTEM ) == 0 ) {
                nonSystemApps.add(installedApplications.get(i));
            }
            if ( installedApplications.get(i).packageName.equals(this.getPackageName()) ) {
                my_uid = installedApplications.get(i).uid;
            }
        }
    }

    protected void unzipApk(int uid, String sourceDir) {
        /**
         * https://stackoverflow.com/questions/2634991/android-1-6-android-view-windowmanagerbadtokenexception-unable-to-add-window
         */
        if ( suAvailable ) {
            //Just in case unzipApkToFolder is empty, we move to directory /data/local since there could be a
            // risk to rm -rf /
            CommandFactory.execCommand("cd "+pathToApkUnzipFolder,this);
            CommandFactory.execCommand("rm -rRf "+pathToApkUnzipFolder+unzipApkToFolder+"_"+Integer.toString(uid),this);
            CommandFactory.execCommand("mkdir -p "+pathToApkUnzipFolder+unzipApkToFolder+"_"+Integer.toString(uid),this);
            CommandFactory.execCommand("unzip "+sourceDir+" -d "+pathToApkUnzipFolder+unzipApkToFolder+"_"+Integer.toString(uid),this);
            CommandFactory.execCommand("chown -R "+my_uid+":"+my_uid+" "+pathToApkUnzipFolder+unzipApkToFolder+"_"+Integer.toString(uid),this);

        }
        else {
            TextView tv = (TextView) findViewById(R.id.sample_text);
            tv.setText("Non rooted phone");
        }
    }

    protected void scanApp(ApplicationInfo app) {
        String sourceDir = app.sourceDir;
        String dataDir = app.dataDir;
        String nativeLibraryDir = app.nativeLibraryDir;
        String privateSourceDir = app.deviceProtectedDataDir;
        String publicSourceDir = app.publicSourceDir;
        int uid = app.uid;
        String[] sharedLibraryFiles = app.sharedLibraryFiles;

        Log.d(app.packageName,sourceDir+" "+dataDir+" "+nativeLibraryDir+" "+privateSourceDir+" "+publicSourceDir+" "+Integer.toString(uid));

        File file = new File(pathToApkUnzipFolder+unzipApkToFolder+"_"+Integer.toString(app.uid)+"/AndroidManifest.xml");

        //unzipApk(uid,sourceDir);

        Log.d("root",getFilesDir().toString());

        //TODO : wait for unzipApkToFinish
        try {
            Log.d("sha256",HashGeneratorUtils.generateSHA256(file));
        } catch (HashGenerationException e) {
            e.printStackTrace();
        }
        Log.d("path","sha256sum "+pathToApkUnzipFolder+unzipApkToFolder+"_"+Integer.toString(app.uid)+"/AndroidManifest.xml");
        CommandFactory.execCommand("sha256sum "+pathToApkUnzipFolder+unzipApkToFolder+"_"+Integer.toString(app.uid)+"/AndroidManifest.xml",this);

        //TODO : endScanApp(app);
    }

    protected void endScanApp(ApplicationInfo app) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there could be a
        // risk to rm -rf /&
        CommandFactory.execCommand("cd /data/local",this);
        CommandFactory.execCommand("rm -rRf "+pathToApkUnzipFolder+unzipApkToFolder+"_"+Integer.toString(app.uid),this);
    }

}
