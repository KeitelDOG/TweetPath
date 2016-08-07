package com.megalobiz.tweetpath.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.TwitterApplication;
import com.megalobiz.tweetpath.TwitterClient;
import com.megalobiz.tweetpath.activities.TimelineActivity;
import com.megalobiz.tweetpath.adapters.TweetArrayAdapter;
import com.megalobiz.tweetpath.models.Tweet;
import com.megalobiz.tweetpath.models.User;
import com.megalobiz.tweetpath.utils.EndlessScrollListener;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by KeitelRobespierre on 8/4/2016.
 */
public class TweetsListFragment extends Fragment{

    protected TwitterClient client;

    private ArrayList<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;
    protected User user;

    // inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        // find list view
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
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
                //populateTimeline(oldestId);
                return true;
            }
        });

        return v;
    }


    // creation lifecycle event
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            client = TwitterApplication.getRestClient();
            // create the array list (data source)
            tweets = new ArrayList<>();
            // construct the adapter from data source
            aTweets = new TweetArrayAdapter(getActivity(), tweets);
        }
    }

    public void addAll(ArrayList<Tweet> tweets, long oldestId) {
        // clear list if populating for the first time, or on refresh
        if(oldestId == 0) {
            //aTweets.clear();
        }
        aTweets.addAll(tweets);
        aTweets.notifyDataSetChanged();
        //Log.d("DEBUG", tweets.toString());

        // scroll to first element in list view
        if (oldestId == 0)
            lvTweets.setSelectionAfterHeaderView();
    }

    public void clear() {
        aTweets.clear();
    }

    public void addTweetInTimeline(Tweet tweet) {
        // add it on top of tweets array list, before the tweet at index 0
        tweets.add(0, tweet);
        aTweets.notifyDataSetChanged();
        lvTweets.setSelectionAfterHeaderView();
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

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_timeline, menu);
    }*/
}
