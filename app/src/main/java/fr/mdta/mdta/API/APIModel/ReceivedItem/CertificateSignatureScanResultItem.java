package fr.mdta.mdta.API.APIModel.ReceivedItem;

import java.util.ArrayList;


public class CertificateSignatureScanResultItem {

    private int status;
    private String error;
    private ArrayList<PackageCertificateSignatureScanResult> result;

    public ArrayList<PackageCertificateSignatureScanResult> getResult() {
        return result;
    }

    public class PackageCertificateSignatureScanResult {
        private String PackageName;

        private boolean IsBlacklisted;

        public String getPackageName() {
            return PackageName;
        }

        public boolean isBlacklisted() {
            return IsBlacklisted;
        }
    }

}
