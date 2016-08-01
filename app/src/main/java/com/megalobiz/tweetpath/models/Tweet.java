package com.megalobiz.tweetpath.models;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KeitelRobespierre on 7/27/2016.
 * parse the JSON + Store the data, encapsulate state login or display logic
 */
public class Tweet implements Serializable {

    private String body;
    private long uid;
    private User user;
    private String createdAt;
    private ArrayList<String> photoUrls;

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public ArrayList<String> getPhotoUrls() {
        return photoUrls;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        // extract value from the json and store them
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

            // take tweet media if Json has entities and media
            tweet.photoUrls = new ArrayList<>();
            if (jsonObject.has("entities") && jsonObject.getJSONObject("entities").has("media")) {
                JSONArray medias = jsonObject.getJSONObject("entities").getJSONArray("media");
                for (int i = 0; i < medias.length(); i++) {
                    JSONObject media = medias.getJSONObject(i);

                    if (media.getString("type").equals("photo")) {
                        tweet.photoUrls.add(media.getString("media_url"));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return the tweet object
        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(jsonArray.getJSONObject(i));
                if(tweet != null) {
                    tweets.add(tweet);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }
}
