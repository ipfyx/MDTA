package fr.mdta.mdta.FilesScanner;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.security.CodeSigner;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import eu.chainfire.libsuperuser.Shell;

import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.R;

public class FilesScannerActivity extends AppCompatActivity implements Callback {

    private List<ApplicationInfo> installedApplications = new ArrayList<ApplicationInfo>();
    private List<ApplicationInfo> systemApps = new ArrayList<ApplicationInfo>();
    private List<ApplicationInfo> nonSystemApps = new ArrayList<ApplicationInfo>();
    
    private Callback mycallback = new Callback() {
        @Override
        public void OnErrorHappended() {

        }

        @Override
        public void OnErrorHappended(String error) {

        }

        @Override
        public void OnTaskCompleted(Object object) {
            endScanApp((ApplicationInfo) object);
        }
    };

    //TODO:need to agree on a syntax on variable containing path, should they all finish with a /
    // or not

    private int my_uid = 0;

    boolean suAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_scanner);

        Button buttonNonSystemApps = (Button) findViewById(R.id.scanUserApp);
        buttonNonSystemApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListNonSystemApps();
                if (suAvailable) {
                    if ( !nonSystemApps.isEmpty() ) {
                        scanApp(nonSystemApps.get(0));
                    }
                } else {
                    //TODO
                }
            }
        });

        Button buttonSystemApps = (Button) findViewById(R.id.scanSystemApps);
        buttonSystemApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListSystemApps();
                if (suAvailable) {
                    if ( !systemApps.isEmpty() ) {
                        scanApp(systemApps.get(0));
                    }
                } else {
                    //TODO
                }
            }
        });

        final Button cancelScan = (Button) findViewById(R.id.cancel);
        cancelScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListSystemApps();
                if (suAvailable) {
                    for ( int i = 0; i < CommandFactory.listProcess.size(); i++) {
                        CommandFactory.listProcess.get(i).cancel(true);
                    }
                    CommandFactory.listProcess.clear();
                } else {
                    //TODO
                }
            }
        });

        installedApplications = this.getPackageManager().getInstalledApplications(PackageManager
                .GET_SHARED_LIBRARY_FILES);

        suAvailable = Shell.SU.available();

        CommandFactory.pathToApkUnzipFolder = getFilesDir().toString() + "/";

    }

    public static javax.security.cert.X509Certificate createCert(byte[] bytes) {
        javax.security.cert.X509Certificate cert = null;
        try {
            cert = javax.security.cert.X509Certificate.getInstance(bytes);
        } catch (javax.security.cert.CertificateException e) {
            e.printStackTrace();
        }
        return cert;
    }


    protected String getSuVersion() {
        return Shell.SU.version(false);
    }

    protected String getSuVersionInternal() {
        return Shell.SU.version(true);
    }


    /**
     * https://stackoverflow.com/questions/8784505/how-do-i-check-if-an-app-is-a-non-system-app
     * -in-android
     */

    protected void getListSystemApps() {
        for (int i = 0; i < installedApplications.size(); i++) {
            if ((installedApplications.get(i).flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                systemApps.add(installedApplications.get(i));
            }
            if (installedApplications.get(i).packageName.equals(this.getPackageName())) {
                my_uid = installedApplications.get(i).uid;
            }
        }
    }

    protected void getListNonSystemApps() {
        for (int i = 0; i < installedApplications.size(); i++) {
            if ((installedApplications.get(i).flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                nonSystemApps.add(installedApplications.get(i));
            }
            if (installedApplications.get(i).packageName.equals(this.getPackageName())) {
                my_uid = installedApplications.get(i).uid;
            }
        }
    }

    protected void scanApp(final ApplicationInfo app) {
/*
        Log.d(app.packageName, app.sourceDir + " " + app.dataDir + " " + app.nativeLibraryDir + "" +
                " " +
                app.deviceProtectedDataDir + " " + app.publicSourceDir + " " + Integer.toString
                (app.uid));
*/
        Log.d("scan",app.packageName);

        //unzipApk(app.uid, app);

        CommandFactory.unzipCommand(new Callback() {
            @Override
            public void OnErrorHappended() {

            }

            @Override
            public void OnErrorHappended(String error) {

            }

            @Override
            public void OnTaskCompleted(Object object) {
                verifyHashesManifest(app.uid, app, (String) object);
            }
        }, this, app, my_uid);

        //TODO : Manage AsyncTask properly
    }

    //TODO : endScanApp(app);
    //TODO: empty listProcess, rm folder
    protected void endScanApp(ApplicationInfo app) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there
        // could be a
        // risk to rm -rf /&

        Log.d("ending",app.packageName);
        CommandFactory.endScanApp(this, this, app);
        if ( nonSystemApps.contains(app) ) {
            nonSystemApps.remove(app);
            if ( !nonSystemApps.isEmpty() ) {
                scanApp(nonSystemApps.get(0));
            }
        } else {
            systemApps.remove(app);
            if ( !systemApps.isEmpty() ) {
                scanApp(systemApps.get(0));
            }
        }

    }

    protected void addFileToListVerification(final String filePath, final String hash, final
    ApplicationInfo app, final String hashMethod, final ArrayList<Command> listProcess) {

        final String[] commandToExecute = new String[]{hashMethod + " -b " + CommandFactory
                .pathToApkUnzipFolder +
                CommandFactory.unzipApkToFolder + "_" +
                Integer.toString(app.uid) + "/" + filePath + "| xxd -r -p | base64"};

        Command command = new Command(new Callback() {
            @Override
            public void OnErrorHappended() {

            }

            @Override
            public void OnErrorHappended(String error) {

            }

            @Override
            public void OnTaskCompleted(Object object) {
                CommandFactory.COUNT -= 1;
                CommandFactory.removeCommand(commandToExecute);
                CommandFactory.launchVerification(mycallback, app);

                String calculatedHash = (String) ((String) object).replaceAll("\\n", "")
                        .replaceAll("\\r", "");

                if (hash.equals(calculatedHash)) {
                    Log.d(filePath, hash + " / " + calculatedHash);
                } else {
                    cancelVerification(app,filePath);
                    Log.d("false", "calc: " + calculatedHash + hashMethod + " " + hash + " " +
                            filePath + " " + app.uid);
                }
            }
        }, this, commandToExecute);

        listProcess.add(command);

    }

    protected void verifyHashesManifest(final int uid, ApplicationInfo app, String unzipResult) {
        try {

            /**
             * https://stackoverflow.com/questions/3392189/reading-android-manifest-mf-file
             */

            JarFile jar = new JarFile(app.sourceDir);
            Manifest mf = jar.getManifest();
            Map<String, Attributes> map = mf.getEntries();

            ArrayList<Command> listProcess = new ArrayList<Command>();

            for (Map.Entry<String, Attributes> entry : map.entrySet()) {

                String filePath = entry.getKey();

                String fileHash = entry.getValue().getValue("SHA-256-Digest");

                if (fileHash == null) {
                    fileHash = entry.getValue().getValue("SHA1-Digest");
                    if (fileHash == null) {
                        //MD5 or somethingElse ?
                    } else {
                        addFileToListVerification(filePath, fileHash, app, "sha1sum", listProcess);
                    }
                } else {
                    addFileToListVerification(filePath, fileHash, app, "sha256sum", listProcess);
                }
            }
            CommandFactory.listProcess = listProcess;
            CommandFactory.launchVerification(mycallback, app);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void cancelVerification(ApplicationInfo app, String filepath) {
        for ( int i = 0; i < CommandFactory.listProcess.size(); i++) {
            CommandFactory.listProcess.get(i).cancel(true);
        }
        CommandFactory.listProcess.clear();
        mycallback.OnTaskCompleted(app);
        Log.d("FilesScannerActivity",filepath+" hash is wrong");
    }

    @Override
    public void OnErrorHappended() {

    }

    @Override
    public void OnErrorHappended(String error) {

    }

    @Override
    public void OnTaskCompleted(Object object) {
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(((String) object));
        /*
        hashTest = (String) ((String) object).replaceAll("\\n", "").replaceAll("\\r", "");
        Log.d("hashTest", hashTest);
        */
        //testSignature();
    }

}
