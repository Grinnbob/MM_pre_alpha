package com.mycompany.grifon.mm_pre_alpha.data;

import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.mycompany.grifon.mm_pre_alpha.utils.domain.Profile;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vlad on 25.10.2017.
 */

public class PlainUser implements Serializable {
    private String name;
    private String uuid;
    /*private String information;
    private List<Profile> subscribers;*/


    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }


    public PlainUser(){

    }

  /*  public PlainUser(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }
*/
    public PlainUser(String name, String uuid/*, String information*/) { //Праивльно вызывать так: myProfile.getName(), user.getUid())
        this.name = name;
        this.uuid = uuid;
        //this.information = information;
        //this.subscribers = subscribers;
    }

    public PlainUser(FirebaseUser user) { //TODO: check all usages
        this(user.getDisplayName(), user.getUid());
    }
/*
   public String getInformation() {
        return information;
    }

    public List<Profile> getSubscribers() {
        return subscribers;
    }*/
}
