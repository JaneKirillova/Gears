package com.example.gears.events;

import org.json.JSONObject;

public class SuccessEventGetBoard {
    private JSONObject response;

    public SuccessEventGetBoard(JSONObject response) {
        this.response = response;
    }

    public JSONObject getResponse() {
        return response;
    }
}
