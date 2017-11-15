package com.mycompany.grifon.mm_pre_alpha.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Post;

import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapterPosts extends RecyclerView.Adapter<RecyclerViewAdapterPosts.ViewHolder> {

    private List<Post> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Player player;

    // data is passed into the constructor
    public RecyclerViewAdapterPosts(Context context, List<Post> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

        player = new Player();
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
        if(!mData.isEmpty()) {
            Log.d("MY LOG:", "POSTS SET is correct ");
            String songName = mData.get(position).getSong().getName();
            String postText = mData.get(position).getText();
            int likes = mData.get(position).getSong().getLikes();
            holder.tv_songName.setText(songName);
            holder.tv_post_text.setText(postText);
            holder.tv_likes.setText(String.valueOf(likes));
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
        public Button btnv_play;
        public Button btnv_pause;
        public Button btnv_repost;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_songName = (TextView) itemView.findViewById(R.id.tv_recycler_item);
            tv_likes = (TextView) itemView.findViewById(R.id.tv_likes);
            tv_post_text = (TextView) itemView.findViewById(R.id.tv_text_post_item);
            btnv_play = (Button) itemView.findViewById(R.id.btn_play);
            btnv_pause = (Button) itemView.findViewById(R.id.btn_pause);
            btnv_repost = (Button) itemView.findViewById(R.id.btn_repost);

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
            if(view.getId() == R.id.btn_play) {
                player.startPlayback(mData.get(getAdapterPosition()).getSong().getUrl());
            } else if(view.getId() == R.id.btn_pause) {
                player.stopPlayback();
            } else if(view.getId() == R.id.btn_repost) {

            } //todo: add likes
        }
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
