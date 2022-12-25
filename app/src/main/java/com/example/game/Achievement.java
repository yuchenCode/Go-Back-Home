package com.example.game;

public class Achievement {

    private int id;
    private int imageId;
    private String name;
    private String des;


    public Achievement(int id, int imageId, String name, String des){
        this.id = id;
        this.name = name;
        this.des = des;
        this.imageId = imageId;

    }

    public String getName() {
        return name;
    }

    public String getDes() {
        return des;
    }

    public int getImageId() {
        return imageId;
    }
}
