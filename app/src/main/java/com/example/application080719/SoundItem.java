package com.example.application080719;

public class SoundItem {

    private String itemTitle;

    private int itemResourceId;

    private boolean itemLoaded, itemSelected, itemPlaying = false;

    public void setItemLoaded(boolean itemLoaded) {
        this.itemLoaded = itemLoaded;
    }

    public void setItemSelected(boolean itemSelected) {
        this.itemSelected = itemSelected;
    }

    public void setItemPlaying(boolean itemPlaying) {
        this.itemPlaying = itemPlaying;
    }

    public boolean isItemLoaded() {
        return itemLoaded;
    }

    public boolean isItemSelected() {
        return itemSelected;
    }

    public boolean isItemPlaying() {
        return itemPlaying;
    }

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
