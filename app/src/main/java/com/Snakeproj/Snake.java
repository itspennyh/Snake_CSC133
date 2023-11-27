package com.Snakeproj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

class Snake {

    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    private static final int INITIAL_SEGMENT = 1;
    private static final int OFF_SCREEN = -10;

    private ArrayList<Point> segmentLocations;
    private int mSegmentSize;
    private Point mMoveRange;
    private int halfWayPoint;

    private Heading heading = Heading.RIGHT;

    private Bitmap[] headBitmaps = new Bitmap[4];
    private Bitmap mBitmapBody;

    Snake(Context context, Point moveRange, int segmentSize) {
        segmentLocations = new ArrayList<>();
        mMoveRange = moveRange;
        mSegmentSize = segmentSize;
        initializeBitmaps(context);
        halfWayPoint = moveRange.x * segmentSize / 2;
    }

    private void initializeBitmaps(Context context) {
        Bitmap originalHead = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        headBitmaps[Heading.RIGHT.ordinal()] = Bitmap.createScaledBitmap(originalHead, mSegmentSize, mSegmentSize, false);
        headBitmaps[Heading.LEFT.ordinal()] = createFlippedBitmap(headBitmaps[Heading.RIGHT.ordinal()]);
        headBitmaps[Heading.UP.ordinal()] = rotateBitmap(headBitmaps[Heading.RIGHT.ordinal()], -90);
        headBitmaps[Heading.DOWN.ordinal()] = rotateBitmap(headBitmaps[Heading.RIGHT.ordinal()], 180);

        mBitmapBody = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.body), mSegmentSize, mSegmentSize, false);
    }

    private Bitmap createFlippedBitmap(Bitmap original) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(original, 0, 0, mSegmentSize, mSegmentSize, matrix, true);
    }

    private Bitmap rotateBitmap(Bitmap original, float degrees) {
        Matrix matrix = new Matrix();
        matrix.preRotate(degrees);
        return Bitmap.createBitmap(original, 0, 0, mSegmentSize, mSegmentSize, matrix, true);
    }

    void reset(int screenWidth, int screenHeight) {
        heading = Heading.RIGHT;
        segmentLocations.clear();
        segmentLocations.add(new Point(screenWidth / 2, screenHeight / 2));
    }

    void move() {
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            Point current = segmentLocations.get(i);
            Point next = segmentLocations.get(i - 1);
            current.set(next.x, next.y);
        }

        Point head = segmentLocations.get(0);
        moveHead(head);
    }

    private void moveHead(Point head) {
        switch (heading) {
            case UP:
                head.y--;
                break;
            case RIGHT:
                head.x++;
                break;
            case DOWN:
                head.y++;
                break;
            case LEFT:
                head.x--;
                break;
        }
    }

    boolean detectDeath() {
        return hitScreenEdges() || hitItself();
    }

    private boolean hitScreenEdges() {
        Point head = segmentLocations.get(0);
        return head.x == -1 || head.x > mMoveRange.x || head.y == -1 || head.y > mMoveRange.y;
    }

    private boolean hitItself() {
        Point head = segmentLocations.get(0);
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            if (head.equals(segmentLocations.get(i))) {
                return true;
            }
        }
        return false;
    }

    boolean checkDinner(Point foodLocation) {
        if (segmentLocations.get(0).equals(foodLocation)) {
            segmentLocations.add(new Point(OFF_SCREEN, OFF_SCREEN));
            return true;
        }
        return false;
    }

    void draw(Canvas canvas, Paint paint) {
        if (!segmentLocations.isEmpty()) {
            drawHead(canvas);
            drawBody(canvas);
        }
    }

    private void drawHead(Canvas canvas) {
        Bitmap headBitmap = headBitmaps[heading.ordinal()];
        Point headLocation = segmentLocations.get(0);
        canvas.drawBitmap(headBitmap, headLocation.x * mSegmentSize, headLocation.y * mSegmentSize, null);
    }

    private void drawBody(Canvas canvas) {
        for (int i = INITIAL_SEGMENT; i < segmentLocations.size(); i++) {
            Point bodyLocation = segmentLocations.get(i);
            canvas.drawBitmap(mBitmapBody, bodyLocation.x * mSegmentSize, bodyLocation.y * mSegmentSize, null);
        }
    }

    void switchHeading(MotionEvent motionEvent) {
        if (motionEvent.getX() >= halfWayPoint) {
            rotateRight();
        } else {
            rotateLeft();
        }
    }

    private void rotateRight() {
        switch (heading) {
            case UP:
                heading = Heading.RIGHT;
                break;
            case RIGHT:
                heading = Heading.DOWN;
                break;
            case DOWN:
                heading = Heading.LEFT;
                break;
            case LEFT:
                heading = Heading.UP;
                break;
        }
    }

    private void rotateLeft() {
        switch (heading) {
            case UP:
                heading = Heading.LEFT;
                break;
            case LEFT:
                heading = Heading.DOWN;
                break;
            case DOWN:
                heading = Heading.RIGHT;
                break;
            case RIGHT:
                heading = Heading.UP;
                break;
        }
    }
}
