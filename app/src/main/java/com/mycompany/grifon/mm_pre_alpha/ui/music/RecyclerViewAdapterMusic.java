package com.mycompany.grifon.mm_pre_alpha.ui.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mycompany.grifon.mm_pre_alpha.R;
import com.mycompany.grifon.mm_pre_alpha.data.SongInfo;
import com.mycompany.grifon.mm_pre_alpha.engine.music.MediaPlayerService;
import com.mycompany.grifon.mm_pre_alpha.ui.AddSongToPostActivity;

import java.util.Collections;
import java.util.List;

//https://ru.stackoverflow.com/questions/549695/%D1%80%D0%B0%D1%81%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D0%B5-%D0%BC%D0%B5%D0%B6%D0%B4%D1%83-%D0%B0%D0%B9%D1%82%D0%B5%D0%BC%D0%B0%D0%BC%D0%B8-recyclerview
//чтобы задать отступ между message_item
public class RecyclerViewAdapterMusic extends RecyclerView.Adapter<RecyclerViewAdapterMusic.ViewHolder> {

    private List<SongInfo> mData = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // player
    private MediaPlayerService player;
    boolean serviceBound = false;
    private Context context;

    // data is passed into the constructor
    public RecyclerViewAdapterMusic(Context context, List<SongInfo> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;

        //mediaPlayerService = new MediaPlayerService();
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

            //Toast.makeText(context, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    // The following function creates a new instance of the MediaPlayerService and sends a media file to play
    private void playAudio(String media) {
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(context, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            context.startService(playerIntent);
            context.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
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
        public Button btnv_plusSong;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_songName = (TextView) itemView.findViewById(R.id.tv_recycler_item);
            btnv_play = (Button) itemView.findViewById(R.id.btn_play);
            btnv_pause = (Button) itemView.findViewById(R.id.btn_pause);
            btnv_plusSong = (Button) itemView.findViewById(R.id.btn_plus_song);

            btnv_play.setOnClickListener(this);
            btnv_pause.setOnClickListener(this);
            btnv_plusSong.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View view) {
          if (view.getId() == R.id.btn_play) {
                if (serviceBound) {
                    // подпорка, надо нормально сделать
                    context.unbindService(serviceConnection);
                    serviceBound = false;
                    //service is active
                    player.stopSelf();

                    playAudio(mData.get(getAdapterPosition()).getUrl());
                } else {
                    playAudio(mData.get(getAdapterPosition()).getUrl());
                }
            } else if (view.getId() == R.id.btn_pause) {
                if (serviceBound) {
                    // подпорка, надо нормально сделать
                    context.unbindService(serviceConnection);
                    serviceBound = false;
                    //service is active
                    player.stopSelf();
                }
                //mediaPlayerService.stopPlayback();
            } else if (view.getId() == R.id.btn_plus_song) {
                Intent intent = new Intent(context, AddSongToPostActivity.class);
                SongInfo songInfo = mData.get(getAdapterPosition());
                intent.putExtra("song_name", songInfo.getName());
                intent.putExtra("song_url", songInfo.getUrl());
                //intent.putExtra("song_likes", songInfo.getLikes());
                context.startActivity(intent);
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