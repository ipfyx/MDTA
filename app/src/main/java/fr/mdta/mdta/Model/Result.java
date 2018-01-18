package fr.mdta.mdta.Model;

import java.io.Serializable;
import java.util.ArrayList;

import fr.mdta.mdta.Scans.Scan;

/**
 * This class represent the result of each scans and the global result according to the specified SimplifiedPackageInfo
 */
public class Result implements Serializable {

    /**
     * Current Simplified Package Info
     */
    private SimplifiedPackageInfo mSimplifiedPackageInfo;
    /**
     * ScanResult element arraylist linked to the specified simplified package info
     */
    private ArrayList<ScanResult> mScanResults = new ArrayList<>();
    /**
     * Global Result value
     * - true: seems to be a clean application
     * - false: seems to be a dangerous application
     */
    private boolean mGlobalResult = true;

    /**
     * Constructor of a Result
     *
     * @param mSimplifiedPackageInfo
     * @param mScanResults
     */
    public Result(SimplifiedPackageInfo mSimplifiedPackageInfo, ArrayList<ScanResult> mScanResults) {
        this.mSimplifiedPackageInfo = mSimplifiedPackageInfo;
        this.mScanResults = mScanResults;
    }

    /**
     * Update and give the global scan result element of our specified simplified package info
     */
    public boolean getmGlobalResult() {
        //TODO Ponderate the result according to the kind of a scan
        for (int i = 0; i < mScanResults.size(); i++) {
            mGlobalResult &= mScanResults.get(i).mScanResult;
        }
        return mGlobalResult;
    }

    /**
     * Standard getter of simplified package info
     *
     * @return
     */
    public SimplifiedPackageInfo getmSimplifiedPackageInfo() {
        return mSimplifiedPackageInfo;
    }

    /**
     * Standard getter to access the scan results array list
     *
     * @return
     */
    public ArrayList<ScanResult> getmScanResults() {
        return mScanResults;
    }


    public static class ScanResult implements Serializable {

        /**
         * Scan Name
         */
        private String mScanName;
        /**
         * Scan Description
         */
        private String mScanDescription;
        /**
         * Scan Result
         * - true: seems to be a clean application
         * - false: seems to be a dangerous application
         */
        private boolean mScanResult;
        /**
         * Result Details according to the kind of scan
         */
        private Scan.SpecificResult mSpecificResult;

        /**
         * Cosntructor of a ScanResult
         *
         * @param mScanName
         * @param mScanDescription
         * @param mSpecificResult
         */
        public ScanResult(String mScanName, String mScanDescription, Scan.SpecificResult mSpecificResult) {
            this.mScanName = mScanName;
            this.mScanDescription = mScanDescription;
            this.mSpecificResult = mSpecificResult;
            this.mScanResult = mSpecificResult.ismStatus();
        }

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
         * Standard getter to access specific result
         *
         * @return
         */
        public Scan.SpecificResult getmSpecificResult() {
            return mSpecificResult;
        }
    }


}
