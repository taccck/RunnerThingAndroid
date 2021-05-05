package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static SoundPool soundPool;
    static int JUMP_SOUND;
    static int GAME_OVER_SOUND;
    static int POINT_SOUND;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playButt = (Button) findViewById(R.id.play_game_butt);
        playButt.setOnClickListener(view -> {
            final Intent i = new Intent(this, GameActivity.class);
            startActivity(i);
        });

        Button aboutButt = (Button) findViewById(R.id.aboutButton);
        aboutButt.setOnClickListener(view -> {
            final Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        });

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        JUMP_SOUND = soundPool.load(this, R.raw.jump, 1);
        POINT_SOUND = soundPool.load(this, R.raw.point, 1);
        GAME_OVER_SOUND = soundPool.load(this, R.raw.gameover, 1);

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true); //loop it
        mediaPlayer.setVolume(.5f,.5f); //can't hear sound effects otherwise

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch musicSwitch = (Switch) findViewById(R.id.music_switch);
        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            else {
                mediaPlayer.stop();

                try {
                    mediaPlayer.prepare(); //move to prepare state
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void PlayJump(){
        soundPool.play(JUMP_SOUND, 1, 1, 0, 0, 1); //play jump sound
    }

    public static void PlayPoint(){
        soundPool.play(POINT_SOUND, 1, 1, 0, 0, 1); //play point sound
    }

    public static void PlayGameOver(){
        soundPool.play(GAME_OVER_SOUND, 1, 1, 0, 0, 1); //play game over sound
    }
}