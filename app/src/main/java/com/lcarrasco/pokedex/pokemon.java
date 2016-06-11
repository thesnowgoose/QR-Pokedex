package com.lcarrasco.pokedex;

import android.graphics.Bitmap;

public class pokemon {

    private int id;
    private String name;
    private String type1;
    private String type2;
    private String details;

    public pokemon (int id, String name){
        this.id = id;
        this.name = name;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public String getDetails() {
        return details;
    }

}
