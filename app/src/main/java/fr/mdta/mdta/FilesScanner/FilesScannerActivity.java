package fr.mdta.mdta.FilesScanner;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Attr;

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
import java.util.regex.Pattern;

import eu.chainfire.libsuperuser.Shell;

import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.R;
import fr.mdta.mdta.SignaturesScanner.Model.PackageSignaturesInfo;
import fr.mdta.mdta.SignaturesScanner.SignaturesInfoFactory;
import fr.mdta.mdta.SignaturesScanner.SignaturesScannerActivity;

public class FilesScannerActivity extends AppCompatActivity implements Callback {

    List<ApplicationInfo> installedApplications = new ArrayList<ApplicationInfo>();
    List<ApplicationInfo> systemApps = new ArrayList<ApplicationInfo>();
    List<ApplicationInfo> nonSystemApps = new ArrayList<ApplicationInfo>();

    ArrayList<PackageSignaturesInfo> result = new ArrayList<PackageSignaturesInfo>();

    //TODO:need to agree on a syntax on variable containing path, should they all finish with a /
    // or not

    private String pathToApkUnzipFolder = "/data/local";
    private String unzipApkToFolder = "unzipedApk";

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
                for (int i = 0; i < nonSystemApps.size(); i++)
                    scanApp(nonSystemApps.get(i));
            }
        });

        Button buttonSystemApps = (Button) findViewById(R.id.scanSystemApps);
        buttonSystemApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListSystemApps();
                for (int i = 0; i < systemApps.size(); i++)
                    scanApp(systemApps.get(i));
            }
        });

        installedApplications = this.getPackageManager().getInstalledApplications(PackageManager
                .GET_SHARED_LIBRARY_FILES);

        suAvailable = Shell.SU.available();

        pathToApkUnzipFolder = getFilesDir().toString() + "/";

        /*
        try {
            pi = this.getPackageManager().getPackageInfo(getPackageName(), PackageManager
                    .GET_SIGNATURES);

            result = SignaturesInfoFactory.getInstalledPackages(this);
            Boolean certRSA = result.get(10).getmApkFileSignatures().get(0).verifySignature(pi.signatures[0].toCharsString(),
                    result.get(10).getmAppDeveloperCertificate());

            //https://stackoverflow.com/questions/17035271/what-does-hide-mean-in-the-android-source-code
            android.content.pm.Signature sign = pi.signatures[0];
            sign.hashCode();

            for (android.content.pm.Signature signs : pi.signatures) {
                if (sign != null) {
                    javax.security.cert.X509Certificate cert = createCert(sign.toByteArray());
                    String dn = (cert == null?"<NULL>":cert.getIssuerDN()).toString();
                    Log.d("dn",dn);
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for ( int i = 0; i < pi.signatures.length; i++) {
            Log.i("appSignature", pi.signatures[i].toCharsString());
        }
        */

    }

    public static javax.security.cert.X509Certificate createCert (byte [] bytes) {
        javax.security.cert.X509Certificate cert = null;
        try {
            cert = javax.security.cert.X509Certificate.getInstance(bytes);
        }
        catch (javax.security.cert.CertificateException e) {
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

    protected void scanApp(ApplicationInfo app) {

        Log.d(app.packageName, app.sourceDir + " " + app.dataDir + " " + app.nativeLibraryDir + " " +
                app.deviceProtectedDataDir + " " + app.publicSourceDir + " " + Integer.toString(app.uid));

        unzipApk(app.uid, app);

        endScanApp(app);

        //TODO : Manage AsyncTask properly
    }

    protected void unzipApk(final int uid, final ApplicationInfo app) {
        /**
         * https://stackoverflow
         * .com/questions/2634991/android-1-6-android-view-windowmanagerbadtokenexception-unable
         * -to-add-window
         */
        if (suAvailable) {
            //Just in case unzipApkToFolder is empty, we move to directory /data/local since
            // there could be a
            // risk to rm -rf /

            String[] listCommand = new String[]{
                    "cd " + pathToApkUnzipFolder,
                    "rm -rRf " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer.toString
                            (uid),
                    "mkdir -p " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer
                            .toString(uid),
                    "unzip " + app.sourceDir + " -d " + pathToApkUnzipFolder + unzipApkToFolder + "_"
                            + Integer.toString(uid),
                    "chown -R " + my_uid + ":" + my_uid + " " + pathToApkUnzipFolder +
                            unzipApkToFolder + "_" + Integer.toString(uid)

            };
            CommandFactory.execCommand(listCommand, new Callback() {
                @Override
                public void OnErrorHappended() {

                }

                @Override
                public void OnErrorHappended(String error) {

                }

                @Override
                public void OnTaskCompleted(Object object) {
                    verifyHashesManifest(uid,app,(String) object);
                }
            }, this);

        } else {
            TextView tv = (TextView) findViewById(R.id.sample_text);
            tv.setText("Non rooted phone");
        }
    }

    //TODO : endScanApp(app);
    //TODO: empty listProcess, rm folder
    protected void endScanApp(ApplicationInfo app) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there
        // could be a
        // risk to rm -rf /&

/*        String[] listCommand = new String[]{
                "cd /data/local",
                "rm -rRf " + pathToApkUnzipFolder + unzipApkToFolder + "_" + Integer.toString(app
                        .uid)
        };

        CommandFactory.execCommand(listCommand, this, this);*/
    }

    protected void addFileToListVerification(final String filePath, final String hash, final int uid, final String hashMethod) {

        final String[] commandToExecute = new String[]{hashMethod+" -b " + pathToApkUnzipFolder +
                unzipApkToFolder + "_" +
                Integer.toString(uid) + "/" + filePath + "| xxd -r -p | base64"};

        CommandFactory.addCommandToExecute(commandToExecute,this,new Callback() {
            @Override
            public void OnErrorHappended() {

            }

            @Override
            public void OnErrorHappended(String error) {

            }

            @Override
            public void OnTaskCompleted(Object object) {
                CommandFactory.COUNT-=1;
                CommandFactory.removeCommand(commandToExecute);
                CommandFactory.launchVerification();

                String calculatedHash = (String) ((String) object).replaceAll("\\n", "")
                        .replaceAll("\\r", "");
                if ( hash.equals(calculatedHash) ) {
                    Log.d(filePath,hash+" / "+calculatedHash);
                } else {
                    Log.d("false","calc: "+calculatedHash+hashMethod+" "+hash+" "+filePath+" "+uid);
                }
            }
        });

    }

        protected void verifyHashesManifest(final int uid, ApplicationInfo app,String unzipResult) {
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
                if ( fileHash == null ) {
                    fileHash = entry.getValue().getValue("SHA1-Digest");
                    if ( fileHash == null ){
                        //MD5 or somethingElse ?
                    } else {
                        addFileToListVerification(filePath,fileHash,uid,"sha1sum");
                    }
                } else {
                    addFileToListVerification(filePath,fileHash,uid,"sha256sum");
                }
            }
            CommandFactory.launchVerification();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
