package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

        registerButton.setOnClickListener(v -> {
            String loginString = login.getText().toString().trim();
            String password1String = password1.getText().toString().trim();
            String password2String = password2.getText().toString().trim();
            if (loginString.isEmpty() || password1String.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Create your Email and Password to register", Toast.LENGTH_SHORT).show();
            } else {
                if (password1String.length() < 5) {
                    Toast.makeText(RegisterActivity.this, "Password must contains al least 5 symbols", Toast.LENGTH_SHORT).show();
                } else {
                    if (!password1String.equals(password2String)) {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    } else {
                        // TODO занести пользователя в базу
                        Toast.makeText(getApplicationContext(), "Welcome to the game, " + loginString, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, PersonalAccountActivity.class));
                    }
                }
            }
        });
    }
}
