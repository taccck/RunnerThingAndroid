package com.example.finalproject;

import android.graphics.Bitmap;
import android.graphics.Rect;

import android.os.Handler;
import android.util.Log;

public class Obstacle extends HorizontalImageScroll {
    float width;
    float obstacleWidth;
    float height; //size of the screen
    float gapSize; //size of the gap the player can jump through
    float gapLocation; //where the gap is located in height

    public Rect upper; //obstacle can be split into two rects
    public Rect lower;
    GameView gameView;
    Bitmap upperImage;

    public Obstacle(float _width, float _height, float _gapSize, float _speed, float groundHeight, GameView _gameView, Bitmap _lowerImage, Bitmap _upperImage) {
        super(_speed, 20L, Bitmap.createScaledBitmap(_lowerImage, (int)(32 * (_height / _lowerImage.getHeight())), (int)_height, false));
        upperImage = Bitmap.createScaledBitmap(_upperImage, (int)(32 * (_height / _upperImage.getHeight())), (int)_height, false);
        width = _width;
        height = _height;
        gapSize = _gapSize;
        obstacleWidth = 32;

        float min = (gapSize / 2);
        float max = height - (gapSize / 2) - (groundHeight / 2);
        gapLocation = (float) (min + (Math.random() * (max - min))); //https://stackoverflow.com/questions/40431966/what-is-the-best-way-to-generate-a-random-float-value-included-into-a-specified

        upper = new Rect();
        upper.top = 0;
        upper.bottom = (int)(gapLocation - gapSize);
        lower = new Rect();
        lower.top = (int)(gapLocation + gapSize);
        lower.bottom = (int)height;

        currX = width; //goes from right to left
        gameView = _gameView;
    }

    public void Move(Handler handler) {
        if (canRun) { //move the rects to the left while can run
            currX -= speed;

            upper.right = (int) (currX + obstacleWidth);
            lower.right = (int) (currX + obstacleWidth);
            upper.left = (int) currX;
            lower.left =  (int) currX;

            Colliding(upper, gameView.playerRect);
            Colliding(lower, gameView.playerRect);

            handler.postDelayed(() -> {
                        Move(handler);
                    }, delay);
        }
    }

    static boolean ended; //opens more than one game over screen otherwise
    public boolean Colliding(Rect a, Rect b) {
        if (!ended) {
            if (a.left < b.right && a.right > b.left) { //overlapping on x-axis
                if (a.top < b.bottom && a.bottom > b.top) { //overlapping on y-axis
                    gameView.GameOver();
                    ended = true;
                    return true;
                }
            }
        }
        return false;
    }
}
