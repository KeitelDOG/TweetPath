package com.megalobiz.tweetpath.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.TwitterApplication;
import com.megalobiz.tweetpath.TwitterClient;
import com.megalobiz.tweetpath.adapters.TweetArrayAdapter;
import com.megalobiz.tweetpath.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.api.TwitterApi;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // find list view
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        // create the array list (data source)
        tweets = new ArrayList<>();
        // construct the adapter from data source
        aTweets = new TweetArrayAdapter(this, tweets);
        // connect adapter to list view
        lvTweets.setAdapter(aTweets);

        client = TwitterApplication.getRestClient();
        populateTimeline();
    }

    // send an API request to get the timeline json
    // fill the listview by creating the tweet objects from the json
    public void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            // On SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("DEBUG", response.toString());
                tweets = Tweet.fromJSONArray(response);
                aTweets.addAll(tweets);
                aTweets.notifyDataSetChanged();
                //Log.d("DEBUG", tweets.toString());

            }

            // On FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
