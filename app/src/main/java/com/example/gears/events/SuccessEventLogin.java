package com.example.gears.events;

public class SuccessEventLogin {
    private String response;
    public SuccessEventLogin(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
