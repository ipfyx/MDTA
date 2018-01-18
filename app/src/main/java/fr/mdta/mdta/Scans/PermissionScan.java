package fr.mdta.mdta.Scans;

import java.net.MalformedURLException;
import java.util.ArrayList;

import fr.mdta.mdta.API.APIModel.SentItem.PackagesList;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.BasicScanRequester;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;

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
    public void launchScan(final ScanCallback callback) {
        //TODO implement the launchscan strategy

        PackagesList packagesList = new PackagesList(this.getmSimplifiedPackageInfos());

        try {
            BasicScanRequester request = new BasicScanRequester(new Callback() {
                @Override
                public void OnErrorHappended() {

                }

                @Override
                public void OnErrorHappended(String error) {

                }

                @Override
                public void OnTaskCompleted(Object object) {
                    for (int i = 0; i < getmSimplifiedPackageInfos().size(); i++) {
                        mResults.put(getmSimplifiedPackageInfos().get(i), new SpecificResult(true, "FAKE PermissionOK", "FAKE BNo danger"));

                    }
                    mState = 100;
                    callback.OnScanTerminated();
                }
            }, packagesList);
            request.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void cancelScan(ScanCallback callback) {

    }

    @Override
    protected void updateState() {
        //TODO Give a strategy to evaluate the state of a scan
    }
}
