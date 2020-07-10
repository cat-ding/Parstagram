package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    public static final int NUM_COMMENTS = 10;
    private TextView tvUsername;
    private ImageView ivProfileImage;
    private TextView tvDescription;
    private Post post;
    protected RecyclerView rvComments;
    private CommentsAdapter adapter;
    private List<Comment> allComments;
    private String postId;
    private Button btnPost;
    private EditText etComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        postId = post.getObjectId();

        tvUsername = findViewById(R.id.tvUsername);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvDescription = findViewById(R.id.tvDescription);
        rvComments = findViewById(R.id.rvComments);
        btnPost = findViewById(R.id.btnPost);
        etComment = findViewById(R.id.etComment);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentContent = etComment.getText().toString();
                if (commentContent.isEmpty()) {
                    Toast.makeText(CommentsActivity.this, "Sorry, your comment cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                queryUpdateComments(commentContent);
            }
        });

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        ParseFile profileImage = post.getUser().getParseFile(KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
        }

        allComments = new ArrayList<>();
        adapter = new CommentsAdapter(this, allComments);
        rvComments.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(layoutManager);

        queryComments();
    }

    private void queryUpdateComments(String commentContent) {
        Comment comment = new Comment();
        comment.put(Comment.KEY_BODY, commentContent);
        comment.put(Comment.KEY_USER, ParseUser.getCurrentUser());
        comment.put(Comment.KEY_POST_ID, postId);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving comment", e);
                    Toast.makeText(CommentsActivity.this, "Error while posting comment!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Comment save was successful!");
                etComment.getText().clear();
                queryComments(); // refresh
            }
        });
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