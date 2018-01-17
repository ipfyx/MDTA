package fr.mdta.mdta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.mdta.mdta.Model.Result;


public class ResultSpecificAppAdapter extends RecyclerView.Adapter<ResultSpecificAppAdapter.ViewHolder> {

    /**
     * Applicatioan context
     */
    private Context mContext;
    /**
     * ArrayList of elements to display within the recyclerview according to the specified adapter
     */
    private ArrayList<Result.ScanResult> mData = new ArrayList<>();

    /**
     * Constructor of ResultSpecificAppAdapter
     *
     * @param context
     * @param scanResults
     */
    public ResultSpecificAppAdapter(Context context, ArrayList<Result.ScanResult> scanResults) {
        this.mContext = context;
        this.mData = scanResults;
    }


    @Override
    public ResultSpecificAppAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scan_result_item, parent, false);
        return new ResultSpecificAppAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ResultSpecificAppAdapter.ViewHolder holder, final int position) {


        final Result.ScanResult scanResult = mData.get(position);

        //Fill UI with scan value
        holder.mScanNameTextView.setText(scanResult.getmScanName());
        holder.mScanResultTextView.setText(scanResult.getmSpecificResult().getmResult());
        if (scanResult.getmSpecificResult().ismStatus()) {
            holder.mResultStatusImageView.setImageResource(R.drawable.checkmark);
        } else {
            holder.mResultStatusImageView.setImageResource(R.drawable.warning);
        }
        //Show details animation
        holder.mScanResultLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isDetailsExpanded) {
                    holder.mScanDetailsTextView.setText("");
                    holder.isDetailsExpanded = false;
                    holder.mExpandDetailsTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.mScanDetailsTextView.setText(scanResult.getmSpecificResult().getmDetails());
                    holder.isDetailsExpanded = true;
                    holder.mExpandDetailsTextView.setVisibility(View.GONE);
                }
            }
        });
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
        private ImageView mResultStatusImageView;
        private TextView mScanNameTextView;
        private TextView mScanResultTextView;
        private TextView mScanDetailsTextView;
        private LinearLayout mScanResultLinearLayout;
        private TextView mExpandDetailsTextView;
        private boolean isDetailsExpanded = false;


        public ViewHolder(View itemView) {
            super(itemView);
            //Retrieve UI components
            mResultStatusImageView = (ImageView) itemView.findViewById(R.id.resultStatusImageView);
            mScanNameTextView = (TextView) itemView.findViewById(R.id.scanName);
            mScanResultTextView = (TextView) itemView.findViewById(R.id.scanResult);
            mScanDetailsTextView = (TextView) itemView.findViewById(R.id.scanDetails);
            mScanResultLinearLayout = (LinearLayout) itemView.findViewById(R.id.scanResultLinearLayout);
            mExpandDetailsTextView = (TextView) itemView.findViewById(R.id.expandTextView);
        }
    }

}


