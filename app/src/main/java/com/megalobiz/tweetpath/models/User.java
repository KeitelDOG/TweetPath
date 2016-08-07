package com.megalobiz.tweetpath.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by KeitelRobespierre on 7/27/2016.
 */
public class User implements Serializable {

    private String name;
    private long id;
    private String screenName;
    private String profileImageUrl;
    private String tagLine;
    private int followerCount;
    private int friendCount;
    private int statusCount;

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

    public String getTagLine() {
        return tagLine;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public int getStatusCount() {
        return statusCount;
    }

    public static User fromJSON(JSONObject json) {
        User user = new User();

        try {
            user.name = json.getString("name");
            user.id = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
            user.tagLine = json.getString("description");
            user.followerCount = json.getInt("followers_count");
            user.friendCount = json.getInt("friends_count");
            user.statusCount = json.getInt("statuses_count");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //return a user
        return user;
    }
}
