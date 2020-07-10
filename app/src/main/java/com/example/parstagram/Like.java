package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {

    public static final String KEY_POST_ID = "postId";
    public static final String KEY_USER = "user";

    public String getPostId() { return getString(KEY_POST_ID); }

    public void setPostId(String id) { put(KEY_POST_ID, id); }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
