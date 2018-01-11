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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
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
            if (packageInfo.signatures != null && packageInfo.signatures.length >= 0) {
                android.content.pm.Signature s = packageInfo.signatures[0];

                byte[] cert = s.toByteArray();
                InputStream input = new ByteArrayInputStream(cert);
                CertificateFactory cf = CertificateFactory.getInstance("X509");
                appDeveloperCertificate = (X509Certificate) cf.generateCertificate(input);

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

    public static Boolean verifyCertificat(PackageSignaturesInfo pi) {

        Boolean result = true;

        try {
            pi.getmAppDeveloperCertificate().verify(pi.getmAppDeveloperCertificate().getPublicKey
                    ());
        } catch (CertificateException e) {
             result = false;
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            result = false;
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            result = false;
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            result = false;
            e.printStackTrace();
        } catch (SignatureException e) {
            result = false;
            e.printStackTrace();
        } finally {
            return result;
        }
    }

}
