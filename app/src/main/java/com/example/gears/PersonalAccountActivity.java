package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class PersonalAccountActivity extends AppCompatActivity {
    Button startGame;
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


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_GEt_USER,
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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", oldUser.getToken());
                params.put("id", oldUser.getId().toString());
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


    private void findOpponent() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!FIND OPPONENT!!!!!!!!!!!!!!!!!!!!!!!!!!1");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_FIND_OPPONENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            String gameId = (String) obj.names().get(0);
                            Boolean isFirstPlayer = obj.getBoolean(gameId);
                            SharedPrefManager.getInstance(getApplicationContext()).writeGame(gameId, isFirstPlayer);
                            

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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("token", user.getToken());
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
