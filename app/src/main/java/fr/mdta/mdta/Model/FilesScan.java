package fr.mdta.mdta.Model;

import java.util.ArrayList;

import fr.mdta.mdta.API.Callback.Callback;

/**
 * Created by manwefm on 18/01/18.
 */

public class FilesScan extends Scan {

    //static values
    private final static String FILES_SCANNER_NAME = "Application Files Scanner";
    private final static String FILES_SCANNER_DESCRIPTION = "This scan can verify the integrity" +
            "of each file contained in an apk and detect dangerous methods used by the application";

    /**
     * @param simplifiedPackageInfos
     */
    public FilesScan(ArrayList<SimplifiedPackageInfo>
            simplifiedPackageInfos) {
        super(FILES_SCANNER_NAME, FILES_SCANNER_DESCRIPTION, simplifiedPackageInfos);
    }

    @Override
    public void launchScan(Callback callback) {

    }

    @Override
    protected void updateState() {

    }
}
