package com.example.parstagram;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parstagram.fragments.ProfileFragment;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
    public static final String KEY_PROFILE_IMAGE = "profileImage";
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAllPosts(List<Post> postsList) {
        posts.addAll(postsList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private ImageView ivProfileImage;
        private RelativeLayout relativeLayout;
        private TextView tvTime;
        private ImageView ivLike;
        private ImageView ivComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivLike = itemView.findViewById(R.id.ivLike);

            itemView.setOnClickListener(this);
        }

        public void bind(final Post post) {
            // bind the post data to the view elements
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvTime.setText(post.getTime());

            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }

            ParseFile profileImage = post.getUser().getParseFile(KEY_PROFILE_IMAGE);
            if (profileImage != null) {
                Glide.with(context).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
            }

            ivComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                    context.startActivity(intent);
                }
            });

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                    Fragment fragment = new ProfileFragment(post.getUser());
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
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

            queryLikes(post.getObjectId());
        }

        private void queryLikes(String postId) {
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

        private void queryNewLike(final String postId) {
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

            @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                context.startActivity(intent);
            }
        }
    }
}
