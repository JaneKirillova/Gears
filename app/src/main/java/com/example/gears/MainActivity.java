package com.example.gears;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button loginButton, registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.register);
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            SharedPrefManager.getInstance(getApplicationContext()).writeGame("123", "FIRSTPLAYER");
//            startActivity(new Intent(MainActivity.this, GameActivity.class));
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, GameActivity.class));
//            }
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
//                SharedPrefManager.getInstance(getApplicationContext()).writeGame("123", "SECONDPLAYER");
//                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });
    }
}