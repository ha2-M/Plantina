package com.example.firstapp;

public class ChatModel {

    private String message;

    private String type;


    public ChatModel() {
    }

    public ChatModel(String message, String type) {
        this.message = message;
        this.type = type;

    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

}
