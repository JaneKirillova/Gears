package com.example.gears.events;

import org.json.JSONObject;

public class SuccessEventGetUser {
    private JSONObject response;

    public SuccessEventGetUser(JSONObject response) {
        this.response = response;
    }

    public JSONObject getResponse() {
        return response;
    }
}
