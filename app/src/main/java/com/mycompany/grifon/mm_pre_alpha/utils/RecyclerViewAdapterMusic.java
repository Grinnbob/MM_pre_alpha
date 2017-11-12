package com.mycompany.grifon.mm_pre_alpha.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.SongInfo;

import java.util.Collections;
import java.util.List;

//https://ru.stackoverflow.com/questions/549695/%D1%80%D0%B0%D1%81%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D0%B5-%D0%BC%D0%B5%D0%B6%D0%B4%D1%83-%D0%B0%D0%B9%D1%82%D0%B5%D0%BC%D0%B0%D0%BC%D0%B8-recyclerview
//чтобы задать отступ между message_item
public class RecyclerViewAdapterMusic extends RecyclerView.Adapter<RecyclerViewAdapterMusic.ViewHolder>  {

    private List<SongInfo> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Player player;

    // data is passed into the constructor
    public RecyclerViewAdapterMusic(Context context, List<SongInfo> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

        player = new Player();
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
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_songName;
        public Button btnv_play;
        public Button btnv_pause;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_songName = (TextView) itemView.findViewById(R.id.tv_recycler_item);
            btnv_play = (Button) itemView.findViewById(R.id.btn_play);
            btnv_pause = (Button) itemView.findViewById(R.id.btn_pause);

            btnv_play.setOnClickListener(this);
            btnv_pause.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
            // хз что это, возможно не нужно
            //if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            if(view.getId() == R.id.btn_play) {
                player.startPlayback(mData.get(getAdapterPosition()).getUrl());
            } else if(view.getId() == R.id.btn_pause) {
                player.stopPlayback();
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