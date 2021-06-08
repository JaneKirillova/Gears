package com.example.gears.events;

public class SuccessEventFindOpponent {
    private String response;
    public SuccessEventFindOpponent(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
