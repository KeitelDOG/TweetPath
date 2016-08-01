package com.megalobiz.tweetpath.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.TwitterApplication;
import com.megalobiz.tweetpath.TwitterClient;
import com.megalobiz.tweetpath.models.Tweet;
import com.megalobiz.tweetpath.models.User;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ComposeActivity extends AppCompatActivity {

    User user;
    TwitterClient client;
    EditText etBody;
    TextView tvRemainingChars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        user = (User) getIntent().getSerializableExtra("user");
        client = TwitterApplication.getRestClient();

        // setup views
        setupViews();
    }

    //setup the view object value and listener
    public void setupViews() {
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(0);

        TextView ivUserName = (TextView) findViewById(R.id.tvUserName);
        TextView ivScreenName = (TextView) findViewById(R.id.tvScreenName);
        etBody = (EditText) findViewById(R.id.etBody);
        tvRemainingChars = (TextView) findViewById(R.id.tvRemainingChars);

        ivUserName.setText(user.getName());
        //add @ to as prefix to screen name
        ivScreenName.setText(String.format("@%s", user.getScreenName()));

        String profileImageUrl = user.getProfileImageUrl();

        if(!TextUtils.isEmpty(profileImageUrl)) {
            Picasso.with(this).load(profileImageUrl)
                    .transform(new RoundedCornersTransformation(3, 3))
                    .into(ivProfileImage);
        }

        // add text changed listener to Body text view
        listenBodyText();
    }

    public void listenBodyText() {
        // count remaining characters on text changed
        etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int remainCount = 140 - editable.length();
                tvRemainingChars.setText(String.valueOf(remainCount));
            }
        });
    }

    public void onTweet(View view) {
        // get status in body edit text
        String status = etBody.getText().toString();

        client.postUpdateStatus(status, new JsonHttpResponseHandler() {
            // On SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // get the tweet that is just created
                Tweet tweet = Tweet.fromJSON(response);
                // return the tweet to the TimelineActivity
                Intent i = new Intent();
                i.putExtra("tweet", tweet);
                setResult(RESULT_OK, i);
                finish();
            }

            // On FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
                if (throwable.getMessage().contains("resolve host")) {
                    Toast.makeText(ComposeActivity.this,
                            "Could not connect to internet, please verify your connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onClose(View view) {
        finish();
    }
}
