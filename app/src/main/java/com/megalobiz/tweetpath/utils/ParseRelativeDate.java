package com.megalobiz.tweetpath.utils;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ParseRelativeDate {

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }

    // getAbbreviatedTimeAgo from string like "3 minutes ago", "8 hours ago"
    // take 3+m, take 8+h
    public static String getAbbreviatedTimeAgo(String timeAgo) {
        if(timeAgo.length() > 2) {
            String[] strings = timeAgo.split(" ");
            if(Integer.parseInt(strings[0]) > -1) {
                // ex: 3 minutes ago
                // (3 => strings[0], minutes => strings[1], m => strings[1].substring(0,1)
                return String.format("%s%s", strings[0], strings[1].substring(0,1));
            }
        }

        return timeAgo;
    }
}
