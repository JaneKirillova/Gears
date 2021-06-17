package com.example.gears;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
import com.example.gears.events.SuccessEventLogin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    EditText login, password;
    EventBus eventBus = EventBus.getDefault();
    String loginString, passwordString, userToken;
    Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.login);
        login = findViewById(R.id.username);
        password = findViewById(R.id.change_password);

        loginButton.setOnClickListener(v -> loginUser());
//        loginButton.setOnClickListener(v -> {
//            SharedPrefManager.getInstance(getApplicationContext()).userLogin(new User("token", "username", "password", 0L));
//            startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class));
//        });
    }


    public static Bitmap convertCompressedByteArrayToBitmap(byte[] src){
        return BitmapFactory.decodeByteArray(src, 0, src.length);
    }

    private void loginUser() {
        loginString = login.getText().toString().trim();
        passwordString = password.getText().toString().trim();
        if (loginString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Enter your Email and Password to login", Toast.LENGTH_SHORT).show();
            return;
        }
        loginRequest();
    }

    private void loginRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventBus.post(new SuccessEventLogin(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body;
                        //get status code here
                        String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        if(error.networkResponse.data!=null) {
                            try {
                                body = new String(error.networkResponse.data,"UTF-8");
                                Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        //do stuff with the body...
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

    private void getPicture() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_LOAD_IMAGE + "/" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            eventBus.post(new SuccessEventGetPicture(obj));
                        } catch (JSONException e) {
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
    public void onSuccessEventLogin(SuccessEventLogin event) {
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
            getPicture();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetPicture(SuccessEventGetPicture event) {
        String str = null;
        try {
            str = event.getResponse().getString("picture");
            if (!str.equals("null")) {
                byte[] picture = new byte[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    picture = java.util.Base64.getDecoder().decode(str);
                }
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("profile", Context.MODE_PRIVATE);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                File mypath = new File(directory, "picture.png");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    fos.write(picture);
                    fos.close();
                } catch (Exception e) {
                    Log.e("SAVE_IMAGE", e.getMessage(), e);
                }
                SharedPrefManager.getInstance(getApplicationContext()).setPictureIsLoaded(true);
                SharedPrefManager.getInstance(getApplicationContext()).savePhotoDirectory(mypath.getPath());
            } else {
                SharedPrefManager.getInstance(getApplicationContext()).setPictureIsLoaded(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
    }


}
