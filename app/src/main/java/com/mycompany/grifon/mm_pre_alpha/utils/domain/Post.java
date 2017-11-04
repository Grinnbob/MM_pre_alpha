package com.mycompany.grifon.mm_pre_alpha.utils.domain;

public class Post {
    final String text;
    final SongInfo song;
    //TODO: add time

    public Post(String text, SongInfo song){
        this.text = text;
        this.song = song;
    }

    public String getText() {return text;}
    public SongInfo getSong() {return song;}
}
