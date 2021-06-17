package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gears.events.SuccessEventGetPicture;
import com.example.gears.events.SuccessEventGetUser;
import com.example.gears.events.SuccessEventUserAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Button registerButton;
    EditText login, password1, password2;
    String loginString, passwordString, userToken;
    Long userId;
    EventBus eventBus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        registerButton = findViewById(R.id.change_password_button);
        login = findViewById(R.id.username);
        password1 = findViewById(R.id.change_password);
        password2 = findViewById(R.id.change_username);

        registerButton.setOnClickListener(v -> registerUser());
    }


    private void registerUser() {
        loginString = login.getText().toString().trim();
        passwordString = password1.getText().toString().trim();
        String password2String = password2.getText().toString().trim();

        if (loginString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Create your Email and Password to register", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordString.equals(password2String)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        registerRequest();

    }

    private void registerRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventBus.post(new SuccessEventUserAuth(response));
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
                params.put("username", loginString);
                params.put("password", passwordString);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getUserRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_USER + "/" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("\n\n\n" + response + "\n\n\n");
                            eventBus.post(new SuccessEventGetUser(obj));
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
                params.put("Content-Type", "application/json");
                params.put("token", userToken);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
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
    public void onSuccessEventUserAuth(SuccessEventUserAuth event) {
        String response = event.getResponse();
        try {
            JSONObject obj = new JSONObject(response);
            System.out.println("\n\n\n" + response + "\n\n\n");
            userToken = obj.getString("token");
            userId = obj.getLong("id");
            getUserRequest();
        } catch (JSONException e) {
            System.out.print("ОШИБКА1: ");
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetUser(SuccessEventGetUser event) {
        JSONObject obj = event.getResponse();
        try {
            User newUser = new User(
                    userToken,
                    obj.getString("username"),
                    obj.getString("password"),
                    obj.getLong("id"),
                    obj.getInt("points"),
                    obj.getInt("totalNumberOfGames"),
                    obj.getInt("numberOfGamesWon"),
                    obj.getInt("numberOfGamesLost"));
            SharedPrefManager.getInstance(getApplicationContext()).userLogin(newUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.finish();
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
    }
}
