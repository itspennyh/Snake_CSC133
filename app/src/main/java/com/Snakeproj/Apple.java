package com.Snakeproj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Apple {

    // The location of the apple on the grid
    // Not in pixels
    protected Point location = new Point();

    // The range of values we can choose from
    // to spawn an apple
    protected Point mSpawnRange;
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

class Potion extends Apple {

    private Bitmap mBitmapPotion;

    public Potion(Context context, Point sr, int s) {
        super(context, sr, s);

        // Load the image to the bitmap
        mBitmapPotion = BitmapFactory.decodeResource(context.getResources(), R.drawable.potion);

        // Resize the bitmap
        mBitmapPotion = Bitmap.createScaledBitmap(mBitmapPotion, s, s, false);
    }

    @Override
    void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapPotion,
                location.x * mSize, location.y * mSize, paint);
    }
}

class Lava extends Apple {

    private Bitmap mBitmapLava;

    //storing multiple lava block locations
    private List<Point> lavaBlocks = new ArrayList<>();

    //constructor for Lava class
    public Lava (Context context, Point sr, int s) {
        super(context, sr, s);

        // Load the image to the bitmap
        mBitmapLava = BitmapFactory.decodeResource(context.getResources(), R.drawable.lava);

        // Resize the bitmap
        mBitmapLava = Bitmap.createScaledBitmap(mBitmapLava, s, s, false);
    }

    void lavaSpawns(int number, Point regAppleLocation, Point rottenAppleLocation, Point potionLocation) {
        Random random = new Random();
        lavaBlocks.clear();

        //spawns first block
        int locationx = random.nextInt(mSpawnRange.x - 2) + 1;
        int locationy = random.nextInt(mSpawnRange.y - 2) + 1;
        lavaBlocks.add(new Point(locationx, locationy));

        //decide num of lava blocks to spawn up to, currently set to 5 in SnakeGame
        int lavaNum = random.nextInt(number) + 1;

        //spawning more lava blocks around the first one
        for (int i = 1; i < lavaNum; i++) {
            //list of potential new locations
            List<Point> potentialLocations = new ArrayList<>();

            //check each direction, add to potentialLocations if valid
            //left
            checkOverlapAndSpawn(potentialLocations, locationx - 1, locationy, regAppleLocation, rottenAppleLocation, potionLocation);
            //right
            checkOverlapAndSpawn(potentialLocations, locationx + 1, locationy, regAppleLocation, rottenAppleLocation, potionLocation);
            //above
            checkOverlapAndSpawn(potentialLocations, locationx, locationy - 1, regAppleLocation, rottenAppleLocation, potionLocation);
            //below
            checkOverlapAndSpawn(potentialLocations, locationx, locationy + 1, regAppleLocation, rottenAppleLocation, potionLocation);

            //if all are valid, choose a random one
            if (!potentialLocations.isEmpty()) {
                Point newLocation = potentialLocations.get(random.nextInt(potentialLocations.size()));
                //check if location is not already filled with lava block
                lavaBlocks.add(newLocation);
            }
        }
    }

    //checks potential location before adding lava block to not spawn over regular and rotten apples
    private void checkOverlapAndSpawn(List<Point> potentialLocations, int x, int y, Point regAppleLocation, Point rottenAppleLocation, Point potionLocation) {
        Point newPoint = new Point(x, y);
        if (x >= 0 && x < mSpawnRange.x && y >= 0 && y < mSpawnRange.y
            && !newPoint.equals(regAppleLocation) && !newPoint.equals(rottenAppleLocation) && !newPoint.equals(potionLocation)
            && !lavaBlocks.contains(newPoint)) {
            potentialLocations.add(newPoint);
        }
    }

    @Override
    void draw(Canvas canvas, Paint paint) {
        for (Point block : lavaBlocks) {
            canvas.drawBitmap(mBitmapLava, block.x * mSize, block.y * mSize, paint);
        }
    }

    //list of all lava block locations
    List<Point> getLavaBlocks() {
        return lavaBlocks;
    }
}