package fr.mdta.mdta.SignaturesScanner.Model;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
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

        /**
         * Method to verify if the signature is valid for a message
         *
         * @param calculatedHash     the hash calculated from the file with the object's path
         * @param packageCertificate certificate which contains the key used to sign the hash
         * @return false if the signature is invalid
         */
        public boolean verifySignature(String calculatedHash, X509Certificate packageCertificate) {
            try {
                Signature verifier = Signature.getInstance(mHashingMethod);
                verifier.initVerify(packageCertificate);
                verifier.update(calculatedHash.getBytes());

                if (verifier.verify(mHash.getBytes())) {
                    System.out.println("Signature is valid");
                    return true;
                } else {
                    System.out.println("Signature is invalid");
                    return false;
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (SignatureException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
            return false;
        }

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

        @Override
        public String toString() {
            return "ApkFileSignature{" +
                    "mPath='" + mPath + '\'' +
                    ", mHashingMethod='" + mHashingMethod + '\'' +
                    ", mHash='" + mHash + '\'' +
                    '}';
        }
    }

}

