package com.mycompany.grifon.mm_pre_alpha.data;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Post implements Comparable<Post> {
    private String text;
    private SongInfo song;
    private PlainUser author;
    private long timestamp;


    private String uuid;

    public Post() {
    }



    // not mine post
    public Post(String text, SongInfo song, PlainUser author, long timestamp,String uuid) {
        this.text = text;
        this.song = song;
        this.author = author;
        this.timestamp = timestamp;
        this.uuid=uuid;
    }

    public String getText() {
        return text;
    }

    public SongInfo getSong() {
        return song;
    }

    public PlainUser getAuthor() {
        return author;
    }

    public long getTimestamp() {
        return timestamp;
    }


    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
    @Override
    public int compareTo(@NonNull Post post) {
        return compare(post.timestamp, timestamp);
    }
}
