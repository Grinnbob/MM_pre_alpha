package com.mycompany.grifon.mm_pre_alpha.data;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

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


    @Override
    public String toString() {
        return "PlainUser{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlainUser plainUser = (PlainUser) o;

        if (name != null ? !name.equals(plainUser.name) : plainUser.name != null) return false;
        return uuid.equals(plainUser.uuid);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + uuid.hashCode();
        return result;
    }
/*
   public String getInformation() {
        return information;
    }

    public List<Profile> getSubscribers() {
        return subscribers;
    }*/
}
