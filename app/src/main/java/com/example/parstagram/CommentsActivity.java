package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";
    public static final int NUM_COMMENTS = 10;
    private TextView tvUsername;
    private ImageView ivProfileImage;
    private TextView tvDescription;
    private Post post;
    protected RecyclerView rvComments;
    private CommentsAdapter adapter;
    private List<Comment> allComments;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        postId = post.getObjectId();

//        tvUsername = findViewById(R.id.tvUsername);
//        ivProfileImage = findViewById(R.id.ivProfileImage);
//        tvDescription = findViewById(R.id.tvDescription);
        rvComments = findViewById(R.id.rvComments);
//
//        tvUsername.setText(post.getUser().getUsername());
//        tvDescription.setText(post.getDescription());
//        ParseFile profileImage = post.getUser().getParseFile("profileImage");
//        if (profileImage != null) {
//            Glide.with(this).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
//        }

        allComments = new ArrayList<>();
        adapter = new CommentsAdapter(this, allComments);
        rvComments.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(layoutManager);

        queryComments();
    }

    private void queryComments() {
        // Specify which class to query
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereEqualTo(Comment.KEY_POST_ID, postId);
        query.setLimit(NUM_COMMENTS);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }
                adapter.clear();
                adapter.addAllComments(comments);
            }
        });
    }
}