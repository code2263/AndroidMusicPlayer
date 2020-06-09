package com.example.musicplayer.entity;

import android.graphics.Bitmap;

/**
 * 基本界面(主界面)在线音乐排行榜大图ITEM
 */
public class MainOnlienItem {
    private int type;

    private Bitmap image;

    private String rank1;

    private String rank2;

    private String rank3;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getRank1() {
        return rank1;
    }

    public void setRank1(String rank1) {
        this.rank1 = rank1;
    }

    public String getRank2() {
        return rank2;
    }

    public void setRank2(String rank2) {
        this.rank2 = rank2;
    }

    public String getRank3() {
        return rank3;
    }

    public void setRank3(String rank3) {
        this.rank3 = rank3;
    }

    public MainOnlienItem(int type, Bitmap image, String rank1, String rank2, String rank3) {
        this.type = type;
        this.image = image;
        this.rank1 = rank1;
        this.rank2 = rank2;
        this.rank3 = rank3;
    }
}
