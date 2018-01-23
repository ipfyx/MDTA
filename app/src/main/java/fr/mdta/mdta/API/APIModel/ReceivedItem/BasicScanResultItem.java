package fr.mdta.mdta.API.APIModel.ReceivedItem;

import java.util.ArrayList;

/**
 * This public object represent our view of a Package, it is based on the PackageInfo Object of Android.
 */
public class BasicScanResultItem {

    private int status;
    private String error;
    private ArrayList<PackageResult> result;

    public ArrayList<PackageResult> getResult() {
        return result;
    }

    public class PackageResult {
        private String PackageName;

        private int PermissionsNumber;
        private int DeprecatedPermissionsNumber;
        private int FinancialImpactPermissionsNumber;
        private int PrivacyImpactPermissionsNumber;
        private int SystemImpactPermissionsNumber;
        private int BatteryImpactPermissionsNumber;
        private int LocationImpactPermissionsNumber;

        public String getPackageName() {
            return PackageName;
        }

        public int getPermissionsNumber() {
            return PermissionsNumber;
        }

        public int getDeprecatedPermissionsNumber() {
            return DeprecatedPermissionsNumber;
        }

        public int getFinancialImpactPermissionsNumber() {
            return FinancialImpactPermissionsNumber;
        }

        public int getPrivacyImpactPermissionsNumber() {
            return PrivacyImpactPermissionsNumber;
        }

        public int getSystemImpactPermissionsNumber() {
            return SystemImpactPermissionsNumber;
        }

        public int getBatteryImpactPermissionsNumber() {
            return BatteryImpactPermissionsNumber;
        }

        public int getLocationImpactPermissionsNumber() {
            return LocationImpactPermissionsNumber;
        }

        @Override
        public String toString() {
            return "PackageResult{" +
                    "PackageName='" + PackageName + '\'' +
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

}
