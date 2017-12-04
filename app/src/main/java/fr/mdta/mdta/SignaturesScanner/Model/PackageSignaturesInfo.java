package fr.mdta.mdta.SignaturesScanner.Model;

import java.security.cert.X509Certificate;
import java.util.ArrayList;

/**
 * This public object represents signature based relevant data for further scan
 */
public class PackageSignaturesInfo {
    private String mAppName;
    private String mPackageName;
    private String mApkSourceDir;
    private X509Certificate mAppDeveloperCertificate;
    private ArrayList<ApkFileSignature> mApkFileSignatures;

    public PackageSignaturesInfo(String mAppName, String mPackageName, String mApkSourceDir,
                                 X509Certificate mAppDeveloperCertificate, ArrayList<ApkFileSignature> mApkFileSignatures) {
        this.mAppName = mAppName;
        this.mPackageName = mPackageName;
        this.mApkSourceDir = mApkSourceDir;
        this.mAppDeveloperCertificate = mAppDeveloperCertificate;
        this.mApkFileSignatures = mApkFileSignatures;
    }

    public String getmAppName() {
        return mAppName;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public String getmApkSourceDir() {
        return mApkSourceDir;
    }

    public X509Certificate getmAppDeveloperCertificate() {
        return mAppDeveloperCertificate;
    }

    public ArrayList<ApkFileSignature> getmApkFileSignatures() {
        return mApkFileSignatures;
    }

    public static class ApkFileSignature {
        private String mPath;
        private String mHashingMethod;
        private String mHash;

        public ApkFileSignature(String mPath, String mHashingMethod, String mHash) {
            this.mPath = mPath;
            this.mHashingMethod = mHashingMethod;
            this.mHash = mHash;
        }

        public String getmPath() {
            return mPath;
        }

        public String getmHashingMethod() {
            return mHashingMethod;
        }

        public String getmHash() {
            return mHash;
        }
    }

}

