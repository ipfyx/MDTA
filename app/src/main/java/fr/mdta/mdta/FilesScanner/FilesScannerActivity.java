package fr.mdta.mdta.FilesScanner;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;

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
            endScanApp((ApplicationInfo) object, TypeScan.SIGNATURE_SCAN);
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
                        scanApp(nonSystemApps.get(0),TypeScan.SIGNATURE_SCAN);
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
                        scanApp(systemApps.get(0),TypeScan.SIGNATURE_SCAN);
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
                //TODO clean up directory
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

        final Button openFile = (Button) findViewById(R.id.openfile);
        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListNonSystemApps();
                scanApp(systemApps.get(0),TypeScan.DEX_SCAN);
                scanDexFile(nonSystemApps.get(0));
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

    protected void scanApp(final ApplicationInfo app, final TypeScan typeScan) {
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
                if ( typeScan.equals("signature") ) {
                    verifyHashesManifest(app.uid, app, (String) object);
                } else {
                    scanDexFile(app);
                }
            }
        }, this, app, my_uid, getAppSELinuxContext());

        //TODO : Manage AsyncTask properly
    }

    //TODO : endScanApp(app);
    //TODO: empty listProcess, rm folder
    protected void endScanApp(ApplicationInfo app, TypeScan typeScan) {
        //Just in case unzipApkToFolder is empty, we move to directory /data/local since there
        // could be a
        // risk to rm -rf /&

        Log.d("ending",app.packageName);
        CommandFactory.endScanApp(this, this, app);
        if ( nonSystemApps.contains(app) ) {
            nonSystemApps.remove(app);
            if ( !nonSystemApps.isEmpty() && typeScan.equals(TypeScan.SIGNATURE_SCAN) ) {
                scanApp(nonSystemApps.get(0),TypeScan.SIGNATURE_SCAN);
            } else {
                scanApp(nonSystemApps.get(0),TypeScan.DEX_SCAN);
            }
        } else {
            systemApps.remove(app);
            if ( !systemApps.isEmpty() && typeScan.equals(TypeScan.SIGNATURE_SCAN)) {
                scanApp(systemApps.get(0),TypeScan.SIGNATURE_SCAN);
            } else {
                scanApp(nonSystemApps.get(0),TypeScan.DEX_SCAN);
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

    protected String getAppSELinuxContext() {

        /**
         * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/os/SELinux.java
         */
        Class seLinux = null;
        try {
            seLinux = Class.forName("android.os.SELinux");
            Method context = seLinux.getMethod("getContext");
            String result = (String) context.invoke(seLinux.newInstance());
            Log.d("context",result);
            return result;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String getFileAppSELinuxContext() {

        /**
         * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/os/SELinux.java
         */

        final String fileName = CommandFactory.pathToApkUnzipFolder + "SELinuxTest.txt";

        Class seLinux = null;
        PrintWriter writer = null;
        try {

            writer = new PrintWriter(fileName, "UTF-8");
            writer.println(fileName);

            seLinux = Class.forName("android.os.SELinux");
            Method context = seLinux.getMethod("getFileContext",new Class[] {String.class});
            String result = (String) context.invoke(seLinux.newInstance(),new Object[]{fileName});
            Log.d("context",result);

            return result;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } finally {
            writer.flush();
            writer.close();
        }
    }

    protected void scanDexFile(ApplicationInfo app) {
        final String appDirectory = CommandFactory.pathToApkUnzipFolder + CommandFactory.unzipApkToFolder + "_" +
                app.uid;
        final String myDirectory = CommandFactory.pathToApkUnzipFolder + CommandFactory.unzipApkToFolder + "_" +
                my_uid;

        try {

            DexBackedDexFile dexFile = DexFileFactory.loadDexFile(appDirectory+"/classes.dex", null);
            Iterator iterator = dexFile.getMethods().iterator();

            while (iterator.hasNext()) {
                String a = iterator.next().toString();
                if ( a.contains("shell")) {
                    System.out.println("Value: " + a + " ");
                }
            }

            endScanApp(app,TypeScan.DEX_SCAN);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
