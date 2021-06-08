package com.example.gears.events;

import org.json.JSONObject;

public class SuccessEventGetPicture {
    private JSONObject response;

    public SuccessEventGetPicture(JSONObject response) {
        this.response = response;
    }

    public JSONObject getResponse() {
        return response;
    }
}
