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
import com.mycompany.grifon.mm_pre_alpha.data.PlainUser;
import com.mycompany.grifon.mm_pre_alpha.data.Post;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseAuthHelper;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebasePathHelper;
import com.mycompany.grifon.mm_pre_alpha.engine.firebase.FirebaseUtils;
import com.mycompany.grifon.mm_pre_alpha.engine.music.Player;
import com.mycompany.grifon.mm_pre_alpha.ui.LoginActivity;
import com.mycompany.grifon.mm_pre_alpha.ui.ProfileActivity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

public class RecyclerViewAdapterPosts extends RecyclerView.Adapter<RecyclerViewAdapterPosts.ViewHolder> {

    private Map<String,Post> originalData;//= new LinkedHashMap<>();
    private List<Post> mData ;//= new LinkedHashMap<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Player player;
    private String uuid;
    // for reposts
    // true = my profile, false = not mine
    private boolean profyleType;
    // true = ProfileActivity, false = NewsActivity
    private boolean activityType;

    private FirebaseUtils firebaseUtils;

    // data is passed into the constructor
    public RecyclerViewAdapterPosts(Context context, LinkedHashMap<String, Post>data, String uuid, boolean profileType, boolean activityType, FirebaseUtils firebaseUtils) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = new ArrayList<>(data.values());
        originalData=data;
        //        Collections.reverse(data);

        player = new Player();
        this.uuid = uuid;
        this.profyleType = profileType;
        this.activityType = activityType;

        this.firebaseUtils = firebaseUtils;
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
            PlainUser me = FirebaseAuthHelper.getInstance().getProfile().toPlain();

            Log.d("MY LOG:", "POSTS SET is correct ");

            String songName = mData.get(position).getSong().getName();
            String postText = mData.get(position).getText();
            String authorName = null;
            Post current = mData.get(position);
            if (!me.equals(current.getAuthor())) {
                authorName = current.getAuthor().getName();
                holder.tv_authorName.setText(authorName);
            }
            Log.d("MY LOG:", "Author name: " + authorName);
            int likes = current.getSong().getLikes();
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
        public Button btn_del;
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
            btn_del = (Button) itemView.findViewById(R.id.btn_del);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_like);
            //checkBox.setOnCheckedChangeListener(checkBoxListener);

            // if my profile - no reposts
            // and you can del posts
            if (profyleType) {
                btnv_repost.setVisibility(View.INVISIBLE);
            } else {
                btn_del.setVisibility(View.INVISIBLE);
            }

            // нереализованный функционал
            tv_likes.setVisibility(View.INVISIBLE);
            checkBox.setVisibility(View.INVISIBLE);
            //btn_del.setVisibility(View.INVISIBLE);
            btn_del.setVisibility(View.VISIBLE);

            btn_del.setOnClickListener(this);
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
                PlainUser me = FirebaseAuthHelper.getInstance().getProfile().toPlain();

                // if NewsActivity
                if (!activityType) {
                    // if author not I
                      if (me.equals(post.getAuthor())) {

                        Post repostedPost = new Post(post.getText(), post.getSong(), me,System.currentTimeMillis(),UUID.randomUUID().toString());
                        FirebasePathHelper.getInstance().writeNewPostDB(uuid, repostedPost);
                    } else {
                        // работает не так, как хотелось бы
                        btnv_repost.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // else - ProfileActivity
                    Post repostedPost = new Post(post.getText(), post.getSong(),me, System.currentTimeMillis(), UUID.randomUUID().toString());
                    FirebasePathHelper.getInstance().writeNewPostDB(uuid, repostedPost);
                }
            } else if (view.getId() == R.id.btn_del) {
                // todo: delete posts
                // somthing going wrong...
                String uuid = mData.get(getAdapterPosition()).getUuid();

                /*for (Post post : mData) {
                    if (post.getTimestamp().equals(timeStamp)) {

                    }
                }*/

                for(Iterator<Post> iter= mData.iterator();iter.hasNext(); ){
                    final Post myPost = iter.next();
                    if (myPost.getUuid().equals(uuid)) {
                        iter.remove();
                        originalData.remove(myPost.getUuid());
                        break;
                    }
                }


/*
//subscribersAdapter.replaceData();
                subscribersAdapter.replaceData(myPlainUsers);
            }*/

            //firebaseUtils.deletePostDB(timeStamp);
            //mData.remove(timeStamp);
            FirebasePathHelper.getInstance().updatePosts(originalData);
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
