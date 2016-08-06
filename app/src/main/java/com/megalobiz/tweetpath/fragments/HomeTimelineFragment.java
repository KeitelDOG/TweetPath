package com.megalobiz.tweetpath.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.TwitterApplication;
import com.megalobiz.tweetpath.TwitterClient;
import com.megalobiz.tweetpath.models.Tweet;
import com.megalobiz.tweetpath.models.User;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by KeitelRobespierre on 8/5/2016.
 */
public class HomeTimelineFragment extends TweetsListFragment {

    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        populateTimeline(Long.parseLong("0"));

        /*
        // Lookup the Swipe Container view//
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        //Listen for Swipe Refresh to fetch Moives again
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Make sure to call swipeContainer.setRefreshing(fasle) once the
                // network has completed successfully
                //clear list before refreshing
                fragmentTweetsList.clear();
                populateTimeline(Long.parseLong("0"));
            }
        });
        */
    }

    // send an API request to get the timeline json
    // fill the listview by creating the tweet objects from the json
    protected void populateTimeline(final Long oldestId) {
        client.getHomeTimeline(oldestId, new JsonHttpResponseHandler() {
            // On SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("DEBUG", response.toString());
                addAll(Tweet.fromJSONArray(response), oldestId);

                // Call swipeContainer.setRefreshing(false) to signal refresh has ended
                //swipeContainer.setRefreshing(false);
            }

            // On FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //Log.d("DEBUG", errorResponse.toString());
                if (throwable.getMessage().contains("resolve host") || throwable.getMessage().contains("failed to connect")) {
                    Toast.makeText(getActivity(),
                            "Could not connect to internet, please verify your connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
