package com.megalobiz.tweetpath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.TwitterApplication;
import com.megalobiz.tweetpath.TwitterClient;
import com.megalobiz.tweetpath.adapters.TweetArrayAdapter;
import com.megalobiz.tweetpath.fragments.HomeTimelineFragment;
import com.megalobiz.tweetpath.fragments.MentionsTimelineFragment;
import com.megalobiz.tweetpath.fragments.TweetsListFragment;
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

    //auth user
    protected User user;
    protected TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setLogo(R.drawable.ic_twitter_logo);
        ab.setTitle("");

        client = TwitterApplication.getRestClient();

        // get the view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // set the view pager adapter for the pager

        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        // find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //attach the tabstrip to the viewpager
        tabStrip.setViewPager(vpPager);

        fetchUserCredentials();
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
                Tweet tweet = (Tweet) data.getSerializableExtra("tweet");

                // add it on top of tweets array list, before the tweet at index 0
                HomeTimelineFragment fm = (HomeTimelineFragment) getSupportFragmentManager().getFragments().get(0);
                fm.addTweetInTimeline(tweet);
            }
        }
    }

    // return the order of the fragments in the view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        // Adapter gets the manager insert or remove pager from activity
        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // the order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
