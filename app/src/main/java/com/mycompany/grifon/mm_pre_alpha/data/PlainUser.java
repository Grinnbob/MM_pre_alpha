package com.mycompany.grifon.mm_pre_alpha.data;

import java.io.Serializable;

/**
 * Created by Vlad on 25.10.2017.
 */

public class PlainUser implements Serializable {
    private String name;
    private String uuid;

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }


    public PlainUser(){

    }
    public PlainUser(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }
}
