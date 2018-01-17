package fr.mdta.mdta.Model;

import java.util.ArrayList;

import fr.mdta.mdta.API.Callback.Callback;

/**
 * This class represent the PermissionScanner Module.
 */
public class PermissionScan extends Scan {

    //static values
    private final static String PERMISSION_SCANNER_NAME = "Application Permissions Scanner";
    private final static String PERMISSION_SCANNER_DESCRIPTION = "This scan consists on parsing all the permissions " +
            "of an application and to give a feedback according to our personal data and each risk rate of every permission.";

    /**
     * Constructor of a permission scan
     *
     * @param simplifiedPackageInfos
     */
    public PermissionScan(ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos) {
        super(PERMISSION_SCANNER_NAME, PERMISSION_SCANNER_DESCRIPTION, simplifiedPackageInfos);
    }

    @Override
    public void launchScan(Callback callback) {
        //TODO implement the launchscan strategy
    }

    @Override
    protected void updateState() {
        //TODO Give a strategy to evaluate the state of a scan
    }
}
