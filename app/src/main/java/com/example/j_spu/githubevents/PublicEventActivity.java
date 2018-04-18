package com.example.j_spu.githubevents;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PublicEventActivity extends AppCompatActivity {

    public static final String LOG_TAG = PublicEventActivity.class.getSimpleName();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ImageButton mRefresh;
    public static User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_event);

        // Create the adapter that will return a fragment for both
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set up the tabs for the main layout
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mRefresh = (ImageButton) findViewById(R.id.refresh_button);
        mRefresh.setOnClickListener(refreshButtonClicked());


    }

    /**
     * this method contains the OnClickListener for the refresh button. Any modifications
     * to the click event should happen in this method
     * @return the click listener for the refresh button
     */
    private View.OnClickListener refreshButtonClicked() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        TabAllEvents.mRefreshBtn.performClick();
                        break;
                    case 1:
                        TabPerformedEvents.mRefreshBtn.performClick();
                        break;
                }
            }
        };
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //Returning the current tabs
            switch (position) {
                case 0:
                    TabAllEvents tabAllEvents = new TabAllEvents();
                    return tabAllEvents;
                case 1:
                    TabPerformedEvents tabYourEvents = new TabPerformedEvents();
                    return tabYourEvents;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All Events";
                case 1:
                    return "Your Events";
            }
            return null;
        }
    }
}
