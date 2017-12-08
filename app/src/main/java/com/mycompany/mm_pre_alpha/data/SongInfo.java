package com.mycompany.mm_pre_alpha.data;

// class that represents the data from Firebase
// (for database)
public class SongInfo {
    public String name;
    public String url;
    public int likes;

    private SongInfo(){}

    public SongInfo(String name, String url, int likes){
        this.name = name;
        this.url = url;
        this.likes = likes;
    }

    public String getName() {return name;}
    public String getUrl() {return url;}
    public int getLikes() {return likes;}
}
