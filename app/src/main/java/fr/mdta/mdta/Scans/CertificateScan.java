package fr.mdta.mdta.Scans;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;

/**
 * Created by manwefm on 18/01/18.
 */

public class CertificateScan extends Scan {

    //scanner description
    private final static String CERTIFICATE_SCANNER_NAME = "Application Certificates Scanner";
    private final static String CERTIFICATE_SCANNER_DESCRIPTION = "This scan consists on verifying " +
            "each application's certificate and warn user if one was tempered";

    private ArrayList<SimplifiedPackageInfo> listPackageInfo;

    /**
     * @param simplifiedPackageInfos
     */
    public CertificateScan(ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos) {
        super(CERTIFICATE_SCANNER_NAME, CERTIFICATE_SCANNER_DESCRIPTION, simplifiedPackageInfos);
    }

    @Override
    public void launchScan(ScanCallback callback) {

        //get list app to scan
        listPackageInfo = getmSimplifiedPackageInfos();

        for ( int i = 0; i < listPackageInfo.size(); i++) {

            SpecificResult result = null;

            try {
                //for each app, verify certificat
                listPackageInfo.get(i).getAppDeveloperCertificate().verify(
                        listPackageInfo.get(i).getAppDeveloperCertificate().getPublicKey()
                );
            } catch (CertificateException e) {
                result = new SpecificResult(false,"CertificateError",
                        e.getMessage());
                mResults.put(listPackageInfo.get(i),result);
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                result = new SpecificResult(false,"NoSuchAlgorithm",
                        e.getMessage());
                mResults.put(listPackageInfo.get(i),result);
            } catch (InvalidKeyException e) {
                result = new SpecificResult(false,"InvalidKey",
                        e.getMessage());
                mResults.put(listPackageInfo.get(i),result);
            } catch (NoSuchProviderException e) {
                result = new SpecificResult(false,"NoSuchProvider",
                        e.getMessage());
                mResults.put(listPackageInfo.get(i),result);
            } catch (SignatureException e) {
                result = new SpecificResult(false,"SignatureError",
                        e.getMessage());
                mResults.put(listPackageInfo.get(i),result);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                if ( result == null ) {
                    result = new SpecificResult(true,"Valid certificate",
                            "The certificate is valid");
                    mResults.put(listPackageInfo.get(i),result);
                }
                updateState();
            }
        }
        //callback to end scan
        callback.OnScanTerminated();
    }


    @Override
    public void cancelScan(ScanCallback callback) {

    }

    protected void updateState() {
        float number_of_app_scanned = listPackageInfo.size();
        mState += 100 / number_of_app_scanned;
    }
}
