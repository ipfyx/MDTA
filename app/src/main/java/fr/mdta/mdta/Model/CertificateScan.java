package fr.mdta.mdta.Model;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import fr.mdta.mdta.API.APIModel.SentItem.PackagesList;
import fr.mdta.mdta.API.Callback.Callback;

/**
 * Created by manwefm on 18/01/18.
 */

public class CertificateScan extends Scan {

    //static values
    private final static String CERTIFICATE_SCANNER_NAME = "Application Certificates Scanner";
    private final static String CERTIFICATE_SCANNER_DESCRIPTION = "This scan consists on verifying " +
            "each application's certificate and warn user if one was tempered";

    /**
     * @param simplifiedPackageInfos
     */
    public CertificateScan(ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos) {
        super(CERTIFICATE_SCANNER_NAME, CERTIFICATE_SCANNER_DESCRIPTION, simplifiedPackageInfos);
    }

    @Override
    public void launchScan(Callback callback) {

        ArrayList<SimplifiedPackageInfo> listPackageInfo = getmSimplifiedPackageInfos();

        SpecificResult result = null;

        for ( int i = 0; i < listPackageInfo.size(); i++) {

            try {
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
            } finally {
                if ( result == null ) {
                    result = new SpecificResult(true,"Valid certificate",
                            "The certificat valid");
                    mResults.put(listPackageInfo.get(i),result);
                }
                updateState();
            }
        }

        callback.OnTaskCompleted("TODO");
    }

    @Override
    protected void updateState() {
        mState+=1;
    }
}
