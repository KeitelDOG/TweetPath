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
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.TwitterApplication;
import com.megalobiz.tweetpath.TwitterClient;
import com.megalobiz.tweetpath.adapters.TweetArrayAdapter;
import com.megalobiz.tweetpath.models.Tweet;
import com.megalobiz.tweetpath.utils.EndlessScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.api.TwitterApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

        // setup view
        setupViews();

        client = TwitterApplication.getRestClient();
        populateTimeline(Long.parseLong("0"));

    }

    //setup the view object value and listener
    public void setupViews() {
        // find list view
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        // create the array list (data source)
        tweets = new ArrayList<>();
        // construct the adapter from data source
        aTweets = new TweetArrayAdapter(this, tweets);
        // connect adapter to list view
        lvTweets.setAdapter(aTweets);

        // add endless scroll listener
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // find the oldest tweet id
                long oldestId = getOldestTweetId();
                //Toast.makeText(TimelineActivity.this,
                //        "Load More -> Page: " + page + " - Oldest Id: " + oldestId,
                //        Toast.LENGTH_LONG).show();

                // pocpulate timeline with tweets before the tweet with the oldest Id
                populateTimeline(oldestId);
                return true;
            }
        });
    }

    // send an API request to get the timeline json
    // fill the listview by creating the tweet objects from the json
    public void populateTimeline(Long oldestId) {
        client.getHomeTimeline(oldestId, new JsonHttpResponseHandler() {
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //Log.d("DEBUG", errorResponse.toString());
                if (throwable.getMessage().contains("resolve host")) {
                    Toast.makeText(TimelineActivity.this,
                            "Could not connect to internet, please verify your connection", Toast.LENGTH_LONG).show();
                }
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

    // get oldest tweet id in the ArrayList tweets to search for other tweets before
    // this last one
    public Long getOldestTweetId() {
        // if we want to sort the tweets first
        /*Collections.sort(tweets, new Comparator<Tweet>(){
            public int compare(Tweet t1, Tweet t2) {
                if(t1.getUid() > t2.getUid())
                    return 1;
                else if(t1.getUid() < t2.getUid())
                    return -1;
                else
                    return 0;
            }
        });*/

        // tweets are already sort by Id in the ArrayList, so the last tweets in the
        return tweets.get(tweets.size()-1).getUid();
    }
}
