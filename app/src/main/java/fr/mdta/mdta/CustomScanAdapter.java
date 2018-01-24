package fr.mdta.mdta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.mdta.mdta.Model.Scan;

/**
 * Adapter to present scan possibilities in a custom scan
 */
public class CustomScanAdapter extends RecyclerView.Adapter<CustomScanAdapter.ViewHolder> {

    /**
     * Applicatioan context
     */
    private Context mContext;
    /**
     * ArrayList of elements to display within the recyclerview according to the specified adapter
     */
    private ArrayList<Scan> mData = new ArrayList<>();
    /**
     * Selected scan
     */
    private ArrayList<Scan> mChoosenScans = new ArrayList<>();

    /**
     * Constructor of CustomScan adapter
     *
     * @param context
     * @param items
     */
    public CustomScanAdapter(Context context, ArrayList<Scan> items) {
        this.mContext = context;
        this.mData = items;
    }

    /**
     * Allows access to the selected scans
     *
     * @return
     */
    public ArrayList<Scan> getmChoosenScans() {
        return mChoosenScans;
    }

    @Override
    public CustomScanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_scan_item, parent, false);
        return new CustomScanAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomScanAdapter.ViewHolder holder, final int position) {


        final Scan scan = mData.get(position);

        //Fill UI with scan value
        holder.mScanNameTextView.setText(scan.getmScanName());
        holder.mScanDescriptionTextView.setText(scan.getmScanDescription());
        holder.mScanCheckboxCheckbox.setChecked(false);
        //Logic to update the selected scans
        holder.mScanCheckboxCheckbox.setClickable(false); //---> blocked to avoid linearlayout/ checkbox confusion
        holder.mScanLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChoosenScans.contains(scan)) {
                    mChoosenScans.remove(scan);
                    holder.mScanCheckboxCheckbox.setChecked(false);
                } else {
                    mChoosenScans.add(scan);
                    holder.mScanCheckboxCheckbox.setChecked(true);
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
        private TextView mScanNameTextView;
        private TextView mScanDescriptionTextView;
        private CheckBox mScanCheckboxCheckbox;
        private LinearLayout mScanLinearLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            //Retrieve UI components
            mScanNameTextView = (TextView) itemView.findViewById(R.id.scanName);
            mScanDescriptionTextView = (TextView) itemView.findViewById(R.id.scanDescription);
            mScanCheckboxCheckbox = (CheckBox) itemView.findViewById(R.id.scanCheckbox);
            mScanLinearLayout = (LinearLayout) itemView.findViewById(R.id.scanLinearLayout);

        }
    }

}


