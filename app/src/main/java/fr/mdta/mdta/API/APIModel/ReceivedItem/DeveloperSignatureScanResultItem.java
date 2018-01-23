package fr.mdta.mdta.API.APIModel.ReceivedItem;

import java.util.ArrayList;


public class DeveloperSignatureScanResultItem {

    private int status;
    private String error;
    private ArrayList<PackageDeveloperSignatureScanResult> result;

    public ArrayList<PackageDeveloperSignatureScanResult> getResult() {
        return result;
    }

    public class PackageDeveloperSignatureScanResult {
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
