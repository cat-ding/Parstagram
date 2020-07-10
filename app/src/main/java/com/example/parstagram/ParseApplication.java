package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    public static final String APP_ID = "cat-parstagram";
    public static final String CLIENT_KEY = "CodepathCatParstagramMaster";
    public static final String SERVER_URL = "https://cat-parstagram.herokuapp.com/parse";

    @Override
    public void onCreate() {
        super.onCreate();

        // Register your parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID) // should correspond to APP_ID env variable
                .clientKey(CLIENT_KEY)  // set explicitly unless clientKey is explicitly configured on Parse server
                .server(SERVER_URL).build());
    }
}
