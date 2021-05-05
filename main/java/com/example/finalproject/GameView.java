package com.example.finalproject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Handler;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView
        implements Runnable,
        SurfaceHolder.Callback {

    SurfaceHolder holder;
    Jump jump;
    GameActivity gameActivity;
    public GameView(Context context, Jump _jump) {
        super(context);

        gameActivity = (GameActivity) context;
        jump = _jump;
        holder = getHolder();
        holder.addCallback(this);

        CreatePaints();
    }

    Paint lvlTxtPaint;
    Paint pointTxtPaint;
    Paint obstaclePaint;
    void CreatePaints(){
        lvlTxtPaint = new Paint();
        lvlTxtPaint.setColor(getResources().getColor(R.color.black));
        lvlTxtPaint.setTextSize(125);
        lvlTxtPaint.setTextAlign(Paint.Align.CENTER);

        pointTxtPaint = new Paint();
        pointTxtPaint.setColor(getResources().getColor(R.color.black));
        pointTxtPaint.setTextSize(75);
        pointTxtPaint.setTextAlign(Paint.Align.CENTER);

        obstaclePaint = new Paint();
        obstaclePaint.setColor(getResources().getColor(R.color.design_default_color_primary));
    }

    float groundHeight;
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder _holder) {
        holder = _holder;
        jump.Create(getHeight() * .4f, getHeight() * .02f);
        groundHeight = (getHeight() * .6f);

        CreateLevels();
    }

    boolean lvlCreated;
    Level[] levels;
    void CreateLevels(){
        Obstacle[] lvl1Obstacles = CreateObstacles(3, getHeight() / 6f, getWidth() / 80f);
        Obstacle[] lvl2Obstacles = CreateObstacles(5, getHeight() / 8f, getWidth() / 60f);
        Obstacle[] lvl3Obstacles = CreateObstacles(10, getHeight() / 10f, getWidth() / 40f);
        Obstacle.ended = false;

        levels = new Level[]{
                new Level("Level 1", lvl1Obstacles),
                new Level("Level 2", lvl2Obstacles),
                new Level("Level 3", lvl3Obstacles)
        };

        lvlCreated = true;
    }

    Obstacle[] CreateObstacles(int amount, float gapSize, float speed){
        Obstacle[] currObstacles = new Obstacle[amount];
        for (int i = 0; i < amount; i++){
            currObstacles[i] = new Obstacle(getWidth(), getHeight(), gapSize, speed, groundHeight, this, IdToBitmap(R.drawable.pipe_lower), IdToBitmap(R.drawable.pipe_upper));
        }
        return currObstacles;
    }

    boolean canRun;
    float playerY;
    Rect playerRect;
    @Override
    public void run() {
        Canvas canvas;

        while (canRun) {
            if (!holder.getSurface().isValid() || !lvlCreated) {
                continue;
            }

            InstantiatePlayer();
            BackgroundLogic();

            canvas = holder.lockCanvas();
            canvas.drawBitmap(background[0].image, background[0].currX,0,null); //set background color
            canvas.drawBitmap(background[1].image, background[1].currX,0,null); //set background color

            playerY = groundHeight - jump.currHeight;
            canvas.drawBitmap(player.currFrame, playerX, playerY, null);

            playerRect.top = (int)playerY;
            playerRect.bottom = (int)playerY + player.currFrame.getHeight();

            if (obstacleHandler != null) { //resume has not been called yet

                ObstacleLogic();

                canvas.drawBitmap(currObstacle.upperImage, currObstacle.upper.left, currObstacle.upper.bottom - currObstacle.upperImage.getHeight() , null);
                canvas.drawBitmap(currObstacle.image, currObstacle.lower.left, currObstacle.lower.top, null);

                canvas.drawText(levels[currLevel].name, getWidth() / 2, getHeight() / 7, lvlTxtPaint);
                canvas.drawText("" + points, getWidth() / 2, getHeight() / 5, pointTxtPaint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    HorizontalImageScroll[] background;
    void BackgroundLogic(){
        if (background == null){
            Bitmap backBitmap =  Bitmap.createScaledBitmap(IdToBitmap(R.drawable.background), getWidth(), getHeight(), false);
            background = new HorizontalImageScroll[]{
                    new HorizontalImageScroll(getWidth() / 120f, 20L, backBitmap),
                    new HorizontalImageScroll(getWidth() / 120f, 20L, backBitmap),
            };
            background[1].currX = getWidth();

            backgroundHandler1.post(() -> {
                background[0].Move(backgroundHandler1);
            });
            backgroundHandler2.post(() -> {
                background[1].Move(backgroundHandler2);
            });
        }

        for(HorizontalImageScroll h: background){
            if(h.currX <= -getWidth()){
                h.currX = getWidth();
            }
        }
    }

    int currLevel = 0;
    int currObstacleIndex = 0;
    Obstacle currObstacle;
    int points = 0;
    void ObstacleLogic() {
        if (currLevel != -1){
            if (currObstacle != null) { //if curr obstacle is drawn
                if (currObstacle.upper.right <= 0) { //next obstacle
                    currObstacle.canRun = false;
                    currObstacleIndex++;
                    points++;
                    MainActivity.PlayPoint();
                    currObstacle = null;
                }
            }

            if (currObstacleIndex >= levels[currLevel].obstacles.length) { //next level
                currLevel++;
                currObstacleIndex = 0;
                if (currLevel >= levels.length) { //the end
                    gameActivity.Win();
                    currLevel = -1; //so it won't crash
                }
            }

            if (currObstacle == null) { //spawn next obstacle
                currObstacle = levels[currLevel].obstacles[currObstacleIndex];
                currObstacle.Move(obstacleHandler);
            }
        }
    }

    float playerX;
    float playerScale;
    Animation player;
    void InstantiatePlayer() {
        if (player == null) {
            playerScale = 64; //original resolution of player images
            playerScale *= getHeight() / 750f;

            Bitmap[] playerRun = new Bitmap[]{IdToBitmap(R.drawable.player_run_frame1), IdToBitmap(R.drawable.player_run_frame2), IdToBitmap(R.drawable.player_run_frame3), IdToBitmap(R.drawable.player_run_frame4)};
            for (int i = 0; i < playerRun.length; i++) {
                playerRun[i] = Bitmap.createScaledBitmap(playerRun[i], (int) playerScale, (int) playerScale, false);
            }
            player = new Animation(playerRun, 125L); //8 frames per second
            player.UpdateFrame(playerHandler);

            playerRect = new Rect();
            playerX = (getWidth() * .25f) - (player.currFrame.getWidth() / 2f); //players placement on screen in x axis
            playerRect.left = (int)playerX;
            playerRect.right = (int)playerX + player.currFrame.getWidth();
        }
    }

    Bitmap IdToBitmap(int id) {
        return BitmapFactory.decodeResource(getResources(), id);
    }

    Handler gameOverHandler;
    public void GameOver(){
        //play death anim
        player.currFrameIndex = 0;
        Bitmap[] deathParticles = new Bitmap[]{IdToBitmap(R.drawable.death_particles1), IdToBitmap(R.drawable.death_particles2), IdToBitmap(R.drawable.death_particles3), IdToBitmap(R.drawable.death_particles4), IdToBitmap(R.drawable.death_particles5), IdToBitmap(R.drawable.death_particles6), IdToBitmap(R.drawable.death_particles7)};
        for (int i = 0; i < deathParticles.length; i++) {
            deathParticles[i] = Bitmap.createScaledBitmap(deathParticles[i], (int) playerScale, (int) playerScale, false);
        }
        player.keyFrames = deathParticles;
        player.delay = 84L;

        background[0].canRun = false;
        background[1].canRun = false;
        jump.canRun = false;
        currObstacle.canRun = false;

        MainActivity.PlayGameOver();
        gameOverHandler = new Handler();
        gameOverHandler.postDelayed(() -> {
            gameActivity.GameOver();
        }, 450L);
    }

    public void pause() {
        canRun = false;
        while (true) {
            try {
                thread.join(); //close thread
                player.canRun = false;
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            break;
        }
        thread = null;
    }

    Thread thread = null;
    Handler playerHandler;
    Handler obstacleHandler;
    Handler backgroundHandler1;
    Handler backgroundHandler2;
    public void resume() {
        thread = new Thread(this); //open thread
        thread.start();
        if (playerHandler == null) {
            playerHandler = new Handler();
        }
        if (obstacleHandler == null) {
            obstacleHandler = new Handler();
        }
        if (backgroundHandler1 == null){
            backgroundHandler1 = new Handler();
            backgroundHandler2 = new Handler();
        }

        if (player != null) {
            player.canRun = true;
        }
        canRun = true;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
    }
}
