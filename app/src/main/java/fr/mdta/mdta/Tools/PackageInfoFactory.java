package fr.mdta.mdta.Tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import java.util.ArrayList;
import java.util.List;

import fr.mdta.mdta.Model.SimplifiedPackageInfo;

/**
 * This public class is there to provide static method to deal with Android Package Manager.
 */
public class PackageInfoFactory {


    public static ArrayList<SimplifiedPackageInfo> getInstalledPackages(Context context) {

        ArrayList<SimplifiedPackageInfo> packageInfoArrayList = new ArrayList<SimplifiedPackageInfo>();


        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(context.getPackageManager().GET_PERMISSIONS);

        //TODO: Deal with too much application sent to db HTTP error 413
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);

            String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            boolean isSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;

            String packageName = packageInfo.packageName;
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            int icon = packageInfo.applicationInfo.icon;
            long firstInstallTime = packageInfo.firstInstallTime;
            long lastUpdateTime = packageInfo.lastUpdateTime;

            ArrayList<android.content.pm.Signature> signatures = new ArrayList<>();
            if (packageInfo.signatures != null)
                for (android.content.pm.Signature s : packageInfo.signatures) {
                    signatures.add(s);
                }

            ArrayList<String> permissions = new ArrayList<>();
            if (packageInfo.requestedPermissions != null)
                for (String s : packageInfo.requestedPermissions) {
                    permissions.add(s);
                }

            SimplifiedPackageInfo simplifiedPackageInfo = new SimplifiedPackageInfo(appName, packageName, isSystemApp,
                    versionCode, versionName, firstInstallTime, lastUpdateTime, signatures, permissions);


            packageInfoArrayList.add(simplifiedPackageInfo);

        }

        return packageInfoArrayList;
    }

    public static ArrayList<SimplifiedPackageInfo> getInstalledPackages(Context context, boolean systemApplication) {

        ArrayList<SimplifiedPackageInfo> packageInfoArrayList = new ArrayList<SimplifiedPackageInfo>();


        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(context.getPackageManager().GET_PERMISSIONS);

        //TODO: Deal with too much application sent to db HTTP error 413
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);

            boolean isSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            if (systemApplication == isSystemApp) {
                String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = packageInfo.packageName;
                int versionCode = packageInfo.versionCode;
                String versionName = packageInfo.versionName;
                int icon = packageInfo.applicationInfo.icon;
                long firstInstallTime = packageInfo.firstInstallTime;
                long lastUpdateTime = packageInfo.lastUpdateTime;

                ArrayList<android.content.pm.Signature> signatures = new ArrayList<>();
                if (packageInfo.signatures != null)
                    for (android.content.pm.Signature s : packageInfo.signatures) {
                        signatures.add(s);
                    }

                ArrayList<String> permissions = new ArrayList<>();
                if (packageInfo.requestedPermissions != null)
                    for (String s : packageInfo.requestedPermissions) {
                        permissions.add(s);
                    }

                SimplifiedPackageInfo simplifiedPackageInfo = new SimplifiedPackageInfo(appName, packageName, isSystemApp,
                        versionCode, versionName, firstInstallTime, lastUpdateTime, signatures, permissions);


                packageInfoArrayList.add(simplifiedPackageInfo);
            }
        }

        return packageInfoArrayList;
    }
}