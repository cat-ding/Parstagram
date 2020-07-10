package com.example.parstagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailActivity";
    public static final int NUM_COMMENTS = 10;
    private static final String KEY_PROFILE_IMAGE = "profileImage";
    private TextView tvUsername;
    private ImageView ivProfileImage;
    private ImageView ivImage;
    private TextView tvDescription;
    private TextView tvTime;
    private Post post;
    private TextView tvViewComments;
    private ImageView ivLike;
    private ImageView ivComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvUsername = findViewById(R.id.tvUsername);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivImage = findViewById(R.id.ivImage);
        tvDescription = findViewById(R.id.tvDescription);
        tvTime = findViewById(R.id.tvTime);
        tvViewComments = findViewById(R.id.tvViewComments);
        ivComment = findViewById(R.id.ivComment);
        ivLike = findViewById(R.id.ivLike);

        tvViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToComments();
            }
        });

        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToComments();
            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int) ivLike.getTag() == R.drawable.ufi_heart) {
                    ivLike.setImageResource(R.drawable.ufi_heart_active);
                    ivLike.setTag(R.drawable.ufi_heart_active);
                    queryNewLike(post.getObjectId());
                } else {
                    ivLike.setImageResource(R.drawable.ufi_heart);
                    ivLike.setTag(R.drawable.ufi_heart);
                    queryDeleteLike(post.getObjectId());
                }
            }
        });

        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvTime.setText(post.getTime());

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivImage);
        }

        ParseFile profileImage = post.getUser().getParseFile(KEY_PROFILE_IMAGE);
        if (profileImage != null) {
            Glide.with(this).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
        }

        queryLikes(post.getObjectId());
    }

    private void queryNewLike(String postId) {
        Like like = new Like();
        like.put(Comment.KEY_USER, ParseUser.getCurrentUser());
        like.put(Comment.KEY_POST_ID, postId);
        like.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving comment", e);
                    return;
                }
                Log.i(TAG, "Like save was successful!");
            }
        });
    }

    private void queryDeleteLike(String postId) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Like.KEY_POST_ID, postId);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> like, ParseException e) {
                like.get(0).deleteInBackground();
            }
        });
    }


    private void goToComments() {
        Intent intent = new Intent(PostDetailActivity.this, CommentsActivity.class);
        intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
        startActivity(intent);
    }

    private void queryLikes(String postId) {
        // Specify which class to query
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.include(Like.KEY_USER);
        query.whereEqualTo(Like.KEY_POST_ID, postId);
        query.whereEqualTo(Like.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error retrieving likes!", e);
                    return;
                }
                Log.i(TAG, "Successfully retrieved likes!");
                if (likes.isEmpty()) {
                    ivLike.setImageResource(R.drawable.ufi_heart);
                    ivLike.setTag(R.drawable.ufi_heart);
                } else {
                    ivLike.setImageResource(R.drawable.ufi_heart_active);
                    ivLike.setTag(R.drawable.ufi_heart_active);
                }
            }
        });
    }
}