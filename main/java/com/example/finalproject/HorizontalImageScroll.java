package com.example.finalproject;

import android.graphics.Bitmap;
import android.os.Handler;


public class HorizontalImageScroll {

    boolean canRun = true;
    float speed;
    float currX;
    long delay;
    public Bitmap image;

    public HorizontalImageScroll(float _speed, long _delay, Bitmap _image){
        speed = _speed;
        delay = _delay;
        image = _image;
    }

    public void Move(Handler handler) {
        if (canRun) { //move  to the left while can run
            currX -= speed;

            handler.postDelayed(() -> {
                Move(handler);
            }, delay);
        }
    }
}
