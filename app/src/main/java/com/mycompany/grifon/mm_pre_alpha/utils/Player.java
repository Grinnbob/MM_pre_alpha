package com.mycompany.grifon.mm_pre_alpha.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

// full easy player example
//http://startandroid.ru/ru/uroki/vse-uroki-spiskom/236-urok-126-media-mediaplayer-audiovideo-pleer-osnovnye-vozmozhnosti.html
public class Player {

    private MediaPlayer mediaPlayer;

    public Player(){}

    // старт
    public void startPlayback(String url) {
        stopPlayback();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // стоп
    public void stopPlayback() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
