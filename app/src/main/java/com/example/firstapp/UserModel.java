package com.example.firstapp;

public class UserModel {


    private String name;
    private String email;
    private String phone;
    private String id;
    private String imageRes;



    public UserModel() {
    }

    public UserModel(String id, String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    public String getImageRes() {
        return imageRes;
    }

    public void setImageRes(String imageRes) {
        this.imageRes = imageRes;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

