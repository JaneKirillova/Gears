package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gears.events.SuccessEventFindOpponent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FIndGameActivity extends AppCompatActivity {
    Button findOpponent;
    EventBus eventBus = EventBus.getDefault();
    User user;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);
        findOpponent = findViewById(R.id.find_opponent);
        findOpponent.setOnClickListener(v -> findOpponent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
        user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventFindOpponent(SuccessEventFindOpponent event) {
        try {
            JSONObject obj = new JSONObject(event.getResponse());
            System.out.println(event.getResponse());
            String gameId = (String) obj.names().get(0);
            boolean isFirstPlayer = obj.getBoolean(gameId);
            String playerNum;
            if (isFirstPlayer) {
                playerNum = "FIRSTPLAYER";
            } else {
                playerNum = "SECONDPLAYER";
            }
            SharedPrefManager.getInstance(getApplicationContext()).writeGame(gameId, playerNum);
            startActivity(new Intent(getApplicationContext(), GameActivity.class));

        } catch (JSONException e) {
            System.out.print("ОШИБКА1: ");
            e.printStackTrace();
        }
    }

    private void findOpponent() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!FIND OPPONENT!!!!!!!!!!!!!!!!!!!!!!!!!!1");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_FIND_OPPONENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventBus.post(new SuccessEventFindOpponent(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body;
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        if(error.networkResponse.data!=null) {
                            try {
                                body = new String(error.networkResponse.data,"UTF-8");
                                Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json");
                params.put("token", user.getToken());
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
