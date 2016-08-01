package com.megalobiz.tweetpath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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
import com.megalobiz.tweetpath.models.User;
import com.megalobiz.tweetpath.utils.EndlessScrollListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.api.TwitterApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeContainer;

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;

    //auth user
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setLogo(R.drawable.ic_twitter_logo);
        ab.setTitle("");

        // setup view
        setupViews();

        client = TwitterApplication.getRestClient();
        fetchUserCredentials();
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

        // Lookup the Swipe Container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        //Listen for Swipe Refresh to fetch Moives again
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Make sure to call swipeContainer.setRefreshing(fasle) once the
                // network has completed successfully
                //clear list before refreshing
                aTweets.clear();
                populateTimeline(Long.parseLong("0"));
            }
        });
    }

    // fetch information of the authenticated user
    public void fetchUserCredentials() {
        client.getVerifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    // send an API request to get the timeline json
    // fill the listview by creating the tweet objects from the json
    public void populateTimeline(final Long oldestId) {
        client.getHomeTimeline(oldestId, new JsonHttpResponseHandler() {
            // On SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("DEBUG", response.toString());
                tweets = Tweet.fromJSONArray(response);
                // clear list if populating for the first time, or on refresh
                if(oldestId == 0) {
                    aTweets.clear();
                }
                aTweets.addAll(tweets);
                aTweets.notifyDataSetChanged();
                //Log.d("DEBUG", tweets.toString());

                // scroll to first element in list view
                if (oldestId == 0)
                    lvTweets.setSelectionAfterHeaderView();

                // Call swipeContainer.setRefreshing(false) to signal refresh has ended
                swipeContainer.setRefreshing(false);
            }

            // On FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
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

        // action compose new tweet
        if (id == R.id.action_compose) {
            composeTweet();
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

    public void composeTweet() {
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("user", user);
        startActivityForResult(i, 10);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                // retrieve the composed tweet
                //Tweet tweet = (Tweet) data.getSerializableExtra("tweet");

                // add it on top of tweets array list, before the tweet at index 0
                //tweets.add(0, tweet);
                //aTweets.notifyDataSetChanged();

                // repopulate the timeline
                populateTimeline(Long.parseLong("0"));
            }
        }
    }
}
