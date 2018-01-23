package fr.mdta.mdta;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import fr.mdta.mdta.Model.Result;
import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Tools.CacheStorage;


public class ResultActivity extends AppCompatActivity implements ActionBar.TabListener {

    //static values
    public static final String KEY_RESULT_LIST = "keyresultlist";

    //UI components
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private ActionBar mActionBar;

    //Model
    private ArrayList<Result> mResults = new ArrayList<>();
    private ArrayList<Result> mWarnedResult = new ArrayList<>();
    private ArrayList<Result> mSafeResult = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //Retrieve results from cache
        try {
            mResults = (ArrayList<Result>) CacheStorage.readObject(getApplicationContext(), KEY_RESULT_LIST);
            CacheStorage.clearCache(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //Set the different list to isolate warned app and safe app
        for (int i = 0; i < mResults.size(); i++) {
            if (mResults.get(i).getmGlobalResult()) {
                mSafeResult.add(mResults.get(i));
            } else {
                mWarnedResult.add(mResults.get(i));
            }
        }

        //Retrieve UI components
        mViewPager = (ViewPager) findViewById(R.id.pager);


        //Set UI components
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());

        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mActionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            mActionBar.addTab(
                    mActionBar.newTab()
                            .setText(mPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }


    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    public static class ScreenSlidePageFragment extends Fragment {

        /**
         * ArrayList of elements to display within the recyclerview according to the specified adapter
         */
        private ArrayList<Result> mList;

        public ScreenSlidePageFragment() {
        }

        public ScreenSlidePageFragment(ArrayList<Result> mRecyclerView) {
            mList = mRecyclerView;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

            //Set UI elements and give the job to display to the custom adapter
            ResultActivity.CustomAdapter adapter = new ResultActivity.CustomAdapter(getActivity(), mList);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);


            return rootView;
        }


    }

    public static class CustomAdapter extends RecyclerView.Adapter<ResultActivity.CustomAdapter.ViewHolder> {
        /**
         * Launcher activity
         */
        private Activity mActivity;
        /**
         * ArrayList of elements to display within the recyclerview according to the specified adapter
         */
        private ArrayList<Result> mData;


        public CustomAdapter(Activity activity, ArrayList<Result> items) {
            this.mActivity = activity;
            this.mData = items;
        }


        @Override
        public ResultActivity.CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_result_item, parent, false);
            return new ResultActivity.CustomAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ResultActivity.CustomAdapter.ViewHolder holder, final int position) {

            final Result result = mData.get(position);
            final SimplifiedPackageInfo simplifiedPackageInfo = result.getmSimplifiedPackageInfo();

            //Fill UI components
            try {
                String pname = simplifiedPackageInfo.getPackageName();
                Drawable icon;
                icon = mActivity.getPackageManager().getApplicationIcon(pname);
                holder.mIcon.setImageDrawable(icon);
                holder.mAppName.setText(simplifiedPackageInfo.getAppName());
                holder.mPackageName.setText(simplifiedPackageInfo.getPackageName());
                if (result.getmGlobalResult()) {
                    holder.mResultImageView.setImageResource(R.drawable.checkmark);
                } else {
                    holder.mResultImageView.setImageResource(R.drawable.warning);
                }

            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            //Show result when an application is selected
            holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(mActivity, ResultSpecificAppActivity.class);
                    myIntent.putExtra(ResultSpecificAppActivity.KEY_RESULT, result);
                    mActivity.startActivity(myIntent);
                    mActivity.finish();
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
            private ImageView mIcon;
            private TextView mAppName;
            private TextView mPackageName;
            private ImageView mResultImageView;
            private LinearLayout mLinearLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                //Retrieve UI components
                mLinearLayout = (LinearLayout) itemView.findViewById(R.id.appLinearLayout);
                mIcon = (ImageView) itemView.findViewById(R.id.icon);
                mAppName = (TextView) itemView.findViewById(R.id.appName);
                mPackageName = (TextView) itemView.findViewById(R.id.packageName);
                mResultImageView = (ImageView) itemView.findViewById(R.id.scanResult);

            }
        }

    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        /**
         * Elements to display in the tab
         */
        public ArrayList<Result> mList;
        /**
         * Tab title
         */
        private String mTitle;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {
                case 0:
                    mList = mWarnedResult;
                    break;
                case 1:
                    mList = mSafeResult;
                    break;

            }

            return new ScreenSlidePageFragment(mList);

        }

        @Override
        public int getCount() {
            return 2;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    mTitle = "Warned Applications";
                    break;
                case 1:
                    mTitle = "Safe Applications";
                    break;

            }
            return mTitle;
        }
    }


}





















