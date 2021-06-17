package com.example.gears.events;

import com.android.volley.VolleyError;

public class ErrorEventGetMessage {
    private VolleyError error;

    public ErrorEventGetMessage(VolleyError error) {
        this.error = error;
    }

    public VolleyError getError() {
        return error;
    }
}
