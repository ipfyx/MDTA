package fr.mdta.mdta.API.APIModel.SentItem;

import java.util.ArrayList;

import fr.mdta.mdta.PermissionsScanner.Model.SimplifiedPackageInfo;

/**
 * Created by baptiste on 07/11/17.
 */

public class PackagesList {
    private ArrayList<SimplifiedPackageInfo> Packages;

    public PackagesList(ArrayList<SimplifiedPackageInfo> packages) {
        Packages = packages;
    }

}
