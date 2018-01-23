package fr.mdta.mdta.Scans;

import java.net.MalformedURLException;
import java.util.ArrayList;

import fr.mdta.mdta.API.APIModel.ReceivedItem.BasicScanResultItem;
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

    private final static int MAX_SENT_ITEM = 50;
    private int endedRequest = 0;

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

        ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos = new ArrayList<>();
        simplifiedPackageInfos.addAll(getmSimplifiedPackageInfos());

        final float globalSize = simplifiedPackageInfos.size();
        final int requiredRequest = (int) Math.ceil((float) simplifiedPackageInfos.size() / MAX_SENT_ITEM);
        while (!simplifiedPackageInfos.isEmpty()) {
            ArrayList<SimplifiedPackageInfo> listToSend = new ArrayList<>();
            while (!simplifiedPackageInfos.isEmpty() && listToSend.size() < MAX_SENT_ITEM) {
                listToSend.add(simplifiedPackageInfos.remove(0));
                float updatedState = mState + 50 / globalSize;
                setmState(updatedState);
            }
            PackagesList packagesList = new PackagesList(listToSend);
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
                        ArrayList<BasicScanResultItem.PackageResult> items = (ArrayList<BasicScanResultItem.PackageResult>) object;

                        for (int i = 0; i < items.size(); i++) {

                            SpecificResult result;
                            result = new SpecificResult(false, "Application a lot permissive", items.get(i).toString());
                            mResults.put(getmSimplifiedPackageInfo(items.get(i).getPackageName()), result);

                            float updatedState = mState + 50 / globalSize;
                            setmState(updatedState);
                        }
                        endedRequest += 1;
                        if (endedRequest == requiredRequest) {
                            callback.OnScanTerminated();
                        }
                    }
                }, packagesList);
                request.execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void cancelScan(ScanCallback callback) {

    }

    protected void updateState() {
        //TODO Give a strategy to evaluate the state of a scan
    }
}
