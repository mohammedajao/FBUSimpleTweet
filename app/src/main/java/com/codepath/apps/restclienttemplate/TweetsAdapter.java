package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    public static final String TAG = "TweetsAdapter";
    Context mContext;
    List<Tweet> mTweets;


    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.mContext = context;
        this.mTweets = tweets;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        final int mRADIUS = 10;

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTime;
        TextView tvUsername;
        GridLayout gridLayout;
        ImageView ivMedia1;
        ImageView ivMedia2;
        ImageView ivMedia3;
        ImageView ivMedia4;

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
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvUsername.setText("@" + tweet.user.screenName);
            tvScreenName.setText(tweet.user.name);
            tvRelativeTime.setText(tweet.getRelativeTimeAgo());
            Log.i(TAG, tweet.user.screenName + ": " + tweet.media.toString());
            if(!tweet.media.isEmpty()) {
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
