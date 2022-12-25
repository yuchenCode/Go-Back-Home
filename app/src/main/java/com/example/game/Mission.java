package com.example.game;

public class Mission {

    private int name;
    private int imageId;

    public Mission(int name, int imageId){
        this.name = name;
        this.imageId = imageId;

    }

    public int getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
