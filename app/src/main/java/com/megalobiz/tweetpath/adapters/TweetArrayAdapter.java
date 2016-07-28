package com.megalobiz.tweetpath.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.megalobiz.tweetpath.R;
import com.megalobiz.tweetpath.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        Tweet tweet = getItem(position);

        //find or inflate the template
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
        }

        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(0);

        TextView ivUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView ivBody = (TextView) convertView.findViewById(R.id.tvBody);

        ivUserName.setText(tweet.getUser().getName());
        ivBody.setText(tweet.getBody());

        String profileImageUrl = tweet.getUser().getProfileImageUrl();

        if(!TextUtils.isEmpty(profileImageUrl)) {
            Picasso.with(getContext()).load(profileImageUrl).into(ivProfileImage);
        }

        return convertView;
    }
}
