package com.example.gears;

import org.json.JSONObject;

public class SuccessEventGetGameFirstTime {
    private JSONObject response;

    public SuccessEventGetGameFirstTime(JSONObject response) {
        this.response = response;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }
}
