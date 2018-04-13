package com.example.j_spu.githubevents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    private static final String LOG_TAG = EventAdapter.class.getSimpleName();

    public EventAdapter(Activity context, List<Event> events) { super(context, 0, events); }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        final Event currentEvent = getItem(position);

        listItemView.setOnClickListener(eventClicked(getContext(), listItemView, currentEvent));

        ImageView userAvatar = (ImageView) listItemView.findViewById(R.id.avatar);
        userAvatar.setImageBitmap(currentEvent.getUser().getAvatar());

        TextView username = (TextView) listItemView.findViewById(R.id.username_txt);
        username.setText(currentEvent.getUser().getUsername());

        TextView repoTxt = (TextView) listItemView.findViewById(R.id.repo_txt);
        repoTxt.setText(currentEvent.getRepoName());

        TextView type = (TextView) listItemView.findViewById(R.id.type_txt);
        type.setText(currentEvent.getType());

        TextView descriptionTxt = listItemView.findViewById(R.id.description_txt);

        descriptionTxt.setText((currentEvent.getDescription().equals("null") ? "No description." : currentEvent.getDescription()));

        TextView starTxt = listItemView.findViewById(R.id.star_txt);
        starTxt.setText(currentEvent.getStars() == -1 ? "" :(String.valueOf(currentEvent.getStars())));
        Log.e(LOG_TAG, "<<<STAR: " + String.valueOf(currentEvent.getStars()));

        TextView watchTxt = listItemView.findViewById(R.id.watch_txt);
        watchTxt.setText(currentEvent.getWatches() == -1 ? "" : String.valueOf(currentEvent.getWatches()));
        Log.e(LOG_TAG, "<<<WATCH: " + String.valueOf(currentEvent.getWatches()));

        TextView forkTxt = listItemView.findViewById(R.id.fork_txt);
        forkTxt.setText(currentEvent.getForks() == -1 ? "" : String.valueOf(currentEvent.getForks()));
        Log.e(LOG_TAG, "<<<FORK: " + String.valueOf(currentEvent.getForks()));

        TextView dateTxt = listItemView.findViewById(R.id.date_txt);
        dateTxt.setText(currentEvent.getDate());
        Log.e(LOG_TAG, "<<<DATE: " + currentEvent.getDate());

        return listItemView;
    }

    /**
     * this method contains the OnClickListener for each event item in the list. Any modifications
     * to the click event should happen in this method
     * @param context
     * @param listItemView
     * @param currentEvent
     * @return the click listener for the event
     */
    private static View.OnClickListener eventClicked(final Context context, final View listItemView, final Event currentEvent) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout detailsLayout = (ConstraintLayout) listItemView.findViewById(R.id.details);
                TextView typeTxt = listItemView.findViewById(R.id.type_txt);
                TextView repoTxt = listItemView.findViewById(R.id.repo_txt);
                ImageView avatar = listItemView.findViewById(R.id.avatar);

                Button repoBtn = listItemView.findViewById(R.id.repo_btn);

                if (detailsLayout.getVisibility() == View.GONE) {
                    detailsLayout.setVisibility(View.VISIBLE);
                    typeTxt.setMaxLines(2);
                    repoTxt.setMaxLines(2);
                    avatar.setVisibility(View.VISIBLE);

                    repoBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent implicitWebIntent =
                                    new Intent(Intent.ACTION_VIEW, Uri.parse(currentEvent.getRepoUrl()));
                            Intent chooser = Intent.createChooser(implicitWebIntent, "Which Method?");

                            Log.i(LOG_TAG, "Click button. Starting Intent...");

                            if (implicitWebIntent.resolveActivity(context.getPackageManager()) != null) {
                                Log.i(LOG_TAG, "Click button. Starting Intent.");
                                context.startActivity(chooser);
                                if (currentEvent.getRepoUrl().contains("?tab")) {
                                    Toast.makeText(context, "Repository doesn't exist anymore. Being redirected to " +
                                            currentEvent.getUser().getUsername() + "'s repositories.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                context.startActivity(implicitWebIntent);
                                if (currentEvent.getRepoUrl().contains("?tab")) {
                                    Toast.makeText(context, "Repository doesn't exist anymore. Being redirected to " +
                                            currentEvent.getUser().getUsername() + "'s repositories.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else {
                    detailsLayout.setVisibility(View.GONE);
                    typeTxt.setMaxLines(1);
                    repoTxt.setMaxLines(1);
                    avatar.setVisibility(View.GONE); }
            }
        };
    }
}
