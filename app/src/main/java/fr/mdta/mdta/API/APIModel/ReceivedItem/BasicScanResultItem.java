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
        private int PermissionLevelNoProtection;
        private int PermissionLevelNormal;
        private int PermissionLevelDangerous;
        private int PermissionLevelSignature;
        private int PermissionLevelSystemOrSignature;
        private int PermissionLevelSystem;
        private int PermissionLevelSignatureOrPrivileged;
        private int PermissionLevelNoThirdParty;

        @Override
        public String toString() {
            return "PackageResult{" +
                    "PermissionsNumber=" + getPermissionsNumber() +
                    ", DeprecatedPermissionsNumber=" + DeprecatedPermissionsNumber +
                    ", FinancialImpactPermissionsNumber=" + FinancialImpactPermissionsNumber +
                    ", PrivacyImpactPermissionsNumber=" + PrivacyImpactPermissionsNumber +
                    ", SystemImpactPermissionsNumber=" + SystemImpactPermissionsNumber +
                    ", BatteryImpactPermissionsNumber=" + BatteryImpactPermissionsNumber +
                    ", LocationImpactPermissionsNumber=" + LocationImpactPermissionsNumber +
                    ", PermissionLevelNoProtection=" + PermissionLevelNoProtection +
                    ", PermissionLevelNormal=" + PermissionLevelNormal +
                    ", PermissionLevelDangerous=" + PermissionLevelDangerous +
                    ", PermissionLevelSignature=" + PermissionLevelSignature +
                    ", PermissionLevelSystemOrSignature=" + PermissionLevelSystemOrSignature +
                    ", PermissionLevelSystem=" + PermissionLevelSystem +
                    ", PermissionLevelSignatureOrPrivileged=" + PermissionLevelSignatureOrPrivileged +
                    ", PermissionLevelNoThirdParty=" + PermissionLevelNoThirdParty +
                    '}';
        }

        public String getPackageName() {
            return PackageName;
        }

        public int getPermissionsNumber() {
            return PermissionLevelNoProtection +
                    PermissionLevelNormal +
                    PermissionLevelDangerous
                    + PermissionLevelSignature
                    + PermissionLevelSystemOrSignature
                    + PermissionLevelSystem
                    + PermissionLevelSignatureOrPrivileged
                    + PermissionLevelNoThirdParty;
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

        public int getPermissionLevelNoProtection() {
            return PermissionLevelNoProtection;
        }

        public int getPermissionLevelNormal() {
            return PermissionLevelNormal;
        }

        public int getPermissionLevelDangerous() {
            return PermissionLevelDangerous;
        }

        public int getPermissionLevelSignature() {
            return PermissionLevelSignature;
        }

        public int getPermissionLevelSystemOrSignature() {
            return PermissionLevelSystemOrSignature;
        }

        public int getPermissionLevelSystem() {
            return PermissionLevelSystem;
        }

        public int getPermissionLevelSignatureOrPrivileged() {
            return PermissionLevelSignatureOrPrivileged;
        }

        public int getPermissionLevelNoThirdParty() {
            return PermissionLevelNoThirdParty;
        }
    }

}
