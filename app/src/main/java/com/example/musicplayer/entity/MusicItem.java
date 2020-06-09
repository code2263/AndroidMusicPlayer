package com.example.musicplayer.entity;

import android.graphics.Bitmap;

/**
 * 音乐实体ITEM
 */
public class MusicItem {
    /** 音乐ID */
    private Integer id;

    /** 音乐文件名 */
    private String display_name;

    /** 歌曲名 */
    private String title;

    /** 音乐时长 */
    private Integer duration = 0;

    /** 歌手名 */
    private String artist;

    /** 专辑名 */
    private String album;

    /** 年代 */
    private String year;

    /** 音乐格式 */
    private String mime_type;

    /** 文件大小 */
    private String size;

    /** 文件路径 */
    private String data;

    /** 是否为音乐 */
    private String is_music;

    /** 专辑ID */
    private Integer album_id;

    /** 专辑图片 */
    private String album_art;

    /** 是否本地音乐 */
    private Boolean on_local;

    /** 专辑图片bitmap */
    private Bitmap album_art_bitmap;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIs_music() {
        return is_music;
    }

    public void setIs_music(String is_music) {
        this.is_music = is_music;
    }

    public Integer getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(Integer album_id) {
        this.album_id = album_id;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public Boolean getOn_local() {
        return on_local;
    }

    public void setOn_local(Boolean on_local) {
        this.on_local = on_local;
    }

    public Bitmap getAlbum_art_bitmap() {
        return album_art_bitmap;
    }

    public void setAlbum_art_bitmap(Bitmap album_art_bitmap) {
        this.album_art_bitmap = album_art_bitmap;
    }

    public MusicItem(Integer id, String display_name, String title, Integer duration, String artist, String album, String year, String mime_type, String size, String data, String is_music, Integer album_id, String album_art, Boolean on_local, Bitmap album_art_bitmap) {
        this.id = id;
        this.display_name = display_name;
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.mime_type = mime_type;
        this.size = size;
        this.data = data;
        this.is_music = is_music;
        this.album_id = album_id;
        this.album_art = album_art;
        this.on_local = on_local;
        this.album_art_bitmap = album_art_bitmap;
    }

    public MusicItem(Integer id, String title, String artist, Boolean on_local, Bitmap album_art_bitmap, String file_link, Integer duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.on_local = on_local;
        this.album_art_bitmap = album_art_bitmap;
        this.data = file_link;
        this.duration = duration;
    }

    public MusicItem(){};
}
