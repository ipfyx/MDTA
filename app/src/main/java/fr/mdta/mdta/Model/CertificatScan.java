package fr.mdta.mdta.Model;

import java.util.ArrayList;

import fr.mdta.mdta.API.Callback.Callback;

/**
 * Created by manwefm on 18/01/18.
 */

public class CertificatScan extends Scan {

    //static values
    private final static String CERTIFICATE_SCANNER_NAME = "Application Certificates Scanner";
    private final static String CERTIFICATE_SCANNER_DESCRIPTION = "This scan consists on verifying " +
            "each application's certificate and warn user if one was tempered";

    /**
     * @param simplifiedPackageInfos
     */
    public CertificatScan(ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos) {
        super(CERTIFICATE_SCANNER_NAME, CERTIFICATE_SCANNER_DESCRIPTION, simplifiedPackageInfos);
    }

    @Override
    public void launchScan(Callback callback) {

    }

    @Override
    protected void updateState() {

    }
}
