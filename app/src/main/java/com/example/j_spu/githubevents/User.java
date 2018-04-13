package com.example.j_spu.githubevents;

import android.graphics.Bitmap;

public class User {

    private String mUsername;
    private Bitmap mAvatar;

    public User(String username, Bitmap avatar) {
        mUsername = username;
        mAvatar = avatar;
    }

    /**
     * gets the username of the user
     * @return username (String)
     */
    public String getUsername() { return mUsername; }

    /**
     *
     * @return user's avatar (Bitmap)
     */
    public Bitmap getAvatar() { return mAvatar; }

    /**
     *  sets the user's avatar (Bitmap)
     * @param avatar
     */
    public void setAvatar(Bitmap avatar) { mAvatar = avatar; }

}
