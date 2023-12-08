package com.Snakeproj;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.io.IOException;

class SnakeGame extends SurfaceView implements Runnable{

    //private enum GameState { BEGINNING, IN_GAME, GAME_OVER }
    //private GameState gameState = GameState.BEGINNING;

    // should try to merge it under stt manager, as if playing, can only play on given difficulty

    //private enum Difficulty { EASY, MEDIUM, HARD }
    //private Difficulty currentDifficulty = Difficulty.MEDIUM; // Default difficulty

    private Rect easyButtonRect, mediumButtonRect, hardButtonRect;
    private Rect newGameButtonRect;
    private Rect pauseButtonRect;
    //private boolean mGameOver = false;
    private Rect quitButtonRect;


    // Objects for the game loop/thread
    private Thread mThread = null;
    // Control pausing between updates
    private long mNextFrameTime;

    //private volatile boolean mPlaying = false;
    //private volatile boolean mPaused = true;

    // How many points does the player have
    private int mScore;

    public enum gmSttMngr {
        INSTANCE;
        public enum stt {
            START, PAUSED, STOPPED
        }

        // ingame variables could go here
        volatile private stt currStt = stt.STOPPED;
        // prev state variable may be necessary

        // setter n getters
        public synchronized void setStt(stt newStt) {
            this.currStt = newStt;
        }

        public synchronized stt getCurrStt() {
            return currStt;
        }
    }

    // for playing sound effects
    private SoundPool mSP;
    private int mEat_ID = -1;
    private int mCrashID = -1;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40; // 20?
    private int mNumBlocksHigh;

    // Objects for drawing
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Paint textPaint, buttonPaint;

    // A snake ssss
    private Snake mSnake;
    // And an apple
    private Apple mApple;



    // This is the constructor method that gets called
    // from SnakeActivity
    public SnakeGame(Context context, Point size) {
        super(context);

        //gameState = GameState.BEGINNING;
        textPaint = new Paint();
        buttonPaint = new Paint();

        // Work out how many pixels each block is
        int blockSize = size.x / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        mNumBlocksHigh = size.y / blockSize;

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the sounds in memory
            descriptor = assetManager.openFd("get_apple.ogg");
            mEat_ID = mSP.load(descriptor, 0);

            descriptor = assetManager.openFd("snake_death.ogg");
            mCrashID = mSP.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }

        // Initialize the drawing objects
        mSurfaceHolder = getHolder();
        mPaint = new Paint();

        // Call the constructors of our two game objects
        mApple = new Apple(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        mSnake = new Snake(context,
                new Point(NUM_BLOCKS_WIDE,
                        mNumBlocksHigh),
                blockSize);

        //newGame(); why is this here?

    }

    // Called to start a new game
    public void newGame() {

        //mGameOver = false;
        //mPaused = false;

        // reset the snake
        mSnake.reset(NUM_BLOCKS_WIDE, mNumBlocksHigh);

        // Get the apple ready for dinner
        mApple.spawn();

        // Reset the mScore
        mScore = 0;

        // Setup mNextFrameTime so an update can triggered
        mNextFrameTime = System.currentTimeMillis();
    }


    // Handles the game loop
    @Override
    public void run() {
        while (gmSttMngr.INSTANCE.getCurrStt() != gmSttMngr.stt.STOPPED) {

                // THIS LINE HERE IS WHERE IT BROKE
                /*

                while (mPlaying) { ...
                if(!mPaused) { ...

                 */

                // snippet logic above was reduced to the one line based on logic that !mPaused == mPlaying
                // THIS IS NOT TRUE, and is trying to render snake into non-space

                // Update 10 times a second
            if (gmSttMngr.INSTANCE.getCurrStt() == gmSttMngr.stt.START)
                if (updateRequired()) {
                    update();
                }

            draw();
        }
    }


    // Check to see if it is time for an update
    public boolean updateRequired() {

        // Run at 5 frames per second
        final long TARGET_FPS; // was = 10
        // There are 1000 milliseconds in a second
        final long MILLIS_PER_SECOND = 1000;
        // go to line 46 to fix this up, as curr
        // is using extra vars to handle
        switch(currentDifficulty) {
            case EASY:
                TARGET_FPS = 5; // Slower for easy
                break;
            case MEDIUM:
                TARGET_FPS = 10; // Normal speed
                break;
            case HARD:
                TARGET_FPS = 15; // Faster for hard
                break;
            default:
                TARGET_FPS = 10;
        }

        // Are we due to update the frame
        if(mNextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            mNextFrameTime =System.currentTimeMillis()
                    + MILLIS_PER_SECOND / TARGET_FPS;

            // Return true so that the update and draw
            // methods are executed
            return true;
        }

        return false;
    }


    // Update all the game objects
    public void update() {

        // Move the snake
        mSnake.move();

        // Did the head of the snake eat the apple?
        if(mSnake.checkDinner(mApple.getLocation())){
            // This reminds me of Edge of Tomorrow.
            // One day the apple will be ready!
            mApple.spawn();

            // Add to  mScore
            mScore = mScore + 1;

            // Play a sound
            mSP.play(mEat_ID, 1, 1, 0, 0, 1);
        }

        // Did the snake die?
        if (mSnake.detectDeath()) {
            // Pause the game ready to start again
            mSP.play(mCrashID, 1, 1, 0, 0, 1);

            gmSttMngr.INSTANCE.setStt(gmSttMngr.stt.PAUSED);
        }

    }


    // Do all the drawing
    public void draw() {

        // Get a lock on the mCanvas
        if (mSurfaceHolder.getSurface().isValid()) {
            mCanvas = mSurfaceHolder.lockCanvas();
            // needs to be integrated into singleton design, line 46
            if (gameState == GameState.BEGINNING) {
                // Clear the canvas
                mCanvas.drawColor(Color.argb(255, 77, 77, 77));

                // Draw the title
                textPaint.setTextSize(100);
                textPaint.setTextAlign(Paint.Align.CENTER);
                mCanvas.drawText("Select Difficulty", mCanvas.getWidth() / 2, 300, textPaint);

                // Draw difficulty buttons
                int buttonWidth = mCanvas.getWidth() - 200; // Width of the button
                int buttonHeight = 100; // Height of the button
                int buttonMarginVertical = 50; // Margin between buttons
                int buttonLeft = 100; // Left position for the button

                // Calculate the top position of each button
                int easyButtonTop = 450;
                int mediumButtonTop = easyButtonTop + buttonHeight + buttonMarginVertical;
                int hardButtonTop = mediumButtonTop + buttonHeight + buttonMarginVertical;

                // Define the buttons
                easyButtonRect = new Rect(buttonLeft, easyButtonTop, buttonLeft + buttonWidth, easyButtonTop + buttonHeight);
                mediumButtonRect = new Rect(buttonLeft, mediumButtonTop, buttonLeft + buttonWidth, mediumButtonTop + buttonHeight);
                hardButtonRect = new Rect(buttonLeft, hardButtonTop, buttonLeft + buttonWidth, hardButtonTop + buttonHeight);

                // Set up button paint
                buttonPaint.setColor(Color.GRAY);
                buttonPaint.setStyle(Paint.Style.FILL);

                // Draw the buttons and text for each difficulty level
                mCanvas.drawRect(easyButtonRect, buttonPaint);
                drawCenteredText(mCanvas, textPaint, "Easy", easyButtonRect);

                mCanvas.drawRect(mediumButtonRect, buttonPaint);
                drawCenteredText(mCanvas, textPaint, "Medium", mediumButtonRect);

                mCanvas.drawRect(hardButtonRect, buttonPaint);
                drawCenteredText(mCanvas, textPaint, "Hard", hardButtonRect);

                // Draw difficulty buttons
                mCanvas.drawRect(easyButtonRect, buttonPaint);
                mCanvas.drawText("Easy", easyButtonRect.centerX(), easyButtonRect.centerY() + 30, textPaint);

                mCanvas.drawRect(mediumButtonRect, buttonPaint);
                mCanvas.drawText("Medium", mediumButtonRect.centerX(), mediumButtonRect.centerY() + 30, textPaint);

                mCanvas.drawRect(hardButtonRect, buttonPaint);
                mCanvas.drawText("Hard", hardButtonRect.centerX(), hardButtonRect.centerY() + 30, textPaint);

            } else {

                // Fill the screen with a color
                mCanvas.drawColor(Color.argb(255, 77, 77, 77));


            if(gmSttMngr.INSTANCE.getCurrStt() == gmSttMngr.stt.START) {
                // Set the size and color of the mPaint for the text
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(150);

                // Draw the score
                mCanvas.drawText("" + mScore, 20, 120, mPaint);

                // Draw the apple and the snake
                mApple.draw(mCanvas, mPaint);
                mSnake.draw(mCanvas, mPaint);

                // Draw some text while pausedG
                // WAS }else {, looks like got moved up?
                if(mPaused && !mGameOver){

                    // Draw the pause screen
                    mPaint.setTextSize(60);
                    mPaint.setColor(Color.WHITE);
                    mCanvas.drawText("Game Paused. Press Pause to Resume", 100, 350, mPaint);

                }

                // Initialize button paint
                if (buttonPaint == null) {
                    buttonPaint = new Paint();
                    buttonPaint.setColor(Color.BLACK);
                    buttonPaint.setAlpha(75); // Semi-transparent
                }

                // Define button positions and sizes
                int buttonWidth = 200;
                int buttonHeight = 80;
                int spacing = 20;

                int totalButtonWidth = buttonWidth * 2 + spacing;
                int startX = (mCanvas.getWidth() - totalButtonWidth) / 2;
                int startY = mCanvas.getHeight() - buttonHeight - 50; // Position from bottom

                if (newGameButtonRect == null) {
                    newGameButtonRect = new Rect(
                            startX,
                            startY,
                            startX + buttonWidth,
                            startY + buttonHeight
                    );
                }

                if (pauseButtonRect == null) {
                    pauseButtonRect = new Rect(
                            startX + buttonWidth + spacing,
                            startY,
                            startX + totalButtonWidth,
                            startY + buttonHeight
                    );
                }

                // Draw the Quit button (optional)
                if (quitButtonRect == null) {
                    quitButtonRect = new Rect(
                            startX + buttonWidth + spacing,
                            startY,
                            startX + totalButtonWidth,
                            startY + buttonHeight
                    );
                }

                // Draw buttons
                if (!mGameOver) {
                    mCanvas.drawRect(pauseButtonRect, buttonPaint);
                    mPaint.setTextSize(70);
                    mPaint.setColor(Color.WHITE);
                    mCanvas.drawText("Pause", 1120, 950, mPaint);
                }

                if (mGameOver) {

                    mCanvas.drawRect(newGameButtonRect, buttonPaint);

                    mPaint.setTextSize(80);
                    mPaint.setColor(Color.WHITE);
                    mCanvas.drawText("NG", 950, 950, mPaint);

                    mCanvas.drawRect(quitButtonRect, buttonPaint);

                    mPaint.setTextSize(80);
                    mPaint.setColor(Color.WHITE);
                    mCanvas.drawText("Quit", 1150, 950, mPaint);

                    // Set the size and color of the mPaint for the text
                    mPaint.setColor(Color.argb(255, 255, 255, 255));
                    mPaint.setTextSize(60);

                    // Draw the message
                    // We will give this an international upgrade soon
                    mCanvas.drawText("Tap NG to Play a New Game!", 100, 450, mPaint);
                /*mCanvas.drawText(getResources().
                                getString(R.string.tap_to_play),
                        200, 700, mPaint); */

                    // Draw Game Over text
                    mPaint.setTextSize(100);
                    mCanvas.drawText("Game Over!", 100, 350, mPaint);

                    // Draw Quit Text
                    mPaint.setTextSize(60);
                    mCanvas.drawText("Tap Quit to Exit", 100, 550, mPaint);

                }

            }

            // Unlock the mCanvas and reveal the graphics for this frame
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }
        /*
        FROM:



        @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (gmSttMngr.INSTANCE.getCurrStt() != gmSttMngr.stt.PLAYING) {
                    gmSttMngr.INSTANCE.setStt(gmSttMngr.stt.PLAYING);
                    newGame();

                    // Don't want to process snake direction for this tap
                    return true;
                }

                // Let the Snake class handle the input
                mSnake.switchHeading(motionEvent);
                break;

            default:
                break;

        }
        return true;
    }




         */
    private void drawCenteredText(Canvas canvas, Paint paint, String text, Rect rect) {
        paint.setTextAlign(Paint.Align.CENTER);
        int xPos = rect.centerX();
        int yPos = (int) (rect.centerY() - ((paint.descent() + paint.ascent()) / 2));
        canvas.drawText(text, xPos, yPos, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();

        if (gameState == GameState.BEGINNING) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (easyButtonRect.contains(x, y)) {
                    currentDifficulty = Difficulty.EASY;
                    gameState = GameState.IN_GAME;
                    newGame();
                } else if (mediumButtonRect.contains(x, y)) {
                    currentDifficulty = Difficulty.MEDIUM;
                    gameState = GameState.IN_GAME;
                    newGame();
                } else if (hardButtonRect.contains(x, y)) {
                    currentDifficulty = Difficulty.HARD;
                    gameState = GameState.IN_GAME;
                    newGame();
                }
            }
            return true;
        } else if (gameState == GameState.IN_GAME) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (mGameOver) {
                        // Game over screen is active
                        if (newGameButtonRect.contains(x, y)) {
                            mGameOver = false;
                            mPaused = false;
                            newGame();
                            return true;
                        }
                        if (quitButtonRect != null && quitButtonRect.contains(x, y)) {
                            System.exit(0);  // Quit the game
                            return true;
                        }
                    } else {
                        // Game is not over - handle normal game touch events
                        if (newGameButtonRect.contains(x, y)) {
                            mPaused = false;
                            newGame();
                            return true;
                        } else if (pauseButtonRect.contains(x, y)) {
                            mPaused = !mPaused;
                            return true;
                        } else if (!mPaused) {
                            mSnake.switchHeading(motionEvent);
                        }
                    }
                    break;
            }

        }

        return true;
    }

    // Stop the thread
    public void stop() {
        gmSttMngr.INSTANCE.setStt(gmSttMngr.stt.STOPPED);
        try {
            mThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }


    // Start the thread
    public void resume() {
        gmSttMngr.INSTANCE.setStt(gmSttMngr.stt.PAUSED);
        mThread = new Thread(this);
        mThread.start();
    }
}
