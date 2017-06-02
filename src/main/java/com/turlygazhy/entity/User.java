package com.turlygazhy.entity;

/**
 * Created by daniyar on 28.05.17.
 */
public class User {
    int id;
    private Long chatId;
    private String name;
    private String phoneNumber;
    private String city;
    private boolean sex;        // 0 - женщина, 1 - мужчина
    private String birthday;
    private boolean haveCar;
    private Long addedBy;

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    private boolean isAdded;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isHaveCar() {
        return haveCar;
    }

    public void setHaveCar(boolean haveCar) {
        this.haveCar = haveCar;
    }

    public Long getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(Long addedBy) {
        this.addedBy = addedBy;
    }

    public int getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "/id" + id + " - " + name + "\n";
    }
}
