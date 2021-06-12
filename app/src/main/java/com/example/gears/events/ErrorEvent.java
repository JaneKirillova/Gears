package com.example.gears.events;

import com.android.volley.VolleyError;

public class ErrorEvent {
    private VolleyError error;

    public ErrorEvent(VolleyError error) {
        this.error = error;
    }

    public VolleyError getError() {
        return error;
    }
}
