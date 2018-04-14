package com.example.j_spu.githubevents;

/**
 * Created by Owner on 10/18/2017.
 */

import android.graphics.Bitmap;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TabPerformedEvents extends Fragment implements LoaderManager.LoaderCallbacks<List<Event>> {

    public static final String LOG_TAG = TabPerformedEvents.class.getSimpleName();
    private static final int EVENT_LOADER_ID = 2;
    private EventAdapter mAdapter;
    private ImageView mEmptyStateImgView;
    private TextView mEmptyStateTxtView;
    private LinearLayout mEmptyStateLinLayout;
    private ProgressBar mLoadingBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageButton mRefreshBtn;
    private ImageButton mAccountBtn;
    private TextView mUsernameHeader;
    private static String baseUrl =
            "https://api.github.com/users/:username/events/public?client_id=e53bb94f9c15a36afebd&client_secret=f23dfaa03e54dfbb4ca57740ba565d8043c4201b";

//    :username

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

        mRefreshBtn = (ImageButton) container.getRootView().findViewById(R.id.refresh_button);
        mRefreshBtn.setOnClickListener(refreshButtonClicked(getContext()));

        mAccountBtn = (ImageButton) container.getRootView().findViewById(R.id.account_button);
        mAccountBtn.setOnClickListener(accountButtonClicked(getContext(), container, mRefreshBtn));

        mUsernameHeader = (TextView) container.getRootView().findViewById(R.id.username_txt);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mEmptyStateLinLayout.setVisibility(View.GONE);
                mAdapter.clear();
                resetLoader();
                mSwipeRefreshLayout.setRefreshing(false);
                TabPerformedEvents newEvent = new TabPerformedEvents();
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

    public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {

        Log.i(LOG_TAG, "--ON CREATE LOADER--");
        mEmptyStateLinLayout.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        mRefreshBtn.setVisibility(View.GONE);
        // Create a new loader for the given URL
        return new EventLoader(getContext(), baseUrl);
    }

    public void onLoadFinished(Loader<List<Event>> loader, List<Event> events) {
        Log.i(LOG_TAG, "--ON LOAD FINISHED--");

        mLoadingBar.setVisibility(View.GONE);
        mRefreshBtn.setVisibility(View.VISIBLE);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if ( events != null && !events.isEmpty()) {
            mAdapter.addAll(events);
            mUsernameHeader.setText(PublicEventActivity.mUser.getUsername());
            mUsernameHeader.setVisibility(View.VISIBLE);
            mAccountBtn.setImageBitmap(Bitmap.createScaledBitmap((PublicEventActivity.mUser.getAvatar()), 100, 100, true));
        } else {
            PublicEventActivity.mUser = null;
            mEmptyStateImgView.setImageResource(R.drawable.ic_account_circle_black_24dp);
            mEmptyStateTxtView.setText("Sign in to see your public events.");
            mEmptyStateLinLayout.setVisibility(View.VISIBLE);
            mUsernameHeader.setText("");
            mUsernameHeader.setVisibility(View.GONE);
            mAccountBtn.setImageResource(R.drawable.ic_account_circle_white_36dp);
        }
    }

    public void onLoaderReset(Loader<List<Event>> loader) {
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
    private View.OnClickListener accountButtonClicked(final Context context, final ViewGroup container, final ImageView button) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PublicEventActivity.mUser == null) {
                    // sign in
                    signInDialog(context, container, button, "in");
                    return;
                }

                // ask to sign out
                signInDialog(context, container, button, "out");
                return;
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
                if(PublicEventActivity.mUser != null) {
                    mEmptyStateLinLayout.setVisibility(View.GONE);
                    mAdapter.clear();
                    resetLoader();
                }
            }
        };
    }

    /**
     * this method is used to display a pop up window to the user to either sign in or sign out
     * depending in the given method
     * @param context
     * @param parent view that the popup window is going above
     * @param button button used to refresh the loader
     * @param method whether the popup window is sign in or sign out. pass "in" or "out" as the value
     */
    private static void signInDialog(final Context context, ViewGroup parent, final ImageView button, String method) {

        String header = "";
        String message = "";
        String actionBtn = "";
        boolean editTxtVisible = true;

        switch (method) {
            case "in":
                header = "Sign In";
                message = "Sign in using your GitHub credentials.";
                actionBtn = "Sign In";
                editTxtVisible = true;
                break;
            case "out":
                header = "Sign Out";
                message = "Are you sure you want to sign out?";
                actionBtn = "Sign Out";
                editTxtVisible = false;
                break;
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewPager list_layout = (ViewPager) parent.findViewById(R.id.pager);
        final View deletePopupView = inflater.from(context).inflate(R.layout.signin_confirmation, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow deletePopupWindow = new PopupWindow(deletePopupView, width, height, focusable);
        deletePopupWindow.showAtLocation(list_layout, Gravity.CENTER, 0, 0);

        TextView headerTxt = (TextView) deletePopupView.findViewById(R.id.header_txt);
        headerTxt.setText(header);

        TextView messageTxt = (TextView) deletePopupView.findViewById(R.id.message_txt);
        messageTxt.setText(message);

        final EditText usernameTxt = deletePopupView.findViewById(R.id.username_txt);
//        final EditText passwordTxt = deletePopupView.findViewById(R.id.password_txt);
        usernameTxt.setVisibility(editTxtVisible ? View.VISIBLE : View.GONE);
//        passwordTxt.setVisibility(editTxtVisible ? View.VISIBLE : View.GONE);

        final TextView positiveTxt = (TextView) deletePopupView.findViewById(R.id.positive_action);
        positiveTxt.setText(actionBtn);
        positiveTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (positiveTxt.getText().equals("Sign In")) {
                    String[] creds = {usernameTxt.getText().toString(), /*passwordTxt.getText().toString()*/};
                    baseUrl = baseUrl.replace(":username", creds[0]);
                    QueryUtils.creds = creds;

                    PublicEventActivity.mUser = new User(creds[0], null);
                    button.performClick();
                } else {
                    baseUrl = baseUrl.replace(PublicEventActivity.mUser.getUsername(), ":username");
                    button.performClick();
                    PublicEventActivity.mUser = null;
                }

                deletePopupWindow.dismiss();
            }
        });

        TextView cancelAction = (TextView) deletePopupView.findViewById(R.id.cancel_action);
        cancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePopupWindow.dismiss();
//                mainView.getForeground().setAlpha(0);
            }
        });

        // handles when the pop up window is closed via touch outside of window or
        // via back button
        deletePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                mainView.getForeground().setAlpha(0);
                deletePopupWindow.dismiss();
            }
        });
    }
} // end of TabAll