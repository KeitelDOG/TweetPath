package com.megalobiz.tweetpath.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.TwitterApplication;
import com.megalobiz.tweetpath.TwitterClient;
import com.megalobiz.tweetpath.fragments.UserTimelineFragment;
import com.megalobiz.tweetpath.models.User;
import com.megalobiz.tweetpath.utils.CountFormatter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {

    private TwitterClient client;
    private User user;
    String profileBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayUseLogoEnabled(true);

        client = TwitterApplication.getRestClient();
        user = (User) getIntent().getSerializableExtra("user");
        String screenName = getIntent().getStringExtra("screen_name");

        // get profile banner
        getProfileBanner(user);

        UserTimelineFragment fragment;
        if (user != null) {
            ab.setTitle(String.format("@%s", user.getScreenName()));
            fragment = UserTimelineFragment.newInstance(user);
        } else {
            ab.setTitle(String.format("@%s", screenName));
            fragment = UserTimelineFragment.newInstance(screenName);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, fragment);
        ft.commit();

        // set user profile info
        populateProfileHeader(user);
    }

    private void getProfileBanner(User user) {
        client.getProfileBanner(user.getScreenName(), new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    profileBanner = response.getJSONObject("sizes").getJSONObject("mobile").getString("url");

                    ImageView ivProfileBanner = (ImageView) findViewById(R.id.ivProfileBanner);
                    ivProfileBanner.setImageResource(0);

                    // set profile banner
                    String profileBannerUrl = profileBanner;
                    if(!TextUtils.isEmpty(profileBannerUrl)) {
                        Picasso.with(ProfileActivity.this).load(profileBannerUrl).into(ivProfileBanner);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getMessage().contains("resolve host") || throwable.getMessage().contains("failed to connect")) {
                    Toast.makeText(ProfileActivity.this,
                            "Could not connect to internet, please verify your connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void populateProfileHeader(User user) {
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(0);

        TextView ivUserName = (TextView) findViewById(R.id.tvUserName);
        TextView ivTagLine = (TextView) findViewById(R.id.tvTagLine);

        // retweet and favorite counter
        TextView tvFollowerCount = (TextView) findViewById(R.id.tvFollowerCount);
        TextView tvFollowingCount = (TextView) findViewById(R.id.tvFollowingCount);
        TextView tvStatusCount = (TextView) findViewById(R.id.tvStatusCount);

        ivUserName.setText(user.getName());
        ivTagLine.setText(user.getTagLine());

        // social counters
        tvFollowerCount.setText(String.format("%s FOLLOWERS", CountFormatter.format(user.getFollowerCount())));
        tvFollowingCount.setText(String.format("%s FOLLOWING", CountFormatter.format(user.getFriendCount())));
        tvStatusCount.setText(String.format("%s TWEETS", CountFormatter.format(user.getStatusCount())));

        // set the images with Picasso
        // set profile image
        String profileImageUrl = user.getProfileImageUrl();
        if(!TextUtils.isEmpty(profileImageUrl)) {
            Picasso.with(this).load(profileImageUrl)
                    .transform(new RoundedCornersTransformation(3, 3))
                    .into(ivProfileImage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // action compose new tweet
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
