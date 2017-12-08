package com.mycompany.mm_pre_alpha.engine.music;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

public class Player implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;

    // старт
    public void startPlayback(String url) {
        stopPlayback();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    // стоп
    public void stopPlayback() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}