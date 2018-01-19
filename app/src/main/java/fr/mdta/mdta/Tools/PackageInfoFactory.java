package fr.mdta.mdta.Tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import fr.mdta.mdta.Model.SimplifiedPackageInfo;

/**
 * This public class is there to provide static method to deal with Android Package Manager.
 */
public class PackageInfoFactory {

    /**
     * Get whole system simplified package info
     *
     * @param context
     * @return
     */
    public static ArrayList<SimplifiedPackageInfo> getInstalledPackages(Context context) {

        ArrayList<SimplifiedPackageInfo> packageInfoArrayList = new ArrayList<SimplifiedPackageInfo>();


        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(context.getPackageManager().GET_PERMISSIONS
                + context.getPackageManager().GET_SIGNATURES);

        //TODO: Deal with too much application sent to db HTTP error 413
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);

            String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            boolean isSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;

            String packageName = packageInfo.packageName;
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            long firstInstallTime = packageInfo.firstInstallTime;
            long lastUpdateTime = packageInfo.lastUpdateTime;

            String apkSourceDir = packageInfo.applicationInfo.sourceDir;
            X509Certificate appDeveloperCertificate = null;
            //We assume that application has only one signature, this is clearly the 99% case.
            if (packageInfo.signatures != null && packageInfo.signatures.length >= 0) {
                android.content.pm.Signature s = packageInfo.signatures[0];

                byte[] cert = s.toByteArray();
                InputStream input = new ByteArrayInputStream(cert);
                CertificateFactory cf = null;
                try {
                    cf = CertificateFactory.getInstance("X509");
                    appDeveloperCertificate = (X509Certificate) cf.generateCertificate(input);
                } catch (CertificateException e) {
                    e.printStackTrace();
                }

            }

            ArrayList<String> permissions = new ArrayList<>();
            if (packageInfo.requestedPermissions != null)
                for (String s : packageInfo.requestedPermissions) {
                    permissions.add(s);
                }

            SimplifiedPackageInfo simplifiedPackageInfo = new SimplifiedPackageInfo(appName, packageName, apkSourceDir, isSystemApp,
                    versionCode, versionName, firstInstallTime, lastUpdateTime, appDeveloperCertificate, permissions);


            packageInfoArrayList.add(simplifiedPackageInfo);

        }

        return packageInfoArrayList;
    }

    /**
     * Get only system application or only downloaded application simplified package info
     * @param context
     * @param systemApplication true to get only system application, false to get only downloaded applciation
     * @return
     */
    public static ArrayList<SimplifiedPackageInfo> getInstalledPackages(Context context, boolean systemApplication) {

        ArrayList<SimplifiedPackageInfo> packageInfoArrayList = new ArrayList<SimplifiedPackageInfo>();


        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(context.getPackageManager().GET_PERMISSIONS + context.getPackageManager()
                .GET_SIGNATURES);

        //TODO: Deal with too much application sent to db HTTP error 413
        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);

            boolean isSystemApp = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            if (systemApplication == isSystemApp) {
                String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = packageInfo.packageName;
                int versionCode = packageInfo.versionCode;
                String versionName = packageInfo.versionName;
                long firstInstallTime = packageInfo.firstInstallTime;
                long lastUpdateTime = packageInfo.lastUpdateTime;

                String apkSourceDir = packageInfo.applicationInfo.sourceDir;
                X509Certificate appDeveloperCertificate = null;
                //We assume that application has only one signature, this is clearly the 99% case.
                if (packageInfo.signatures != null && packageInfo.signatures.length >= 0) {
                    android.content.pm.Signature s = packageInfo.signatures[0];

                    byte[] cert = s.toByteArray();
                    InputStream input = new ByteArrayInputStream(cert);
                    CertificateFactory cf = null;
                    try {
                        cf = CertificateFactory.getInstance("X509");
                        appDeveloperCertificate = (X509Certificate) cf.generateCertificate(input);
                    } catch (CertificateException e) {
                        e.printStackTrace();
                    }

                }

                ArrayList<String> permissions = new ArrayList<>();
                if (packageInfo.requestedPermissions != null)
                    for (String s : packageInfo.requestedPermissions) {
                        permissions.add(s);
                    }


                SimplifiedPackageInfo simplifiedPackageInfo = new SimplifiedPackageInfo(appName, packageName, apkSourceDir, isSystemApp,
                        versionCode, versionName, firstInstallTime, lastUpdateTime, appDeveloperCertificate, permissions);


                packageInfoArrayList.add(simplifiedPackageInfo);
            }
        }

        return packageInfoArrayList;
    }
}