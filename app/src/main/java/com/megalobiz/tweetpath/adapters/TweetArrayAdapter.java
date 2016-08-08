package com.megalobiz.tweetpath.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.activities.ProfileActivity;
import com.megalobiz.tweetpath.models.Tweet;
import com.megalobiz.tweetpath.utils.ParseRelativeDate;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by KeitelRobespierre on 7/27/2016.
 * taking the Tweet objects and turning them into Views displayed in the list
 */
public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    // override and setup custom template

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the Tweet
        final Tweet tweet = getItem(position);

        //find or inflate the template
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
        }

        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(0);

        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
        TextView tvTimeAgo = (TextView) convertView.findViewById(R.id.tvTimeAgo);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);

        // find media view
        ImageView ivMediaPhoto = (ImageView) convertView.findViewById(R.id.ivMediaPhoto);
        ivMediaPhoto.setImageResource(0);

        // retweet and favorite counter
        TextView tvRetweetCount = (TextView) convertView.findViewById(R.id.tvRetweetCount);
        TextView tvFavoriteCount = (TextView) convertView.findViewById(R.id.tvFavoriteCount);

        tvUserName.setText(tweet.getUser().getName());
        //add @ to as prefix to screen name
        tvScreenName.setText(String.format("@%s", tweet.getUser().getScreenName()));

        // set abbreviated time ago, ex: 6s, 3m, 8h
        String timeAgo = ParseRelativeDate.getAbbreviatedTimeAgo(tweet.getCreatedAt());
        tvTimeAgo.setText(timeAgo);

        tvBody.setText(tweet.getBody());

        // social counters
        tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));

        // set the images with Picasso
        // set profile image
        String profileImageUrl = tweet.getUser().getProfileImageUrl();
        if(!TextUtils.isEmpty(profileImageUrl)) {
            Picasso.with(getContext()).load(profileImageUrl)
                    .transform(new RoundedCornersTransformation(3, 3))
                    .into(ivProfileImage);
        }

        // set media photo. if there is photos, we take only the 1st photo
        if (tweet.getPhotoUrls().size() > 0) {
            String mediaPhoto = tweet.getPhotoUrls().get(0);
            if(!TextUtils.isEmpty(mediaPhoto)) {
                Picasso.with(getContext()).load(mediaPhoto)
                        .transform(new RoundedCornersTransformation(20, 20))
                        .into(ivMediaPhoto);
            }
        }

        // Set Events
        // profile image onclick
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TweetArrayAdapter.this.getContext(), ProfileActivity.class);
                i.putExtra("user", tweet.getUser());
                TweetArrayAdapter.this.getContext().startActivity(i);
            }
        });

        return convertView;
    }
}
