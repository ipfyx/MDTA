package fr.mdta.mdta.SignaturesScanner;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import fr.mdta.mdta.SignaturesScanner.Model.PackageSignaturesInfo;

/**
 * This public class is there to provide static method to deal with Android Package Manager
 * related to signtature functions
 */
public class SignaturesInfoFactory {

    public static ArrayList<PackageSignaturesInfo> getInstalledPackages(Context context) throws
            IOException, CertificateException {

        ArrayList<PackageSignaturesInfo> packageInfoArrayList = new
                ArrayList<PackageSignaturesInfo>();

        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages
                (context.getPackageManager().GET_META_DATA + context.getPackageManager()
                        .GET_SIGNATURES);

        List<ApplicationInfo> installedApplications = context.getPackageManager()
                .getInstalledApplications(PackageManager
                .GET_SHARED_LIBRARY_FILES);

        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo packageInfo = installedPackages.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager())
                    .toString();
            String packageName = packageInfo.packageName;
            int flag = 0;

            for (int j = 0; j < installedApplications.size(); j++) {
                if (installedApplications.get(j).packageName.equals(packageName)) {
                    flag = installedApplications.get(j).flags;
                }
            }

            String apkSourceDir;
            X509Certificate appDeveloperCertificate = null;
            ArrayList<PackageSignaturesInfo.ApkFileSignature> apkFileSignatures = new ArrayList<>();

            //We assume that application has only one signature, this is clearly the 99% case.
            if (packageInfo.signatures != null && packageInfo.signatures.length >= 0 && (packageInfo.packageName.equals("fr.mdta.mdta") || packageInfo.packageName.equals("eu.chainfire.supersu"))) {
                android.content.pm.Signature s = packageInfo.signatures[0];
                Log.d("hashcode",Integer.toString(s.hashCode()));
                Class c;
                try {

                    /**
                     * https://stackoverflow.com/questions/160970/how-do-i-invoke-a-java-method-when-given-the-method-name-as-a-string
                     */
                    c = Class.forName("android.content.pm.Signature");
                    Method m = c.getMethod("getPublicKey");
                    Object o = m.invoke(s);
                    Log.d("pubkey", o.toString());

                    m = c.getMethod("getChainSignatures");
                    o = m.invoke(s);
                    Signature[] s2 = (Signature []) o;
                    Log.d(packageInfo.packageName, s2[0].toCharsString());


                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                byte[] cert = s.toByteArray();
                InputStream input = new ByteArrayInputStream(cert);
                CertificateFactory cf = CertificateFactory.getInstance("X509");
                appDeveloperCertificate = (X509Certificate) cf.generateCertificate(input);
                String tbs = bytesToHex(appDeveloperCertificate.getTBSCertificate());

                Log.d("TBS",tbs);
                String sign = bytesToHex(appDeveloperCertificate.getSignature());
                Log.d("Signature",sign);
        }

            ApplicationInfo ai = packageInfo.applicationInfo;
            apkSourceDir = ai.sourceDir;
            JarFile jar = new JarFile(apkSourceDir);
            Enumeration<JarEntry> entries = jar.entries();

            /*
            while (entries.hasMoreElements()) {
                PackageSignaturesInfo.ApkFileSignature apkFileSignature;
                JarEntry entry = entries.nextElement();
                Attributes attributes = entry.getAttributes();
                try {
                    if (attributes != null) {
                        for (Iterator it = attributes.keySet().iterator(); it.hasNext(); ) {
                            String attrName = it.next().toString();
                            String attrValue = attributes.getValue(attrName);
                            apkFileSignature = new PackageSignaturesInfo.ApkFileSignature(entry
                                    .toString(), attrName, attrValue);
                            apkFileSignatures.add(apkFileSignature);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            */
            if (appDeveloperCertificate != null) {
                PackageSignaturesInfo packageSignaturesInfo = new PackageSignaturesInfo(appName,
                        packageName, apkSourceDir, appDeveloperCertificate
                        , apkFileSignatures, flag);
                packageInfoArrayList.add(packageSignaturesInfo);
            }

        }

        return packageInfoArrayList;
    }

    public static String bytesToHex(byte[] bytes) {

        final char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
