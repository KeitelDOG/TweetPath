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

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014")
    // from string like output like 3m, 8h, 3d
    public static String getAbbreviatedTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {

            long nowMs = System.currentTimeMillis();
            long thenMs = sf.parse(rawJsonDate).getTime();
            // Calculate difference in milliseconds
            long diff = nowMs - thenMs;

            // Calculate difference in seconds
            long diffSeconds = diff / 1000;
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffSeconds < 60) {
                return diffSeconds + "s";

            } else if (diffMinutes < 60) {
                return diffMinutes + "m";

            } else if (diffHours < 24) {
                return diffHours + "h";

            } else if (diffDays < 7) {
                return diffDays + "d";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //return sf.format(sf.getCalendar().getTime());
        return getRelativeTimeAgo(rawJsonDate);
    }
}
