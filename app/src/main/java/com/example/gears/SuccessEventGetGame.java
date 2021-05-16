package com.example.gears;

import org.json.JSONObject;

public class SuccessEventGetGame {
    private JSONObject response;

    public SuccessEventGetGame(JSONObject response) {
        this.response = response;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }
}
