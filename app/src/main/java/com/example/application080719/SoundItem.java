package com.example.application080719;

public class SoundItem {

    private String itemTitle;

    private int itemResourceId;

    public String getItemTitle() {
        return itemTitle;
    }

    public int getItemResourceId() {
        return itemResourceId;
    }

    public SoundItem(String itemTitle, int itemResourceId) {
        this.itemTitle = itemTitle;
        this.itemResourceId = itemResourceId;
    }
}
