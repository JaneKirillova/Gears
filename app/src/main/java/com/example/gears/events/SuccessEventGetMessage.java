package com.example.gears.events;

import org.json.JSONObject;

public class SuccessEventGetMessage {
    private JSONObject response;

    public SuccessEventGetMessage(JSONObject response) {
        this.response = response;
    }

    public JSONObject getResponse() {
        return response;
    }
}
