package fr.mdta.mdta.API.APIModel.ReceivedItem;

import java.util.ArrayList;

/**
 * This public object represent our view of a Package, it is based on the PackageInfo Object of Android.
 */
public class BasicScanResultItem {

    private int status;
    private String error;
    private ArrayList<PackageResult> result;

    private class PackageResult {
        private String AppName;

        private int PermissionsNumber;
        private int DeprecatedPermissionsNumber;
        private int FinancialImpactPermissionsNumber;
        private int PrivacyImpactPermissionsNumber;
        private int SystemImpactPermissionsNumber;
        private int BatteryImpactPermissionsNumber;
        private int LocationImpactPermissionsNumber;

        @Override
        public String toString() {
            return "PackageResult{" +
                    "AppName='" + AppName + '\'' +
                    ", PermissionsNumber=" + PermissionsNumber +
                    ", DeprecatedPermissionsNumber=" + DeprecatedPermissionsNumber +
                    ", FinancialImpactPermissionsNumber=" + FinancialImpactPermissionsNumber +
                    ", PrivacyImpactPermissionsNumber=" + PrivacyImpactPermissionsNumber +
                    ", SystemImpactPermissionsNumber=" + SystemImpactPermissionsNumber +
                    ", BatteryImpactPermissionsNumber=" + BatteryImpactPermissionsNumber +
                    ", LocationImpactPermissionsNumber=" + LocationImpactPermissionsNumber +
                    '}';
        }
    }

    @Override
    public String toString() {
        String stringResult = "BasicScanResultItem{" +
                "status=" + status +
                ", error='" + error + '\'' +
                ", result=";

        for (int i = 0; i < result.size(); i++) {
            stringResult += result.get(i).toString() + '\n';
        }

        stringResult += '}';
        return stringResult;

    }
}
