package com.codepath.apps.restclienttemplate.models;

import org.parceler.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

@Parcel
public class User {
    public String name;
    public String screenName;
    public String profileImageUrl;

    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        return user;
    }

}
