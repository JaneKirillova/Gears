package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.AuthFailureError;
import com.example.gears.GameObjects.Board;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PersonalAccountActivity extends AppCompatActivity {
    Button startGame;
    EventBus eventBus = EventBus.getDefault();
    TextView userId, userLogin, userPassword;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);

        userId = findViewById(R.id.userId);
        userLogin = findViewById(R.id.userLogin);
        userPassword = findViewById(R.id.userPassword);

        final User oldUser = SharedPrefManager.getInstance(this).getUser();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_USER + "/" + oldUser.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("\n\n\n" + response + "\n\n\n");
                            userId.setText(String.valueOf(obj.getInt("points")));
                            userLogin.setText(obj.getString("password"));
                            userPassword.setText(obj.getString("username"));


                            User newUser = new User(
                                    obj.getString("token"),
                                    obj.getString("username"),
                                    obj.getString("password"),
                                    obj.getLong("id"),
                                    obj.getInt("points"));
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(newUser);

                        } catch (JSONException e) {
                            System.out.print("ОШИБКА1: ");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("ОШИБКА2: ");
                        String s = new String(error.networkResponse.data, Charset.defaultCharset());
                        System.out.println(s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json");
                params.put("token", oldUser.getToken());
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);



        user = SharedPrefManager.getInstance(this).getUser();

        userId.setText(String.valueOf(user.getToken()));
        userLogin.setText(user.getUsername());
        userPassword.setText(user.getPassword());

        startGame = findViewById(R.id.startGame);


        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findOpponent();
//                startActivity(new Intent(PersonalAccountActivity.this, GameActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
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
                        System.out.print("ОШИБКА2: ");
                        System.out.println(error.toString());
//                        String s = new String(error.networkResponse.data, Charset.defaultCharset());
//                        System.out.println(s);
//                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", "Ilya");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json");
                params.put("token", "hello");
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
