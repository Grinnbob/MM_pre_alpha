package com.mycompany.grifon.mm_pre_alpha.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post implements Comparable<Post> {
    private String text;
    private SongInfo song;
    private PlainUser author;
    private String timestamp;

    public Post() {
    }

    // my post
    public Post(String text, SongInfo song, String timestamp) {
        this.text = text;
        this.song = song;
        this.timestamp = timestamp;
        //this.author = null;
    }

    // not mine post
    public Post(String text, SongInfo song, PlainUser author, String timestamp) {
        this.text = text;
        this.song = song;
        this.author = author;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Post post) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate1 = null;
        Date parsedDate2 = null;
        try {
            parsedDate1 = dateFormat.parse(post.getTimestamp());
            parsedDate2 = dateFormat.parse(getTimestamp());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert parsedDate2 != null;
        return parsedDate2.compareTo(parsedDate1);
    }
}
