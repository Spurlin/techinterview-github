package com.example.j_spu.githubevents;

import java.util.Date;

public class Event {

    private User mUser;
    private String mType;
    private String mRepoUrl;
    private String mRepoName;
    private String mDescription;
    private int mStars;
    private int mWatches;
    private int mForks;
    private String mDate;

    public Event(User user, String type, String repoName, String repoUrl, String description, int stars, int watches, int forks, String date) {
        mUser = user;
        mType = type;
        mRepoName = repoName;
        mRepoUrl = repoUrl;
        mDescription = description;
        mStars = stars;
        mWatches = watches;
        mForks = forks;
        mDate = date;
    }

    /**
     * gets the user object that started the event
     * @return User object
     */
    public User getUser() { return mUser; }

    /**
     * gets the type of the event
     * @return event type (String)
     */
    public String getType() { return mType; }

    /**
     * gets the details for the event
     * @return event details (String)
     */
    public String getDescription() { return mDescription; }

    /**
     * gets the name to the event's repository
     * @return event repository (String)
     */
    public String getRepoName() { return mRepoName; }

    /**
     * gets the link to the event's repository
     * @return event repository (String)
     */
    public String getRepoUrl() { return mRepoUrl; }

    /**
     * gets the event's star count
     * @return event repository (String)
     */
    public int getStars() { return mStars; }

    /**
     * gets the event's watch count
     * @return event repository (String)
     */
    public int getWatches() { return mWatches; }

    /**
     * gets the event's fork count
     * @return event repository (String)
     */
    public int getForks() { return mForks; }

    /**
     * gets the date and time the event happened
     * @return event repository (String)
     */
    public String getDate() { return mDate; }

}
