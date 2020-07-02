package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    public static final String TAG = "TweetsAdapter";

    public static final int DETAIL_VIEW_FLAG_REPLY = 0;
    public static final int DETAIL_VIEW_FLAG_RETWEET = 1;
    public static final int DETAIL_VIEW_FLAG_LIKE = 2;

    Context mContext;
    List<Tweet> mTweets;
    TimelineActivity.OnClickListener mOnClickListener;
    TwitterClient client;


    public TweetsAdapter(Context context, List<Tweet> tweets, TimelineActivity.OnClickListener onClickListener) {
        this.mContext = context;
        this.mTweets = tweets;
        this.mOnClickListener = onClickListener;
        client = TwitterApplication.getRestClient(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    private void retweet(final Tweet tweet) {
        client.retweet(tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                    mOnClickListener.onClick(tweet, DETAIL_VIEW_FLAG_RETWEET);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failed to retweet: " + tweet.id + response, throwable);
            }
        });
    }

    public void like(final Tweet tweet) {
        client.like(tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                mOnClickListener.onClick(tweet, DETAIL_VIEW_FLAG_LIKE);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failed to like: " + tweet.id + response, throwable);
            }
        });
    }

    public void unlike(final Tweet tweet) {
        client.unlike(tweet.id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failed to like: " + tweet.id + response, throwable);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final int mRADIUS = 10;

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTime;
        TextView tvUsername;
        Tweet mTweet;
        TextView tvRTCount;
        TextView tvFaveCount;
        GridLayout gridLayout;
        ImageView ivMedia1;
        ImageView ivMedia2;
        ImageView ivMedia3;
        ImageView ivMedia4;

        ImageButton ibRetweetBtn;
        ImageButton ibLikeBtn;

        ImageButton replyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvRelativeTime = itemView.findViewById(R.id.tvRelativeTime);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            gridLayout = itemView.findViewById(R.id.glMedia);
            ivMedia1 = itemView.findViewById(R.id.ivMedia1);
            ivMedia2 = itemView.findViewById(R.id.ivMedia2);
            ivMedia3 = itemView.findViewById(R.id.ivMedia3);
            ivMedia4 = itemView.findViewById(R.id.ivMedia4);
            tvRTCount = itemView.findViewById(R.id.tvRetweetCount);
            tvFaveCount = itemView.findViewById(R.id.tvFaveCount);
            replyButton = itemView.findViewById(R.id.ibReply);
            ibRetweetBtn = itemView.findViewById(R.id.ibRetweet);
            ibLikeBtn = itemView.findViewById(R.id.ibFavourites);

            ibRetweetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mTweet.retweetedStatus) {
                        ibRetweetBtn.setColorFilter(Color.argb(255, 100, 255, 100));
                        tvRTCount.setText("" + (++mTweet.retweetCount));
                        retweet(mTweet);
                    }
                }
            });

            ibLikeBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ibLikeBtn.setColorFilter(Color.argb(255, 255, 100, 100));
                    if(!mTweet.liked) {
                        ibLikeBtn.setColorFilter(Color.argb(255, 255, 100, 100));
                        tvFaveCount.setText("" + (++mTweet.likeCount));
                        like(mTweet);
                    } else {
                        tvFaveCount.setText("" + (--mTweet.likeCount));
                        ibLikeBtn.clearColorFilter();
                        unlike(mTweet);
                    }
                }
            });

            replyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onClick(mTweet, DETAIL_VIEW_FLAG_REPLY);
                }
            });
        }

        public void bind(Tweet tweet) {
            mTweet = tweet;
            tvFaveCount.setText("" + tweet.likeCount);
            tvRTCount.setText("" + tweet.retweetCount);
            tvBody.setText(tweet.body);
            tvUsername.setText("@" + tweet.user.screenName);
            tvScreenName.setText(tweet.user.name);
            tvRelativeTime.setText(tweet.getRelativeTimeAgo());
            if(mTweet.retweetedStatus)
                ibRetweetBtn.setColorFilter(Color.argb(255, 100, 255, 100));
            if(mTweet.liked)
                ibLikeBtn.setColorFilter(Color.argb(255, 255, 100, 100));
            if(tweet.media != null && !tweet.media.isEmpty()) {
                gridLayout.setVisibility(View.VISIBLE);
                ImageView image;
                for (int i = 0; i < 4; i++) {
                    switch (i) {
                        case 0:
                            image = ivMedia1;
                            break;
                        case 1:
                            image = ivMedia2;
                            break;
                        case 2:
                            image = ivMedia3;
                            break;
                        case 3:
                            image = ivMedia4;
                            break;
                        default:
                            image = ivMedia1;
                    }
                    image.setVisibility(View.GONE);
                    if (i < tweet.media.size()) {
                        image.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(tweet.media.get(i)).into(image);
                    }
                }
            } else {
                gridLayout.setVisibility(View.GONE);
            }
            Glide.with(mContext)
                    .load(tweet.user.profileImageUrl)
                    .transform(new RoundedCorners(mRADIUS))
                    .into(ivProfileImage);
        }
    }
}
