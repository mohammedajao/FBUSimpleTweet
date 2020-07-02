package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public static final String TAG = "Tweet";
    public long id;
    public String body;
    public String createdAt;
    public User user;
    public ArrayList<String> media;

    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.id = jsonObject.getLong("id");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.media = new ArrayList<String>();
        if(jsonObject.has("extended_entities")) {
            tweet.mediaFromJsonArray(jsonObject.getJSONObject("extended_entities").getJSONArray("media"));
        }

        /*if(jsonObject.has("extended_entities")){
            String type = jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("type");
            if(type.equals("photo")){
                tweet.media.add(jsonObject.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0).getString("media_url_https"));
            }
        }*/

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i=0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    private void mediaFromJsonArray(JSONArray jsonArray) throws JSONException {
        for(int i=0; i < jsonArray.length(); i++) {
            JSONObject mediaData = jsonArray.getJSONObject(i);
            String type = mediaData.getString("type");
            if(type.equals("photo")) {
                media.add(mediaData.getString("media_url_https"));
            }
        }
    }

    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
