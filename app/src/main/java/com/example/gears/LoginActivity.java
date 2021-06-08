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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    EditText login, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.login);
        login = findViewById(R.id.username);
        password = findViewById(R.id.password);

        loginButton.setOnClickListener(v -> loginUser());
//        loginButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class)));
    }


    private void loginUser() {
        final String loginString = login.getText().toString().trim();
        final String passwordString = password.getText().toString().trim();
        if (loginString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Enter your Email and Password to login", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("\n\n\n" + response + "\n\n\n");

                                User user = new User(
                                        obj.getString("token"),
                                        loginString,
                                        passwordString,
                                        obj.getLong("id"));
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                finish();
                                startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class));
                        } catch (JSONException e) {
                            System.out.print("ОШИБКА1: ");
                            e.printStackTrace();
                        }
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


//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.print("ОШИБКА2: ");
//                        System.out.println(error.getMessage());
////                        String s = new String(error.networkResponse.data, Charset.defaultCharset());
////                        System.out.println(s);
////                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                    }
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

}
