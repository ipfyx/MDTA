package fr.mdta.mdta.Model;

import android.util.Log;

import java.net.MalformedURLException;
import java.util.ArrayList;

import fr.mdta.mdta.API.APIModel.ReceivedItem.BasicScanResultItem;
import fr.mdta.mdta.API.APIModel.SentItem.PackagesList;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.BasicScanRequester;

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
                    Log.d("result", ((BasicScanResultItem) object).toString());
                }
            }, packagesList);
            request.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void updateState() {
        //TODO Give a strategy to evaluate the state of a scan
    }
}
