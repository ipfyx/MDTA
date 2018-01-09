package fr.mdta.mdta.API.APIModel.ReceivedItem;

import java.util.ArrayList;


public class DeveloperSignatureScanResultItem {

    private int status;
    private String error;
    private ArrayList<PackageDeveloperSignatureScanResult> result;


    private class PackageDeveloperSignatureScanResult {
        private String PackageName;

        private boolean IsBlacklisted;


    }

}
