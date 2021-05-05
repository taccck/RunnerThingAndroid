package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity
        implements View.OnTouchListener {

    GameView gameView;
    Jump jump;
    Handler jumpHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jump = new Jump();
        gameView = new GameView(this, jump);
        gameView.setOnTouchListener(this);
        setContentView(gameView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //start jump
                if (jump.takeInput){
                    MainActivity.PlayJump();
                    jump.takeInput = false;
                    jump.rising = true;
                    jump.currSpeed = jump.maxSpeed;
                }
                break;

            case MotionEvent.ACTION_UP: //end jump
                jump.rising = false;
                return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        gameView.resume();
        if (jumpHandler == null){
            jumpHandler = new Handler();
        }
        jump.resume();
        jump.Tick(jumpHandler);
        super.onResume();
    }

    @Override
    protected void onPause() {
        gameView.pause();
        jump.pause();
        super.onPause();
    }

    public void Win(){
        finish();
        final Intent i = new Intent(this, Win.class);
        startActivity(i);
    }

    public void GameOver(){
        finish();
        final Intent i = new Intent(this, GameOver.class);
        startActivity(i);
    }
}
