package fr.mdta.mdta;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import fr.mdta.mdta.Model.Scan;
import fr.mdta.mdta.Tools.ScanLauncher;


public class ScanSpecificAppAdapter extends RecyclerView.Adapter<ScanSpecificAppAdapter.ViewHolder> {

    /**
     * Applicatioan context
     */
    private Context mContext;
    /**
     * ArrayList of elements to display within the recyclerview according to the specified adapter
     */
    private ArrayList<Scan> mData = new ArrayList<>();

    private Handler mHandler = new Handler();

    private ProgressBar[] mProgressBars;
    private TextView[] mScanStatusTextViews;
    private TextView[] mPercentTextViews;


    /**
     * Constructor of ScanSpecificAppAdapter
     *
     * @param context
     * @param items
     */
    public ScanSpecificAppAdapter(Context context, ArrayList<Scan> items) {
        this.mContext = context;
        this.mData.addAll(items);
        this.mProgressBars = new ProgressBar[this.mData.size()];
        this.mScanStatusTextViews = new TextView[this.mData.size()];
        this.mPercentTextViews = new TextView[this.mData.size()];

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Integer> scanStates = ScanLauncher.getInstance().getIndividualScanState();
                for (int i = 0; i < scanStates.size(); i++) {
                    int value = scanStates.get(i);
                    if (value == 0) {
                        mScanStatusTextViews[i].setText(mContext.getResources().getText(R.string.status_scan_pending));
                        mPercentTextViews[i].setText("0%");
                    } else if (value < 100) {
                        mScanStatusTextViews[i].setText(mContext.getResources().getText(R.string.status_scan_running));
                        mProgressBars[i].setProgress(value);
                        mPercentTextViews[i].setText(value + "%");
                    } else {
                        mPercentTextViews[i].setText("100% ");
                        mProgressBars[i].setProgress(100);
                        mScanStatusTextViews[i].setText(mContext.getResources().getText(R.string.status_scan_over));
                    }
                }
                mHandler.postDelayed(this, 1000);

            }
        }, 1000);
    }


    @Override
    public ScanSpecificAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scan_item, parent, false);
        return new ScanSpecificAppAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ScanSpecificAppAdapter.ViewHolder holder, final int position) {


        final Scan scan = mData.get(position);

        mProgressBars[position] = holder.mProgressBar;
        mPercentTextViews[position] = holder.mPercentTextView;
        mScanStatusTextViews[position] = holder.mScanStatusTextView;

        //Fill UI with scan value
        holder.mScanNameTextView.setText(scan.getmScanName());
        holder.mScanDescriptionTextView.setText(scan.getmScanDescription());

        //Progress bar animation according to the scans state(out of 100)
        final int max = 100;
        holder.mProgressBar.setVisibility(View.VISIBLE);
        holder.mProgressBar.setMax(max);


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //UI components
        private ProgressBar mProgressBar;
        private TextView mPercentTextView;
        private TextView mScanNameTextView;
        private TextView mScanDescriptionTextView;
        private TextView mScanStatusTextView;
        private int mCounter;

        public ViewHolder(View itemView) {
            super(itemView);
            //Retrieve UI components
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.scanProgressBar);
            mPercentTextView = (TextView) itemView.findViewById(R.id.progessbarPercent);
            mScanNameTextView = (TextView) itemView.findViewById(R.id.scanName);
            mScanDescriptionTextView = (TextView) itemView.findViewById(R.id.scanDescription);
            mScanStatusTextView = (TextView) itemView.findViewById(R.id.scanStatus);

        }
    }

}


