package com.studyverse.server.Model;

public class Family {
    private int id;
    private String name;
    private String avatar;
    private String email;

    public Family() {

    }

    public Family(int id, String name, String avatar, String email) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
