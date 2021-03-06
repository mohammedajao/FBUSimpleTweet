package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;

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
@Entity(foreignKeys = @ForeignKey(entity=User.class, parentColumns="id", childColumns="userId"))

public class Tweet {
    public static final String TAG = "Tweet";

    @ColumnInfo
    @PrimaryKey
    public long id;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public String createdAt;

    @ColumnInfo
    public long userId;

    @ColumnInfo
    public long likeCount;

    @ColumnInfo
    public long retweetCount;

    @Ignore
    public User user;

    @Ignore
    public ArrayList<String> media;

    @Ignore
    public boolean liked;

    @ColumnInfo
    public boolean retweetedStatus;

    @ColumnInfo
    public boolean retweeted;

    @Ignore
    public User retweetedUser;

    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.id = jsonObject.getLong("id");
        Log.i(TAG, jsonObject.toString());
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.retweetedStatus = jsonObject.has("retweeted_status");
        if(tweet.retweetedStatus) {
            tweet.retweetedUser = User.fromJson(jsonObject.getJSONObject("user"));
            jsonObject = jsonObject.getJSONObject("retweeted_status");
        }
        tweet.liked = jsonObject.getBoolean("favorited");
        tweet.likeCount = jsonObject.getLong("favorite_count");
        tweet.retweetCount = jsonObject.getLong("retweet_count");
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.userId = tweet.user.id;
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
