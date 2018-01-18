package fr.mdta.mdta.Tools;


import java.util.ArrayList;

import fr.mdta.mdta.Model.Scan;

/**
 * ScanLauncher is responsible to manage scan concurrency and to launch scans
 */
public class ScanLauncher {

    /**
     * ScanLauncher Singleton for out access
     */
    private static ScanLauncher mScanLauncherInstance;
    /**
     * Control and forbid concurrent access on scanlauncher
     */
    private boolean isAlreadyInUse = false;
    /**
     * Used to stock all not already launch serial scans
     */
    private ArrayList<Scan> mSerialScans = new ArrayList<>();
    /**
     * Used to know how many parralel scans are terminated, parralel scan is over when this value is equal to mResultScans
     */
    private int mParralelScanTerminatedCounter;
    /**
     * List to give as parameter when the scan is terminated
     */
    private ArrayList<Scan> mResultScans = new ArrayList<>();

    /**
     * Singleton access method
     * @return
     * @throws ScanLauncherException
     */
    public static ScanLauncher getInstance() throws ScanLauncherException {
        if (mScanLauncherInstance == null) {
            mScanLauncherInstance = new ScanLauncher();
        }
        if (mScanLauncherInstance.isAlreadyInUse) {
            throw new ScanLauncherException();
        }
        return mScanLauncherInstance;
    }

    /**
     * Init method before a scan, to avoid memory of wrong value in every logic controller attributes
     */
    private void initForScan() {
        mResultScans = new ArrayList<>();
        mSerialScans = new ArrayList<>();
        isAlreadyInUse = true;
        mParralelScanTerminatedCounter = 0;
    }

    /**
     * Public access to launch serial scan
     * @param serialScansArrayList
     * @param callback
     */
    public void launchScansSerial(ArrayList<Scan> serialScansArrayList, ScanLauncherCallback callback) {
        initForScan();
        mSerialScans = serialScansArrayList;
        checkToContinueScansSerial(callback);
    }

    /**
     * Launch next scan for a serial scan
     * @param callback
     */
    private void checkToContinueScansSerial(final ScanLauncherCallback callback) {
        if (!mSerialScans.isEmpty()) {
            mSerialScans.get(0).launchScan(new Scan.ScanCallback() {
                @Override
                public void OnScanTerminated() {
                    mResultScans.add(mSerialScans.remove(0));
                    checkToContinueScansSerial(callback);
                }
            });
        } else {
            callback.OnScansTerminated(mResultScans);
            isAlreadyInUse = false;
        }
    }

    /**
     * Public access to launch parralel scan
     * @param parallelScansArrayList
     * @param callback
     */
    public void launchScansParallel(ArrayList<Scan> parallelScansArrayList, final ScanLauncherCallback callback) {
        initForScan();
        mResultScans = parallelScansArrayList;
        if (!mResultScans.isEmpty()) {
            for (int i = 0; i < mResultScans.size(); i++) {
                mResultScans.get(i).launchScan(new Scan.ScanCallback() {
                    @Override
                    public void OnScanTerminated() {
                        mParralelScanTerminatedCounter++;
                        if (mParralelScanTerminatedCounter == mResultScans.size()) {
                            callback.OnScansTerminated(mResultScans);
                            isAlreadyInUse = false;
                        }
                    }
                });
            }
        } else {
            callback.OnScansTerminated(mResultScans);
            isAlreadyInUse = false;
        }
    }

    /**
     * Public access to launch a parralel scan specifying that some scan has to be launch one after the other
     * @param serialScansArrayList
     * @param parallelScansArrayList
     * @param callback
     */
    public void launchScansParallelAndForcedSerialScans(ArrayList<Scan> serialScansArrayList, ArrayList<Scan> parallelScansArrayList, final ScanLauncherCallback callback) {
        initForScan();
        ScanLauncher launcherSerial = new ScanLauncher();
        ScanLauncher launcherParralel = new ScanLauncher();

        launcherSerial.launchScansSerial(serialScansArrayList, new ScanLauncherCallback() {
            @Override
            public void OnScansTerminated(ArrayList<Scan> arrayListScanWithResult) {
                //it is just like having two parallels scan
                mParralelScanTerminatedCounter += 1;
                mResultScans.addAll(arrayListScanWithResult);
                if (mParralelScanTerminatedCounter == 2) {
                    callback.OnScansTerminated(mResultScans);
                }
            }
        });
        launcherParralel.launchScansParallel(parallelScansArrayList, new ScanLauncherCallback() {
            @Override
            public void OnScansTerminated(ArrayList<Scan> arrayListScanWithResult) {
                //it is just like having two parallels scan
                mParralelScanTerminatedCounter += 1;
                mResultScans.addAll(arrayListScanWithResult);
                if (mParralelScanTerminatedCounter == 2) {
                    callback.OnScansTerminated(mResultScans);
                }
            }
        });

    }

    /**
     * ScanLauncherCallback interface
     */
    public interface ScanLauncherCallback {
        void OnScansTerminated(ArrayList<Scan> arrayListScanWithResult);
    }

    /**
     * ScanLauncherException class
     */
    public static class ScanLauncherException extends Exception {
        private static final String EXCEPTION_TEXT = "Launcher is already in use";

        public ScanLauncherException() {
            super(EXCEPTION_TEXT);
        }
    }
}
