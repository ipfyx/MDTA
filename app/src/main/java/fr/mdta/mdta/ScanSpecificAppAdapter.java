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

import fr.mdta.mdta.Scans.Scan;


public class ScanSpecificAppAdapter extends RecyclerView.Adapter<ScanSpecificAppAdapter.ViewHolder> {

    /**
     * Applicatioan context
     */
    private Context mContext;
    /**
     * ArrayList of elements to display within the recyclerview according to the specified adapter
     */
    private ArrayList<Scan> mData = new ArrayList<>();

    /**
     * Constructor of ScanSpecificAppAdapter
     *
     * @param context
     * @param items
     */
    public ScanSpecificAppAdapter(Context context, ArrayList<Scan> items) {
        this.mContext = context;
        this.mData = items;
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

        //Fill UI with scan value
        holder.mScanNameTextView.setText(scan.getmScanName());
        holder.mScanDescriptionTextView.setText(scan.getmScanDescription());

        //Progress bar animation according to the scans state(out of 100)
        final int max = 100;
        holder.mProgressBar.setVisibility(View.VISIBLE);
        holder.mProgressBar.setMax(max);
        holder.mHandler.postDelayed(new Runnable() {

            public void run() {
                if (scan.getmState() == 0) {
                    holder.mScanStatusTextView.setText(mContext.getResources().getText(R.string.status_scan_pending));
                    holder.mPercentTextView.setText("0%");
                } else if (scan.getmState() < max) {
                    holder.mScanStatusTextView.setText(mContext.getResources().getText(R.string.status_scan_running));
                    holder.mProgressBar.setProgress(scan.getmState());
                    int percent_num = (holder.mCounter * 100 / max);
                    holder.mPercentTextView.setText(percent_num + "%");
                    holder.mHandler.postDelayed(this, 5);
                } else {
                    holder.mPercentTextView.setText("100% ");
                    holder.mScanStatusTextView.setText(mContext.getResources().getText(R.string.status_scan_over));
                }
            }
        }, 5);

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
        private Handler mHandler = new Handler();

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


