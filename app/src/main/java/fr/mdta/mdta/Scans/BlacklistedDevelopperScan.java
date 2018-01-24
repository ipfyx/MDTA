package fr.mdta.mdta.Scans;

import java.net.MalformedURLException;
import java.util.ArrayList;

import fr.mdta.mdta.API.APIModel.ReceivedItem.DeveloperSignatureScanResultItem;
import fr.mdta.mdta.API.APIModel.SentItem.DeveloperSignatureList;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.DeveloperSignatureScanRequester;
import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;

public class BlacklistedDevelopperScan extends Scan {

    //static values
    private final static String BLACKLISTED_DEVELOPPER_SCANNER_NAME = "Blacklisted Developper Scanner";
    private final static String BLACKLISTED_DEVELOPPER_SCANNER_DESCRIPTION = "This scan consists on verifying " +
            "each application's developper key and warn user if the developper is known as malicious";

    private final static int MAX_SENT_ITEM = 50;
    private int endedRequest = 0;

    /**
     * @param simplifiedPackageInfos
     */
    public BlacklistedDevelopperScan(ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos) {
        super(BLACKLISTED_DEVELOPPER_SCANNER_NAME, BLACKLISTED_DEVELOPPER_SCANNER_DESCRIPTION, simplifiedPackageInfos);
    }

    @Override
    public void launchScan(final ScanCallback callback) {
        ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos = new ArrayList<>();
        simplifiedPackageInfos.addAll(getmSimplifiedPackageInfos());

        final float globalSize = simplifiedPackageInfos.size();
        final int requiredRequest = (int) Math.ceil((float) simplifiedPackageInfos.size() / MAX_SENT_ITEM);
        while (!simplifiedPackageInfos.isEmpty()) {
            ArrayList<DeveloperSignatureList.DeveloperSignatureListElement> developerSignatures = new ArrayList<>();
            while (!simplifiedPackageInfos.isEmpty() && developerSignatures.size() < MAX_SENT_ITEM) {
                SimplifiedPackageInfo simplifiedPackageInfo = simplifiedPackageInfos.remove(0);
                DeveloperSignatureList.DeveloperSignatureListElement developerSignature = new DeveloperSignatureList.DeveloperSignatureListElement(simplifiedPackageInfo.getPackageName(), simplifiedPackageInfo.getDevelopperKeyAlgorithmMethod(), simplifiedPackageInfo.getAppDeveloperBase64Key());
                developerSignatures.add(developerSignature);
                float updatedState = mState + 50 / globalSize;
                setmState(updatedState);
            }
            DeveloperSignatureList developerSignatureList = new DeveloperSignatureList(developerSignatures);
            try {
                DeveloperSignatureScanRequester request = new DeveloperSignatureScanRequester(new Callback() {
                    @Override
                    public void OnErrorHappended() {

                    }

                    @Override
                    public void OnErrorHappended(String error) {

                    }

                    @Override
                    public void OnTaskCompleted(Object object) {
                        ArrayList<DeveloperSignatureScanResultItem.PackageDeveloperSignatureScanResult> items = (ArrayList<DeveloperSignatureScanResultItem.PackageDeveloperSignatureScanResult>) object;
                        for (int i = 0; i < items.size(); i++) {
                            SpecificResult result = null;
                            if (items.get(i).isBlacklisted()) {
                                result = new SpecificResult(false, "Suspicious developper", "This developper is known as malicious application developper.");
                            } else {
                                result = new SpecificResult(true, "Unknown developper", "This developper has never been known as a malicious developper.");
                            }
                            mResults.put(getmSimplifiedPackageInfo(items.get(i).getPackageName()), result);
                            float updatedState = mState + 50 / globalSize;
                            setmState(updatedState);
                        }
                        endedRequest += 1;
                        if (endedRequest == requiredRequest) {
                            callback.OnScanTerminated();
                        }
                    }
                }, developerSignatureList);
                request.execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void cancelScan(ScanCallback callback) {

    }

}
