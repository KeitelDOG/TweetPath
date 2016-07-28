package com.megalobiz.tweetpath.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KeitelRobespierre on 7/27/2016.
 */
public class User {

    private String name;
    private long id;
    private String screenName;
    private String profileImageUrl;

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJSON(JSONObject json) {
        User user = new User();

        try {
            user.name = json.getString("name");
            user.id = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //return a user
        return user;
    }
}
