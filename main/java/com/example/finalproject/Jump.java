package com.example.finalproject;

import android.os.Handler;
import android.util.Log;

public class Jump {

    public boolean canRun = true;
    public boolean rising = false; //when going up
    public boolean takeInput; //prevents double jumping
    public float currHeight = 0;
    float maxHeight;
    public float currSpeed = 0;
    public float maxSpeed;
    float accelerationConst; //gravity
    long delay = 20L; //50 per sec

    public void Create(float _maxHeight, float _maxSpeed){
        maxHeight = _maxHeight;
        maxSpeed = _maxSpeed;
        accelerationConst = 2;
        accelerationConst *= maxSpeed / delay; //set acceleration relative to speed
    }

    void Tick(Handler handler){
        if (canRun){
            currHeight += currSpeed;

            if (!rising){ //deaccelerate when not rising
                currSpeed -= accelerationConst;
            }

            if (currHeight <= 0){ //reset all the stuff when on ground
                currHeight = 0;
                currSpeed = 0;
                rising = true;
                takeInput = true;
            }
            if (currHeight >= maxHeight){ //stop rising when at max height
                rising = false;
            }

            handler.postDelayed(() -> { //loop as long as can run
                Tick(handler);
            }, delay);
        }
    }

    public void resume(){
        canRun = true;
    }

    public void pause(){
        canRun = false;
    }
}
