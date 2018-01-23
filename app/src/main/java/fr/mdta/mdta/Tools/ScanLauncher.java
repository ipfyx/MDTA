package fr.mdta.mdta.Tools;


import java.util.ArrayList;

import fr.mdta.mdta.Scans.Scan;

/**
 * ScanLauncher is responsible to manage scan concurrency and to launch scans
 */
public class ScanLauncher {

    /**
     * ScanLauncher Singleton for out access
     */
    private static ScanLauncher mScanLauncherInstance;
    /**
     * List to give as parameter when the scan is terminated
     */
    public ArrayList<Scan> mResultScans = new ArrayList<>();
    private ScanLauncher mLauncherSerial;
    private ScanLauncher mLauncherParralel;
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
     * Type of running launcher
     */
    private ScanLauncher.TypeOfLauncher mTypeOfLauncher;

    /**
     * Singleton access method
     *
     * @return
     * @throws ScanLauncherException
     */
    public static ScanLauncher getInstance() {
        if (mScanLauncherInstance == null) {
            mScanLauncherInstance = new ScanLauncher();
        }
        return mScanLauncherInstance;
    }

    /**
     * Init method before a scan, to avoid memory of wrong value in every logic controller attributes
     */
    private void initForScan(TypeOfLauncher typeOfLauncher) {
        mResultScans = new ArrayList<>();
        mSerialScans = new ArrayList<>();
        isAlreadyInUse = true;
        mParralelScanTerminatedCounter = 0;
        mTypeOfLauncher = typeOfLauncher;
    }

    /**
     * Public access to launch serial scan
     *
     * @param serialScansArrayList
     * @param callback
     */
    public void launchScansSerial(ArrayList<Scan> serialScansArrayList, ScanLauncherCallback callback) throws ScanLauncherException {
        if (mScanLauncherInstance.isAlreadyInUse) {
            throw ScanLauncherException.getScanLauncherAlreadyInUseException();
        }
        initForScan(TypeOfLauncher.SERIAL);
        mSerialScans = serialScansArrayList;
        checkToContinueScansSerial(callback);
    }

    /**
     * Launch next scan for a serial scan
     *
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
     *
     * @param parallelScansArrayList
     * @param callback
     */
    public void launchScansParallel(ArrayList<Scan> parallelScansArrayList, final ScanLauncherCallback callback) throws ScanLauncherException {
        if (mScanLauncherInstance.isAlreadyInUse) {
            throw ScanLauncherException.getScanLauncherAlreadyInUseException();
        }
        initForScan(TypeOfLauncher.PARALLEL);
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
     *
     * @param serialScansArrayList
     * @param parallelScansArrayList
     * @param callback
     */
    public void launchScansParallelAndForcedSerialScans(ArrayList<Scan> serialScansArrayList, ArrayList<Scan> parallelScansArrayList, final ScanLauncherCallback callback) throws ScanLauncherException {
        if (mScanLauncherInstance.isAlreadyInUse) {
            throw ScanLauncherException.getScanLauncherAlreadyInUseException();
        }
        initForScan(TypeOfLauncher.MIX);
        mLauncherSerial = new ScanLauncher();
        mLauncherParralel = new ScanLauncher();

        mLauncherSerial.launchScansSerial(serialScansArrayList, new ScanLauncherCallback() {
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
        mLauncherParralel.launchScansParallel(parallelScansArrayList, new ScanLauncherCallback() {
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
     * Calculate and give scanglobalstate according to the current process of scans managed by the launcher
     *
     * @return
     */
    public int getScansGlobalState() {
        int globalStateValue = 0;
        switch (mTypeOfLauncher) {
            case SERIAL:
                float finishedScan = mResultScans.size();
                if (mSerialScans.size() > 0) {
                    float waitingScan = mSerialScans.size();
                    float runningScanState = mSerialScans.get(0).getmState();
                    globalStateValue = (int) (100 * ((finishedScan / (finishedScan + waitingScan))) + runningScanState / 100);
                } else {
                    globalStateValue = 100;
                }
                break;
            case PARALLEL:
                for (int i = 0; i < mResultScans.size(); i++) {
                    globalStateValue += mResultScans.get(i).getmState();
                }
                break;
            case MIX:
                globalStateValue = (mLauncherParralel.getScansGlobalState() + mLauncherSerial.getScansGlobalState()) / 2;
                break;
        }
        return globalStateValue;
    }

    /**
     * Retrieve scan states
     *
     * @return
     */
    public ArrayList<Integer> getIndividualScanState() {
        ArrayList<Integer> scansState = new ArrayList<>();
        switch (mTypeOfLauncher) {
            case SERIAL:
                for (int i = 0; i < mResultScans.size(); i++) {
                    scansState.add((int) mResultScans.get(i).getmState());
                }
                for (int i = 0; i < mSerialScans.size(); i++) {
                    scansState.add((int) mSerialScans.get(i).getmState());
                }
                break;
            case PARALLEL:
                for (int i = 0; i < mResultScans.size(); i++) {
                    scansState.add((int) mResultScans.get(i).getmState());
                }
                break;
            case MIX:
                break;
        }
        return scansState;
    }

    private enum TypeOfLauncher {
        SERIAL,
        PARALLEL,
        MIX
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
        private static final String EXCEPTION_TEXT_ALREADY_IN_USE = "Launcher is already in use";
        private static final String EXCEPTION_TEXT_NOT_IN_USE = "Launcher is not in use";

        private ScanLauncherException(String message) {
            super(message);
        }

        public static ScanLauncherException getScanLauncherAlreadyInUseException() {
            return new ScanLauncherException(EXCEPTION_TEXT_ALREADY_IN_USE);
        }

        public static ScanLauncherException getScanLauncherNotInUseException() {
            return new ScanLauncherException(EXCEPTION_TEXT_NOT_IN_USE);
        }

    }
}
