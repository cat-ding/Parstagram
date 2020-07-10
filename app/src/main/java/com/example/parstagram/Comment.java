package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_POST_ID = "postId";
    public static final String KEY_BODY = "commentBody";
    public static final String KEY_USER = "user";

    public String getPostId() { return getString(KEY_POST_ID); }

    public void setPostId(String id) { put(KEY_POST_ID, id); }

    public String getCommentBody() { return getString(KEY_BODY); }

    public void setCommentBody(String body) { put(KEY_BODY, body); }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
