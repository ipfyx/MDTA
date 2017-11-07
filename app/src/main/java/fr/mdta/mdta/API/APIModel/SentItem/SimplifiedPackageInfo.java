package fr.mdta.mdta.API.APIModel.SentItem;

import java.util.ArrayList;

/**
 * This public object represent our view of a Package, it is based on the PackageInfo Object of Android.
 */
public class SimplifiedPackageInfo {
    private String AppName;
    private String PackageName;
    private int VersionCode;
    private String VersionName;
    private int Icon;

    private long FirstInstallTime;
    private long LastUpdateTime;

    private ArrayList<android.content.pm.Signature> Signatures;
    private ArrayList<String> Permissions = new ArrayList<String>();

    public SimplifiedPackageInfo(String appName, String packageName, int versionCode,
                                 String versionName, int icon, long firstInstallTime,
                                 long lastUpdateTime, ArrayList<android.content.pm.Signature> signatures,
                                 ArrayList<String> permissions) {
        this.AppName = appName;
        this.PackageName = packageName;
        this.VersionCode = versionCode;
        this.VersionName = versionName;
        this.Icon = icon;
        this.FirstInstallTime = firstInstallTime;
        this.LastUpdateTime = lastUpdateTime;
        this.Signatures = signatures;
        this.Permissions = permissions;
    }
}
