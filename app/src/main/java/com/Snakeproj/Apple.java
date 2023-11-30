package com.Snakeproj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;


import java.util.Random;

class Apple {

    // The location of the apple on the grid
    // Not in pixels
    protected Point location = new Point();

    // The range of values we can choose from
    // to spawn an apple
    private Point mSpawnRange;
    protected int mSize;

    // An image to represent the apple
    private Bitmap mBitmapApple;

    /// Set up the apple in the constructor
    Apple(Context context, Point sr, int s){

        // Make a note of the passed in spawn range
        mSpawnRange = sr;
        // Make a note of the size of an apple
        mSize = s;
        // Hide the apple off-screen until the game starts
        location.x = -10;

        // Load the image to the bitmap
        mBitmapApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.apple);

        // Resize the bitmap
        mBitmapApple = Bitmap.createScaledBitmap(mBitmapApple, s, s, false);
    }

    // This is called every time an apple is eaten
    void spawn(){
        // Choose two random values and place the apple
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    // Let SnakeGame know where the apple is
    // SnakeGame can share this with the snake
    Point getLocation(){
        return location;
    }

    // Draw the apple
    void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(mBitmapApple,
                location.x * mSize, location.y * mSize, paint);

    }

}

class RottenApple extends Apple {

    private Bitmap mBitmapRottenApple;

    //constructor for RottenApple class
    public RottenApple(Context context, Point sr, int s) {
        super(context, sr, s);

        // Load the image to the bitmap
        mBitmapRottenApple = BitmapFactory.decodeResource(context.getResources(), R.drawable.rottenapple);

        // Resize the bitmap
        mBitmapRottenApple = Bitmap.createScaledBitmap(mBitmapRottenApple, s, s, false);
    }

    @Override
    void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapRottenApple,
                location.x * mSize, location.y * mSize, paint);
    }
}

class Lava extends Apple {

    private Bitmap mBitmapLava;

    //constructor for Lava class
    public Lava (Context context, Point sr, int s) {
        super(context, sr, s);

        // Load the image to the bitmap
        mBitmapLava = BitmapFactory.decodeResource(context.getResources(), R.drawable.lava);

        // Resize the bitmap
        mBitmapLava = Bitmap.createScaledBitmap(mBitmapLava, s, s, false);
    }

    @Override
    void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapLava,
                location.x * mSize, location.y * mSize, paint);
    }
}