package com.turlygazhy.entity;

/**
 * Created by lol on 08.06.2017.
 */
public class News {
    private int id;
    private String title;
    private String text;
    private String date;
    private String photo;
    private boolean show;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "<b>" + title + "</b>\n" + text + "\n" + date;
    }

    public void addPhoto(String photoId) {
        if (photo == null) {
            photo = "";
        }
        photo += photoId + ";";
    }
}
