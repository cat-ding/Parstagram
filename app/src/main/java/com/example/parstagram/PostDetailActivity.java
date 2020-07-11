package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    public static final String TAG = "PostDetailActivity";
    private static final String KEY_PROFILE_IMAGE = "profileImage";
    private TextView tvUsername;
    private ImageView ivProfileImage;
    private ImageView ivImage;
    private TextView tvUsernameDescription;
    private TextView tvDescription;
    private TextView tvTime;
    private Post post;
    private TextView tvViewComments;
    private ImageView ivLike;
    private ImageView ivComment;
    private RelativeLayout relativeLayout;
    private TextView tvNumLikes;
    private Integer numLikes;

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
        tvUsernameDescription = findViewById(R.id.tvUsernameDescription);
        tvNumLikes = findViewById(R.id.tvNumLikes);
        relativeLayout = findViewById(R.id.relativeLayout);

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
                    numLikes++;
                } else {
                    ivLike.setImageResource(R.drawable.ufi_heart);
                    ivLike.setTag(R.drawable.ufi_heart);
                    queryDeleteLike(post.getObjectId());
                    numLikes--;
                }
                setNumLikes(numLikes);
            }
        });

        tvUsername.setText(post.getUser().getUsername());
        tvUsernameDescription.setText(post.getUser().getUsername());
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
        queryNumLikes(post.getObjectId());
    }

    private void queryNumLikes(final String postId) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.include(Like.KEY_USER);
        query.whereEqualTo(Like.KEY_POST_ID, postId);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error retrieving likes!", e);
                    return;
                }
                setNumLikes(likes.size());
                numLikes = likes.size();
            }
        });
    }

    private void setNumLikes(Integer num) {
        if (num == 1) {
            tvNumLikes.setText(num + " like");
        } else {
            tvNumLikes.setText(num + " likes");
        }
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

    @Override
    public void onBackPressed() {
        // transmitting tweet object back to ProfileFragment
        Intent intent = new Intent();
        intent.putExtra("updatedPost", Parcels.wrap(post));
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}