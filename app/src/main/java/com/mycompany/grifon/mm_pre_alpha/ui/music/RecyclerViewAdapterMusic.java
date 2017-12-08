package com.mycompany.grifon.mm_pre_alpha.ui.music;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.SongInfo;
import com.mycompany.grifon.mm_pre_alpha.engine.music.Player;
import com.mycompany.grifon.mm_pre_alpha.ui.AddSongToPostActivity;
import com.mycompany.grifon.mm_pre_alpha.ui.LoginActivity;

import java.util.Collections;
import java.util.List;

//https://ru.stackoverflow.com/questions/549695/%D1%80%D0%B0%D1%81%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D0%B5-%D0%BC%D0%B5%D0%B6%D0%B4%D1%83-%D0%B0%D0%B9%D1%82%D0%B5%D0%BC%D0%B0%D0%BC%D0%B8-recyclerview
//чтобы задать отступ между message_item
public class RecyclerViewAdapterMusic extends RecyclerView.Adapter<RecyclerViewAdapterMusic.ViewHolder> {

    private List<SongInfo> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // player
    private Player player;

    // data is passed into the constructor
    public RecyclerViewAdapterMusic(Context context, List<SongInfo> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;

        this.player = LoginActivity.player;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_music_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String songName = mData.get(position).getName();
        holder.tv_songName.setText(songName);
        holder.toogleButton_play.setChecked(false);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        TextView tv_songName;
        ToggleButton toogleButton_play;
        Button btnv_plusSong;

        ViewHolder(View itemView) {
            super(itemView);
            try {
                tv_songName = (TextView) itemView.findViewById(R.id.tv_recycler_item);
                btnv_plusSong = (Button) itemView.findViewById(R.id.btn_plus_song);

                btnv_plusSong.setOnClickListener(this);
                itemView.setOnClickListener(this);

                toogleButton_play = (ToggleButton) itemView.findViewById(R.id.tbtn_play);
                toogleButton_play.setOnCheckedChangeListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // play song
                try {
                    player.startPlayback(mData.get(getAdapterPosition()).getUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // stop song
                try {
                    player.stopPlayback();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_plus_song) {
                try {
                    Intent intent = new Intent(context, AddSongToPostActivity.class);
                    SongInfo songInfo = mData.get(getAdapterPosition());
                    intent.putExtra("song_name", songInfo.getName());
                    intent.putExtra("song_url", songInfo.getUrl());
                    //intent.putExtra("song_likes", songInfo.getLikes());
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // convenience method for getting data at click position
    public SongInfo getItem(int id) {
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