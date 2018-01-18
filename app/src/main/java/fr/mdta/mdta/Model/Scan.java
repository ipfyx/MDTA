package fr.mdta.mdta.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represent any scans, every scan should be extended from this class, that makes the common basics of a scan.
 */
public abstract class Scan implements Serializable {

    /**
     * Link the specific result of a scan on a the specified simplified package info
     */
    protected HashMap<SimplifiedPackageInfo, SpecificResult> mResults;
    /**
     * Value used to make animation and to keep the state(generally out of 100) of a scan
     */
    protected int mState = 0;
    /**
     * Represents the scan name
     */
    private String mScanName;
    /**
     * Represents the scan description
     */
    private String mScanDescription;
    /**
     * Represents the list of all the simplifiedpackageinfos which needs to be scan
     */
    private ArrayList<SimplifiedPackageInfo> mSimplifiedPackageInfos;

    /**
     * @param scanName
     * @param scanDescription
     * @param simplifiedPackageInfos
     */
    //TODO should become protected
    public Scan(String scanName, String scanDescription, ArrayList<SimplifiedPackageInfo> simplifiedPackageInfos) {
        this.mScanName = scanName;
        this.mScanDescription = scanDescription;
        this.mSimplifiedPackageInfos = simplifiedPackageInfos;
        mResults = new HashMap<>();
    }

    /**
     * Access scan result of a specified simplified package info
     *
     * @param simplifiedPackageInfo
     * @return
     */
    public Result.ScanResult getScanResult(SimplifiedPackageInfo simplifiedPackageInfo) {
        Result.ScanResult result;
        SpecificResult specificResult = mResults.get(simplifiedPackageInfo);
        result = new Result.ScanResult(mScanName, mScanDescription, specificResult);
        return result;
    }

    /**
     * Method to launch a scan which should be implemented by the extender's class.
     *
     * @param callback
     */
    public abstract void launchScan(ScanCallback callback);

    /**
     * Method to cancel a scan which should be implemented by the extender's class.
     *
     * @param callback
     */
    public abstract void cancelScan(ScanCallback callback);

    /**
     * Method to update scan state which should be implemented by the extender's class.
     */
    protected abstract void updateState();

    /**
     * Standard getter to access scan name
     *
     * @return
     */
    public String getmScanName() {
        return mScanName;
    }

    /**
     * Standard getter to access scan description
     *
     * @return
     */
    public String getmScanDescription() {
        return mScanDescription;
    }

    /**
     * Standard getter to access the list of simplified package info concerned by the scan
     *
     * @return
     */
    public ArrayList<SimplifiedPackageInfo> getmSimplifiedPackageInfos() {
        return mSimplifiedPackageInfos;
    }

    /**
     * Standard getter to access the map of result for the current scan
     *
     * @return
     */
    public HashMap<SimplifiedPackageInfo, SpecificResult> getmResults() {
        return mResults;
    }

    /**
     * Standard getter to access the state(out of 100) of the scan
     *
     * @return
     */
    public int getmState() {
        return mState;
    }

    /**
     * Callback interface
     */
    public interface ScanCallback extends Serializable {
        void OnScanTerminated();

    }

    /**
     * Class which keep information about a scan result related to the current scan
     */
    public static class SpecificResult implements Serializable {

        /**
         * Scan result
         * - true: seems to be a clean application
         * - false: seems to be a dangerous application
         */
        private boolean mStatus;
        /**
         * Verbose scan result, allows to make distinction between two differents false result
         */
        private String mResult;
        /**
         * More information about the scan
         */
        private String mDetails;

        /**
         * Constructor to make a specific result
         *
         * @param mStatus
         * @param mResult
         * @param mDetails
         */
        public SpecificResult(boolean mStatus, String mResult, String mDetails) {
            this.mStatus = mStatus;
            this.mResult = mResult;
            this.mDetails = mDetails;
        }

        /**
         * Standard getter to access the scan result
         *
         * @return
         */
        public boolean ismStatus() {
            return mStatus;
        }

        /**
         * Standard getter to access the verbose scan result
         *
         * @return
         */
        public String getmResult() {
            return mResult;
        }

        /**
         * Standard getter to access the scan details
         *
         * @return
         */
        public String getmDetails() {
            return mDetails;
        }
    }
}
