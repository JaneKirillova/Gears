package com.example.gears;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button loginButton, registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPrefManager.getInstance(getApplicationContext()).setPictureIsLoaded(false);
        SharedPrefManager.getInstance(getApplicationContext()).setWasPlayedGame(false);

        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.change_password_button);
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//            SharedPrefManager.getInstance(getApplicationContext()).writeGame("123", "FIRSTPLAYER");
//            startActivity(new Intent(MainActivity.this, PersonalAccountActivity.class));
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, GameActivity.class));
//            }
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
//                SharedPrefManager.getInstance(getApplicationContext()).writeGame("123", "SECONDPLAYER");
                startActivity(new Intent(MainActivity.this, RatingActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}