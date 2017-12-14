package fr.mdta.mdta.API.APIModel.ReceivedItem;

import java.util.ArrayList;

/**
 * This public object represent our view of a Package, it is based on the PackageInfo Object of Android.
 */
public class DeveloperSignatureScanResultItem {

    private int status;
    private String error;
    private ArrayList<PackageDeveloperSignatureScanResult> result;

    private class PackageDeveloperSignatureScanResult {
        private String PackageName;

        private boolean IsBlacklisted;


    }

}
