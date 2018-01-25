package fr.mdta.mdta.Scans;

import java.net.MalformedURLException;
import java.util.ArrayList;

import fr.mdta.mdta.API.APIModel.ReceivedItem.BasicScanResultItem;
import fr.mdta.mdta.API.APIModel.SentItem.PackagesList;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.BasicScanRequester;
import fr.mdta.mdta.Model.Scan;
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
                            result = buildSpecificResult(items.get(i));
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

    private SpecificResult buildSpecificResult(BasicScanResultItem.PackageResult packageResult) {
        final int DeprecatedPermissionsNumber = 75;
        final int FinancialImpactPermissionsNumber = 50;
        final int PrivacyImpactPermissionsNumber = 50;
        final int SystemImpactPermissionsNumber = 100;
        final int BatteryImpactPermissionsNumber = 20;
        final int LocationImpactPermissionsNumber = 25;
        final int PermissionLevelNoProtection = 0;
        final int PermissionLevelNormal = 0;
        final int PermissionLevelDangerous = 50;
        final int PermissionLevelSignature = 50;
        final int PermissionLevelSystemOrSignature = 75;
        final int PermissionLevelSystem = 100;
        final int PermissionLevelSignatureOrPrivileged = 100;
        final int PermissionLevelNoThirdParty = 100;

        final int MAX_ACCEPTABLE_VALUE = 100;

        int value = packageResult.getDeprecatedPermissionsNumber() * DeprecatedPermissionsNumber +
                packageResult.getFinancialImpactPermissionsNumber() * FinancialImpactPermissionsNumber +
                packageResult.getPrivacyImpactPermissionsNumber() * PrivacyImpactPermissionsNumber +
                packageResult.getSystemImpactPermissionsNumber() * SystemImpactPermissionsNumber +
                packageResult.getBatteryImpactPermissionsNumber() * BatteryImpactPermissionsNumber +
                packageResult.getLocationImpactPermissionsNumber() * LocationImpactPermissionsNumber +
                packageResult.getPermissionLevelNoProtection() * PermissionLevelNoProtection +
                packageResult.getPermissionLevelNormal() * PermissionLevelNormal +
                packageResult.getPermissionLevelDangerous() * PermissionLevelDangerous +
                packageResult.getPermissionLevelSignature() * PermissionLevelSignature +
                packageResult.getPermissionLevelSystemOrSignature() * PermissionLevelSystemOrSignature +
                packageResult.getPermissionLevelSystem() * PermissionLevelSystem +
                packageResult.getPermissionLevelSignatureOrPrivileged() * PermissionLevelSignatureOrPrivileged +
                packageResult.getPermissionLevelNoThirdParty() * PermissionLevelNoThirdParty;

        if (value < MAX_ACCEPTABLE_VALUE) {
            return new SpecificResult(true, "Application not too much permissive", "Calculated risk over 100: " + value + "\n" + packageResult.toString());
        } else {
            return new SpecificResult(false, "Application a lot permissive", "Calculated risk over 100: " + value + "\n" + packageResult.toString());
        }
    }

    @Override
    public void cancelScan(ScanCallback callback) {

    }

    protected void updateState() {
        //TODO Give a strategy to evaluate the state of a scan
    }
}
