package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class Win extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        Button gameButt = (Button) findViewById(R.id.retryButtonLose);
        gameButt.setOnClickListener(view -> {
            finish();
            final Intent i = new Intent(this, GameActivity.class);
            startActivity(i);
        });

        Button mainButt = (Button) findViewById(R.id.mainButton);
        mainButt.setOnClickListener(view -> {
            finish();
        });
    }
}