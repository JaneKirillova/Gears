package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


        loginButton.setOnClickListener(v -> {
            String loginString = login.getText().toString().trim();
            String passwordString = password.getText().toString().trim();
            if (loginString.isEmpty() || passwordString.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter your Email and Password to login", Toast.LENGTH_SHORT).show();
            } else {
                // TODO связь с сервером, получение ответа есть ли такое сочетание логин + пароль
                if (passwordString.length() < 5) {
                    Toast.makeText(LoginActivity.this, "Password must contains al least 5 symbols", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Welcome to the game, " + loginString, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, PersonalAccountActivity.class));
                }
            }
        });
    }
}
