package fr.mdta.mdta.Scans;

import java.net.MalformedURLException;
import java.util.ArrayList;

import fr.mdta.mdta.API.APIModel.ReceivedItem.CertificateSignatureScanResultItem;
import fr.mdta.mdta.API.APIModel.SentItem.CertificateSignatureList;
import fr.mdta.mdta.API.Callback.Callback;
import fr.mdta.mdta.API.Requester.CertificateSignatureScanRequester;
import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;

public class BlacklistedCertificateScan extends Scan {

    //static values
    private final static String BLACKLISTED_DEVELOPPER_SCANNER_NAME = "Blacklisted Application Scanner";
    private final static String BLACKLISTED_DEVELOPPER_SCANNER_DESCRIPTION = "This scan consists on verifying " +
            "each application's certificate signature and warn user if the application is known as malicious";

    private final static int MAX_SENT_ITEM = 50;
    private int endedRequest = 0;

    /**
     * @param simplifiedPackageInfos
     */
    public BlacklistedCertificateScan(ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos) {
        super(BLACKLISTED_DEVELOPPER_SCANNER_NAME, BLACKLISTED_DEVELOPPER_SCANNER_DESCRIPTION, simplifiedPackageInfos);
    }

    @Override
    public void launchScan(final ScanCallback callback) {
        ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos = new ArrayList<>();
        simplifiedPackageInfos.addAll(getmSimplifiedPackageInfos());

        final float globalSize = simplifiedPackageInfos.size();
        final int requiredRequest = (int) Math.ceil((float) simplifiedPackageInfos.size() / MAX_SENT_ITEM);
        while (!simplifiedPackageInfos.isEmpty()) {
            ArrayList<CertificateSignatureList.CertificateSignatureListElement> certificateSignatures = new ArrayList<>();
            while (!simplifiedPackageInfos.isEmpty() && certificateSignatures.size() < MAX_SENT_ITEM) {
                SimplifiedPackageInfo simplifiedPackageInfo = simplifiedPackageInfos.remove(0);
                CertificateSignatureList.CertificateSignatureListElement certificateSignature = new CertificateSignatureList.CertificateSignatureListElement(simplifiedPackageInfo.getPackageName(), simplifiedPackageInfo.getApplicationCertificateSignatureAlgorithm(), simplifiedPackageInfo.getApplicationCertificateSignature());
                certificateSignatures.add(certificateSignature);
                float updatedState = mState + 50 / globalSize;
                setmState(updatedState);
            }
            CertificateSignatureList certificateSignatureList = new CertificateSignatureList(certificateSignatures);
            try {
                CertificateSignatureScanRequester request = new CertificateSignatureScanRequester(new Callback() {
                    @Override
                    public void OnErrorHappended() {

                    }

                    @Override
                    public void OnErrorHappended(String error) {

                    }

                    @Override
                    public void OnTaskCompleted(Object object) {
                        ArrayList<CertificateSignatureScanResultItem.PackageCertificateSignatureScanResult> items = (ArrayList<CertificateSignatureScanResultItem.PackageCertificateSignatureScanResult>) object;
                        for (int i = 0; i < items.size(); i++) {
                            SpecificResult result = null;
                            if (items.get(i).isBlacklisted()) {
                                result = new SpecificResult(false, "Blacklisted Application", "This application is known as malicious application.");
                            } else {
                                result = new SpecificResult(true, "Unknown application", "This application has never been known as a malicious aplication.");
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
                }, certificateSignatureList);
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
