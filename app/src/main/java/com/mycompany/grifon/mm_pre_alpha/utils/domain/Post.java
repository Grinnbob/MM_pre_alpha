package com.mycompany.grifon.mm_pre_alpha.utils.domain;

public class Post {
    private String text;
    private SongInfo song;
    private boolean type; //true = my post; false = not my

    public Post(){}

    public Post(String text, SongInfo song, boolean type){
        this.text = text;
        this.song = song;
        this.type = type;
    }

    public String getText() {return text;}
    public SongInfo getSong() {return song;}
    public boolean getType() {return type;}
}
