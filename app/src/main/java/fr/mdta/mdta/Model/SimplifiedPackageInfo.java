package fr.mdta.mdta.Model;

import android.content.pm.Signature;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This public object represent our view of a Package, it is based on the PackageInfo Object of Android.
 */
public class SimplifiedPackageInfo implements Serializable {
    /**
     * Applciation name
     */
    private String AppName;
    /**
     * Package name
     */
    private String PackageName;
    /**
     * Version name
     */
    private String VersionName;
    /**
     * Version code
     */
    private int VersionCode;
    /**
     * Boolean to know if the app is considered as a system application for android
     */
    private boolean IsSystemApp;
    /**
     * Date of the first installation
     */
    private long FirstInstallTime;
    /**
     * Date of the last update
     */
    private long LastUpdateTime;
    /**
     * List of application signatures
     */
    private ArrayList<android.content.pm.Signature> Signatures;
    /**
     * List of application permissions
     */
    private ArrayList<String> Permissions = new ArrayList<String>();

    /**
     * Cosntructor of our simplified package info
     *
     * @param appName
     * @param packageName
     * @param isSystemApp
     * @param versionCode
     * @param versionName
     * @param firstInstallTime
     * @param lastUpdateTime
     * @param signatures
     * @param permissions
     */
    public SimplifiedPackageInfo(String appName, String packageName, boolean isSystemApp, int versionCode,
                                 String versionName, long firstInstallTime,
                                 long lastUpdateTime, ArrayList<android.content.pm.Signature> signatures,
                                 ArrayList<String> permissions) {
        this.AppName = appName;
        this.PackageName = packageName;
        this.IsSystemApp = isSystemApp;
        this.VersionCode = versionCode;
        this.VersionName = versionName;
        this.FirstInstallTime = firstInstallTime;
        this.LastUpdateTime = lastUpdateTime;
        this.Signatures = signatures;
        this.Permissions = permissions;
    }

    /**
     * Standard getter to access app name
     *
     * @return
     */
    public String getAppName() {
        return AppName;
    }

    /**
     * Standard getter to access package name
     *
     * @return
     */
    public String getPackageName() {
        return PackageName;
    }

    /**
     * Standard getter to access app name
     *
     * @return
     */
    public boolean isSystemApp() {
        return IsSystemApp;
    }

    /**
     * Standard getter to know if the app is considered as system applciation or not
     *
     * @return
     */
    public int getVersionCode() {
        return VersionCode;
    }

    /**
     * Standard getter to access version name
     *
     * @return
     */
    public String getVersionName() {
        return VersionName;
    }

    /**
     * Standard getter to access the date of the first installation
     *
     * @return
     */
    public long getFirstInstallTime() {
        return FirstInstallTime;
    }

    /**
     * Standard getter to access the date of the last update of the package
     *
     * @return
     */
    public long getLastUpdateTime() {
        return LastUpdateTime;
    }

    /**
     * Standard getter to access the lis tof applciation signature
     *
     * @return
     */
    public ArrayList<Signature> getSignatures() {
        return Signatures;
    }

    /**
     * Standard getter to access the list of application permission
     *
     * @return
     */
    public ArrayList<String> getPermissions() {
        return Permissions;
    }
}
