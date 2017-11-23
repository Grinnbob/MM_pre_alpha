package com.mycompany.grifon.mm_pre_alpha.ui.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.Post;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.engine.music.Player;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapterPosts extends RecyclerView.Adapter<RecyclerViewAdapterPosts.ViewHolder> {

    private List<Post> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Player player;
    private String uuid;
    // for reposts
    // true = my profile, false = not mine
    private boolean profyleType;
    // true = ProfileActivity, false = NewsActivity
    private boolean activityType;

    // data is passed into the constructor
    public RecyclerViewAdapterPosts(Context context, List<Post> data, String uuid, boolean profileType, boolean activityType) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

        player = new Player();
        this.uuid = uuid;
        this.profyleType = profileType;
        this.activityType = activityType;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("MY LOG:", "POSTS SET: " + mData);
        if (!mData.isEmpty()) {
            Log.d("MY LOG:", "POSTS SET is correct ");
            String songName = mData.get(position).getSong().getName();
            String postText = mData.get(position).getText();
            String authorName = null;
            if (mData.get(position).getAuthor() != null) {
                authorName = mData.get(position).getAuthor().getName();
                holder.tv_authorName.setText(authorName);
            }
            Log.d("MY LOG:", "Author name: " + authorName);
            int likes = mData.get(position).getSong().getLikes();
            holder.tv_songName.setText(songName);
            holder.tv_post_text.setText(postText);
            holder.tv_likes.setText(Integer.toString(likes));

        } else {
            Log.d("MY LOG:", "POSTS SET is empty or null");
            holder.tv_songName.setText("no songs");
            holder.tv_post_text.setText("no posts");
            holder.tv_likes.setText("0");
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_songName;
        public TextView tv_likes;
        public TextView tv_post_text;
        public TextView tv_authorName;
        public Button btnv_play;
        public Button btnv_pause;
        public Button btnv_repost;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_songName = (TextView) itemView.findViewById(R.id.tv_recycler_item);
            tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
            tv_post_text = (TextView) itemView.findViewById(R.id.tv_text_post_item);
            tv_authorName = (TextView) itemView.findViewById(R.id.tv_author_name);
            btnv_play = (Button) itemView.findViewById(R.id.btn_play);
            btnv_pause = (Button) itemView.findViewById(R.id.btn_pause);
            btnv_repost = (Button) itemView.findViewById(R.id.btn_repost);
            //checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_like);
            //checkBox.setOnCheckedChangeListener(checkBoxListener);

            // if my profile - no reposts
            if(profyleType) {
                btnv_repost.setVisibility(View.INVISIBLE);
            }

            btnv_play.setOnClickListener(this);
            btnv_pause.setOnClickListener(this);
            btnv_repost.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
            // хз что это, возможно не нужно
            //if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

            if (view.getId() == R.id.btn_play) {
                player.startPlayback(mData.get(getAdapterPosition()).getSong().getUrl());
            } else if (view.getId() == R.id.btn_pause) {
                player.stopPlayback();
            } else if (view.getId() == R.id.btn_repost) {
                Post post = mData.get(getAdapterPosition());
                // if NewsActivity
                if(!activityType) {
                    // if author not I
                    if (post.getAuthor() != null) {
                        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentTime = dateFormat.format(currentTimestamp);
                        Post repostedPost = new Post(post.getText(), post.getSong(), currentTime);
                        FirebasePathHelper.writeNewPostDB(uuid, repostedPost);
                    } else {
                        // работает не так, как хотелось бы
                        btnv_repost.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // else - ProfileActivity
                    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = dateFormat.format(currentTimestamp);
                    Post repostedPost = new Post(post.getText(), post.getSong(), currentTime);
                    FirebasePathHelper.writeNewPostDB(uuid, repostedPost);
                }
            }
        }

/*
        // like it!
        CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                // ставим лайк
                    Post post = mData.get(getAdapterPosition());
                    //int likes = Integer.parseInt(post.getSong().getLikes());
                    int likes = post.getSong().getLikes();
                    likes++;
                    FirebasePathHelper.addLikeDB(uuid, post.getTimestamp(), likes);

                } else {
                // убираем лайк
                    Post post = mData.get(getAdapterPosition());
                    //int likes = Integer.parseInt(post.getSong().getLikes());
                    int likes = post.getSong().getLikes();
                    likes--;
                    FirebasePathHelper.addLikeDB(uuid, post.getTimestamp(), likes);
                }
            }
        };
        */
    }

    // convenience method for getting data at click position
    public Post getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        //void onItemClick(int position);
    }

}
