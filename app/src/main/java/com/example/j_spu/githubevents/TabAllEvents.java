package com.example.j_spu.githubevents;

/**
 * Created by Owner on 10/18/2017.
 */

import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TabAllEvents extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Event>> {

    public static final String LOG_TAG = TabAllEvents.class.getSimpleName();
    private static final int EVENT_LOADER_ID = 1;
    private EventAdapter mAdapter;
    private ImageView mEmptyStateImgView;
    private TextView mEmptyStateTxtView;
    private LinearLayout mEmptyStateLinLayout;
    private ProgressBar mLoadingBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageButton mRefreshBtn;
    private ImageButton mAccountBtn;
    private String baseUrl =
            "https://api.github.com/events?client_id=e53bb94f9c15a36afebd&client_secret=f23dfaa03e54dfbb4ca57740ba565d8043c4201b";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_events, container, false);
        super.onCreate(savedInstanceState);

        // Create a new blank {@link ArrayAdapter} of events
        mAdapter = new EventAdapter(getActivity(), new ArrayList<Event>());

        // Find a reference to the {@link ListView} in the layout
        ListView eventListView = (ListView) rootView.findViewById(R.id.list);

        mLoadingBar = (ProgressBar) rootView.findViewById(R.id.loadingPBar);
        mLoadingBar.setVisibility(View.GONE);

        mEmptyStateImgView = (ImageView) rootView.findViewById(R.id.empty_view_img);
        mEmptyStateTxtView = (TextView) rootView.findViewById(R.id.empty_view_txt);

        mEmptyStateLinLayout = (LinearLayout) rootView.findViewById(R.id.empty_view_layout);

        eventListView.setEmptyView(mEmptyStateLinLayout);

        mAccountBtn = (ImageButton) container.getRootView().findViewById(R.id.account_button);
        mAccountBtn.setOnClickListener(accountButtonClicked(getContext()));
//
        mRefreshBtn = (ImageButton) container.getRootView().findViewById(R.id.refresh_button);
        mRefreshBtn.setOnClickListener(refreshButtonClicked(getContext()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyStateLinLayout.setVisibility(View.GONE);
                mAdapter.clear();
                resetLoader();
                mSwipeRefreshLayout.setRefreshing(false);
                TabAllEvents newEvent = new TabAllEvents();
            }
        });

        // set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        eventListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loader
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EVENT_LOADER_ID, null, this);
            Log.i(LOG_TAG, "--INIT LOADER--");
        } else {
            // otherwise, display error
            // first, hide loading indicator so error message will be visible
            mLoadingBar.setVisibility(View.GONE);

            // update empty state with no connection error message
            mEmptyStateTxtView.setText(R.string.no_internet_connection);
            mEmptyStateImgView.setImageResource(R.drawable.ic_signal_wifi_off_black_48dp);
        }

        return rootView;
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {

        Log.i(LOG_TAG, "--ON CREATE LOADER--");
        mEmptyStateLinLayout.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        // Create a new loader for the given URL
        return new EventLoader(getContext(), baseUrl);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Event>> loader, List<Event> events) {
        Log.i(LOG_TAG, "--ON LOAD FINISHED--");

        mLoadingBar.setVisibility(View.GONE);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if ( events != null && !events.isEmpty()) {
            mAdapter.addAll(events);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Event>> loader) {
        // Loader reset, so we can clear out our existing data
        mAdapter.clear();
        Log.i(LOG_TAG, "--ON LOADER RESET");
    }

    private void resetLoader() {
        getLoaderManager().restartLoader(EVENT_LOADER_ID, null, this);
    }

    /**
     * this method contains the OnClickListener for the user account button. Any modifications
     * to the click event should happen in this method
     * @param context
     * @return the click listener for the user account button
     */
    private View.OnClickListener accountButtonClicked(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PublicEventActivity.mUser == null) {
                    // sign in
                    String username = "Spurlin";
                    String password = ":)spur594J1995";

                    resetLoader();
                    return;
                }

                // ask to sign out
                resetLoader();
            }
        };
    }

    /**
     * this method contains the OnClickListener for the refresh button. Any modifications
     * to the click event should happen in this method
     * @param context
     * @return the click listener for the refresh button
     */
    private View.OnClickListener refreshButtonClicked(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmptyStateLinLayout.setVisibility(View.GONE);
                mAdapter.clear();
                resetLoader();
            }
        };
    }
} // end of TabAll