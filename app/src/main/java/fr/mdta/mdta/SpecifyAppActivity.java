package fr.mdta.mdta;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
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

import java.util.ArrayList;

import fr.mdta.mdta.Model.SimplifiedPackageInfo;
import fr.mdta.mdta.Tools.PackageInfoFactory;


public class SpecifyAppActivity extends AppCompatActivity implements ActionBar.TabListener {

    //UI components
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private ActionBar mActionBar;

    //Model
    private ArrayList<SimplifiedPackageInfo> mSystemPackageInfos;
    private ArrayList<SimplifiedPackageInfo> mApplicationsPackageInfos;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //Set the different list to isolate system app and non-system app
        mSystemPackageInfos = PackageInfoFactory.getInstalledPackages(this, true);
        mApplicationsPackageInfos = PackageInfoFactory.getInstalledPackages(this, false);

        //Retrieve UI components
        mViewPager = (ViewPager) findViewById(R.id.pager);

        //Set UI components
        mPagerAdapter = new SpecifyAppActivity.ScreenSlidePagerAdapter(getSupportFragmentManager());

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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public static class ScreenSlidePageFragment extends Fragment {


        /**
         * ArrayList of elements to display within the recyclerview according to the specified adapter
         */
        private ArrayList<SimplifiedPackageInfo> mList;


        public ScreenSlidePageFragment() {
        }

        public ScreenSlidePageFragment(ArrayList<SimplifiedPackageInfo> mRecyclerView) {
            mList = mRecyclerView;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

            //Set UI elements and give the job to display to the custom adapter
            CustomAdapter adapter = new CustomAdapter(getActivity(), mList);
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);


            return rootView;
        }


    }

    /**
     * Inclass custom adapter to fill the different pager
     */
    public static class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        /**
         * Launcher activity
         */
        private Activity mActivity;
        /**
         * ArrayList of elements to display within the recyclerview according to the specified adapter
         */
        private ArrayList<SimplifiedPackageInfo> mData;


        public CustomAdapter(Activity activity, ArrayList<SimplifiedPackageInfo> items) {
            this.mActivity = activity;
            this.mData = items;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            final SimplifiedPackageInfo simplifiedPackageInfo = mData.get(position);

            //Fill UI components
            try {
                String pname = simplifiedPackageInfo.getPackageName();
                Drawable icon;
                icon = mActivity.getPackageManager().getApplicationIcon(pname);
                holder.mIcon.setImageDrawable(icon);
                holder.mAppName.setText(simplifiedPackageInfo.getAppName());
                holder.mPackageName.setText(simplifiedPackageInfo.getPackageName());

            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Launch scan when an application is selected
            holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(mActivity, ScanSpecificAppActivity.class);
                    myIntent.putExtra(ScanSpecificAppActivity.CURRENT_SIMPLIFIED_APP_PACKAGE_INFO, simplifiedPackageInfo);
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
            private LinearLayout mLinearLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                //Retrieve UI components
                mLinearLayout = (LinearLayout) itemView.findViewById(R.id.aaa);
                mIcon = (ImageView) itemView.findViewById(R.id.icon);
                mAppName = (TextView) itemView.findViewById(R.id.appName);
                mPackageName = (TextView) itemView.findViewById(R.id.packageName);

            }
        }

    }

    /**
     * Pager Adapter to represent the different tabs
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        /**
         * Tab title
         */
        private String mTitle;
        /**
         * Elements to display in the tab
         */
        private ArrayList<SimplifiedPackageInfo> mList;

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    mList = mApplicationsPackageInfos;
                    break;
                case 1:
                    mList = mSystemPackageInfos;
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
                    mTitle = "Your apps";
                    break;
                case 1:
                    mTitle = "System apps";
                    break;
            }
            return mTitle;
        }
    }


}





















