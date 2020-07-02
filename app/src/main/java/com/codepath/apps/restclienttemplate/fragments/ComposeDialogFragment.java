package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.github.scribejava.core.model.Token;

import org.json.JSONException;

import okhttp3.Headers;

public class ComposeDialogFragment extends DialogFragment {
    public static final int MAX_LENGTH_TWEET = 280;
    public static final String TAG = "ComposeDialogFragment";
    public static final int AVATAR_MARGIN = 10;

    Context ctx;

    User user;

    private EditText mEditText;
    private TextView mCounter;
    private TextView mScreenName;
    private TextView mUsername;
    private  TextView mInReplyTo;
    private ImageView mAvatar;
    private Button mTweetBtn;
    private ImageButton mCloseDialog;

    private String mUserToReplyTo;

    LinearLayout llReplyEmbelish;

    private FragmentComposeBinding binding;
    private TwitterClient client;
    private ComposeReader activity;

    public interface ComposeReader {
        public void readComposeFragmentData(Tweet t);
    }

    public ComposeDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeDialogFragment newInstance(String title) {
        ComposeDialogFragment frag = new ComposeDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentComposeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ctx = getContext();
        activity = (ComposeReader) getActivity();
        client = TwitterApplication.getRestClient(ctx);
        user = client.getUser();

        Bundle data = this.getArguments();
        if(data != null) {
            mUserToReplyTo = data.getString("name");
        }

        return view;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mTweetBtn = binding.btnTweet;
        mEditText = binding.etCompose;
        mCounter = binding.tvCounter;
        mScreenName = binding.tvScreenName;
        mUsername = binding.tvName;
        mAvatar = binding.ivAvatar;
        mCloseDialog = binding.ibCloseDialog;
        mInReplyTo = binding.tvInReplyTo;
        llReplyEmbelish = binding.llReplyEmbellish;

        if(user != null)
            mScreenName.setText(user.name);
        mUsername.setText(user.screenName);
        mCounter.setText(Integer.toString(MAX_LENGTH_TWEET));
        if(mUserToReplyTo != null) {
            llReplyEmbelish.setVisibility(View.VISIBLE);
            mEditText.setText("@" + mUserToReplyTo);
            mInReplyTo.setText("In reply to @" + mUserToReplyTo);
        } else {
            llReplyEmbelish.setVisibility(View.GONE);
        }
        Glide.with(ctx)
                .load(user.profileImageUrl)
                .transform(new RoundedCorners(AVATAR_MARGIN))
                .into(mAvatar);

        mCloseDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = 280 - s.toString().length();
                mCounter.setText(Integer.toString(count));
                if(count < 0) {
                    mCounter.setTextColor(Color.rgb(255,0,0));
                } else {
                    mCounter.setTextColor(Color.rgb(100,100,100));
                }
            }
        });

        mTweetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String tweetContent = mEditText.getText().toString();
                if(tweetContent.isEmpty()) {
                    Toast.makeText(ctx,"Tweet is empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_LENGTH_TWEET) {
                    Toast.makeText(ctx,"Tweet is too long!", Toast.LENGTH_LONG).show();
                    return;
                }
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess PublishTweet:" + json.toString());
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            activity.readComposeFragmentData(tweet);
                            dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure: " + response, throwable);
                    }
                });
            }
        });
        // Show soft keyboard automatically and request focus to field
//        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
