package com.bilocker.model;

public class ItemCategory {

    private int image;
    private String name;

    public ItemCategory(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
