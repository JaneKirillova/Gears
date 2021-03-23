package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    Button registerButton;
    EditText login, password1, password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        registerButton = findViewById(R.id.register);
        login = findViewById(R.id.username);
        password1 = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);

        registerButton.setOnClickListener(v -> registerUser());
    }


    private void registerUser() {
        String loginString = login.getText().toString().trim();
        String password1String = password1.getText().toString().trim();
        String password2String = password2.getText().toString().trim();

        if (loginString.isEmpty() || password1String.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Create your Email and Password to register", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password1String.length() < 5) {
            Toast.makeText(RegisterActivity.this, "Password must contains al least 5 symbols", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password1String.equals(password2String)) {
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("username"),
                                        userJson.getString("password")
                                );

                                // TODO add user to the base (??)

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                //starting the profile activity
                                finish();
                                startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        User user = new User(1, loginString, password1String);
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        finish();
                        startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", loginString);
                params.put("password", password1String);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }
}
