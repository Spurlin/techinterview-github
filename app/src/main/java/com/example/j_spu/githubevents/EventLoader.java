package com.example.j_spu.githubevents;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class EventLoader extends AsyncTaskLoader<List<Event>> {

    private static final String LOG_TAG = EventLoader.class.getSimpleName();
    private String mUrl;
    private Context mContext;

    public EventLoader(Context context, String url) {
        super(context);
        mContext = context;
        mUrl = url;
    }

    @Override
    public List<Event> loadInBackground() {
        if (mUrl == null) { return null; }

        List<Event> events = QueryUtils.fetchEventData(mUrl);
        return events;
    }

}
