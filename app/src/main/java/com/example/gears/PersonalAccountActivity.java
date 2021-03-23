package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PersonalAccountActivity extends AppCompatActivity {
    Button startGame;
    TextView userId, userLogin, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);


        userId = findViewById(R.id.userId);
        userLogin = findViewById(R.id.userLogin);
        userPassword = findViewById(R.id.userPassword);

        User user = SharedPrefManager.getInstance(this).getUser();

        userId.setText(String.valueOf(user.getId()));
        userLogin.setText(user.getUsername());
        userPassword.setText(user.getPassword());

        startGame = findViewById(R.id.startGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalAccountActivity.this, GameActivity.class));
            }
        });

    }
}
