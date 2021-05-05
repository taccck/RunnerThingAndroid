package com.example.finalproject;

import android.graphics.Bitmap;

import android.os.Handler;

public class Animation {

    boolean canRun = true;
    Bitmap[] keyFrames;
    public Bitmap currFrame; //image to draw
    long delay; //time between key frames

    public Animation(Bitmap[] _keyFrames, long _delay) {
        keyFrames = _keyFrames;
        currFrame = keyFrames[0];
        delay = _delay;
    }

    int currFrameIndex;

    public void UpdateFrame(Handler handler) { //go to the next frame
        if (canRun) {
            currFrameIndex++;
            if (currFrameIndex >= keyFrames.length) {
                currFrameIndex = 0;
            }
            currFrame = keyFrames[currFrameIndex];
            handler.postDelayed(() -> {
                UpdateFrame(handler);
            }, delay); //calls itself until canRun is false
        }
    }
}
