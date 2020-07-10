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
    private ImageButton btnLike; // TODO
    private ImageButton btnComment;

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
        btnComment = findViewById(R.id.btnComment);

        tvViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToComments();
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToComments();
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
    }

    private void goToComments() {
        Intent intent = new Intent(PostDetailActivity.this, CommentsActivity.class);
        intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
        startActivity(intent);
    }
}