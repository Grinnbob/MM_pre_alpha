package com.mycompany.grifon.mm_pre_alpha.utils;

// class that represents the data from Firebase
// (for database)
public class UploadInfo {
    public String name;
    public String url;
    public int likes;

    public UploadInfo(String name, String url, int likes){
        this.name = name;
        this.url = url;
        this.likes = likes;
    }
}
