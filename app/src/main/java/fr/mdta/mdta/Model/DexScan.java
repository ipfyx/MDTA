package fr.mdta.mdta.Model;

import java.util.ArrayList;

import fr.mdta.mdta.API.Callback.Callback;

/**
 * Created by manwefm on 18/01/18.
 */

public class DexScan extends Scan {
    /**
     * @param scanName
     * @param scanDescription
     * @param simplifiedPackageInfos
     */
    public DexScan(String scanName, String scanDescription, ArrayList<SimplifiedPackageInfo>
            simplifiedPackageInfos) {
        super(scanName, scanDescription, simplifiedPackageInfos);
    }

    @Override
    public void launchScan(Callback callback) {

    }

    @Override
    protected void updateState() {

    }
}
