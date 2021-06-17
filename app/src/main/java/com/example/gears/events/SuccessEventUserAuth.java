package com.example.gears.events;

public class SuccessEventUserAuth {
    private String response;
    public SuccessEventUserAuth(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
