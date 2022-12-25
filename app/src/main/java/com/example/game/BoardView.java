package com.example.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class BoardView extends View {

    private Paint paint;
    private int[][] car;
    private int[][] coordinate;
    private ArrayList<int[]> move_coordinate;
    private int[] background;

    private Bitmap board;
    private Bitmap red_car;
    private Bitmap blue_car;
    private Bitmap brown_car;
    private Bitmap green_car;
    private Bitmap white_car;

    private GameCallBack callBack;

    private int start_x = 0;
    private int start_y = 0;
    private int x1 = 0;
    private int y1 = 0;
    private int x2 = 0;
    private int y2 = 0;
    private int car_index = 999;
    private int can_move = 1;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        // init Broadview
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        // init board bound
        background = new int[]{5, 6, 383, 384};
        // init picture
        board = imageScale(BitmapFactory.decodeResource(getResources(), R.drawable.board));
        red_car = imageScale(BitmapFactory.decodeResource(getResources(), R.drawable.red_car));
        blue_car = imageScale(BitmapFactory.decodeResource(getResources(), R.drawable.blue_car));
        brown_car = imageScale(BitmapFactory.decodeResource(getResources(), R.drawable.brown_car));
        green_car = imageScale(BitmapFactory.decodeResource(getResources(), R.drawable.green_car));
        white_car = imageScale(BitmapFactory.decodeResource(getResources(), R.drawable.white_car));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap car_image = null;
        // draw the board
        canvas.drawBitmap(board, 0, 0, paint);
        for (int i = 0; i < coordinate.length; i++) {
            switch (coordinate[i][0]) {
                case 1:
                    car_image = red_car;
                    break;
                case 2:
                    car_image = blue_car;
                    break;
                case 3:
                    car_image = brown_car;
                    break;
                case 4:
                    car_image = green_car;
                    break;
                case 5:
                    car_image = white_car;
                    break;
            }
            // draw every car
            canvas.drawBitmap(car_image, coordinate[i][1] + 2, coordinate[i][2] + 2, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // catch each touch event
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // store the car touched
                start_x = (int) event.getX();
                start_y = (int) event.getY();
                for (int i = 0; i < coordinate.length; i++) {
                    if (start_x > coordinate[i][1] && start_x < coordinate[i][3] && start_y > coordinate[i][2] && start_y < coordinate[i][4]) {
                        car_index = i;
                        // store last step
                        int[] move_car = {car_index, coordinate[car_index][1], coordinate[car_index][2], coordinate[car_index][3], coordinate[car_index][4]};
                        move_coordinate.add(move_car);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (car_index != 999) {
                    // coordinate after every move
                    x1 = coordinate[car_index][1] + (int) event.getX() - start_x;
                    y1 = coordinate[car_index][2] + (int) event.getY() - start_y;
                    x2 = coordinate[car_index][3] + (int) event.getX() - start_x;
                    y2 = coordinate[car_index][4] + (int) event.getY() - start_y;
                    switch (coordinate[car_index][0]) {
                        case 1: case 2: case 4:
                            // check the board bound
                            if (x1 < background[0] || x2 > background[2]) {
                                // restrict the car into block
                                setCoordinate(coordinate, car_index);
                            } else {
                                for (int i = 0; i < coordinate.length; i++) {
                                    if (i != car_index) {
                                        // check other cars for not crash
                                        if (!(coordinate[i][1] >= x2 || coordinate[i][3] <= x1 || coordinate[i][2] >= coordinate[car_index][4] || coordinate[i][4] <= coordinate[car_index][2])) {
                                            // restrict the car into block
                                            setCoordinate(coordinate, car_index);
                                            can_move = 0;
                                        }
                                    }
                                }
                                // renew coordinate
                                if (can_move == 1) {
                                    coordinate[car_index][1] = x1;
                                    coordinate[car_index][3] = x2;
                                    start_x = (int) event.getX();
                                } else {
                                    can_move = 1;
                                }
                            }
                            break;
                        case 3: case 5:
                            // check the board bound
                            if (y1 < background[1] || y2 > background[3]) {
                                // restrict the car into block
                                setCoordinate(coordinate, car_index);
                            } else {
                                for (int i = 0; i < coordinate.length; i++) {
                                    if (i != car_index) {
                                        // check other cars for not crash
                                        if (!(coordinate[i][2] >= y2 || coordinate[i][4] <= y1 || coordinate[i][1] >= coordinate[car_index][3] || coordinate[i][3] <= coordinate[car_index][1])) {
                                            // restrict the car into block
                                            setCoordinate(coordinate, car_index);
                                            can_move = 0;
                                        }
                                    }
                                }
                                // renew coordinate
                                if (can_move == 1) {
                                    coordinate[car_index][2] = y1;
                                    coordinate[car_index][4] = y2;
                                    start_y = (int) event.getY();
                                } else {
                                    can_move = 1;
                                }
                            }
                    }
                    // redraw the car
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (car_index != 999) {
                    // restrict the car into block
                    setCoordinate(coordinate, car_index);
                    // redraw the car
                    invalidate();
                    int[] move_car = move_coordinate.get(move_coordinate.size()-1);
                    if (coordinate[car_index][1] == move_car[1] && coordinate[car_index][2] == move_car[2]) {
                        move_coordinate.remove(move_coordinate.size()-1);
                    }
                    updateStep();
                    checkGameOver();
                    car_index = 999;
                }
                break;
        }
        return true;
    }

    // function for undo the step
    public void undo() {
        try {
            int[] move_car = move_coordinate.get(move_coordinate.size() - 1);
            coordinate[move_car[0]][1] = move_car[1];
            coordinate[move_car[0]][2] = move_car[2];
            coordinate[move_car[0]][3] = move_car[3];
            coordinate[move_car[0]][4] = move_car[4];
            move_coordinate.remove(move_coordinate.size() - 1);
        } catch (Exception e) {

        }
        updateStep();
        invalidate();
    }

    // function for restart the game
    public void restart() {
        setDifficulty(car);
        move_coordinate = new ArrayList();
        updateStep();
        invalidate();
    }

    public void updateStep() {
        callBack.UpdateStep(move_coordinate.size());
    }

    public void checkGameOver() {
        if ((coordinate[0][1] - 5) / 63 == 4 && (coordinate[0][2] - 6) / 63 == 2) {
            callBack.GameOver();
            coordinate[0][1] += 63;
            coordinate[0][3] += 63;
        }
    }

    public void setCallBack(GameCallBack callBack) {
        this.callBack = callBack;
    }

    // set three difficulty of game
    public void setDifficulty(int[][] cars) {
        car = cars;
        coordinate = getCoordinate(car);
        move_coordinate = new ArrayList();
        invalidate();
    }

    // resize the image
    private Bitmap imageScale(Bitmap bitmap) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float dst_w = (float) (src_w / 5 * 1.5);
        float dst_h = (float) (src_h / 5 * 1.5);
        float scale_w = dst_w / src_w;
        float scale_h = dst_h / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        return Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
    }

    // get the coordinate of each car
    private int[][] getCoordinate(int[][] car) {
        int[][] coordinate = new int[car.length][5];
        for (int i = 0; i < car.length; i++) {
            coordinate[i][0] = car[i][0];
            coordinate[i][1] = car[i][2] * 63 + 5;
            coordinate[i][2] = car[i][1] * 63 + 6;
            switch (car[i][0]) {
                case 1: case 2:
                    coordinate[i][3] = coordinate[i][1] + 2 * 63;
                    coordinate[i][4] = coordinate[i][2] + 1 * 63;
                    break;
                case 3:
                    coordinate[i][3] = coordinate[i][1] + 1 * 63;
                    coordinate[i][4] = coordinate[i][2] + 2 * 63;
                    break;
                case 4:
                    coordinate[i][3] = coordinate[i][1] + 3 * 63;
                    coordinate[i][4] = coordinate[i][2] + 1 * 63;
                    break;
                case 5:
                    coordinate[i][3] = coordinate[i][1] + 1 * 63;
                    coordinate[i][4] = coordinate[i][2] + 3 * 63;
                    break;
            }
        }
        return coordinate;
    }

    // set the coordinate of each car
    private void setCoordinate(int[][] coordinate, int car_index) {
        switch (coordinate[car_index][0]) {
            case 1: case 2: case 4:
                if ((coordinate[car_index][1] - 5) % 63 > 31) {
                    coordinate[car_index][1] += 63 - (coordinate[car_index][1] - 5) % 63;
                    coordinate[car_index][3] += 63 - (coordinate[car_index][3] - 5) % 63;
                } else {
                    coordinate[car_index][1] -= (coordinate[car_index][1] - 5) % 63;
                    coordinate[car_index][3] -= (coordinate[car_index][3] - 5) % 63;
                }
                invalidate();
                break;
            case 3: case 5:
                if ((coordinate[car_index][2] - 6) % 63 > 31) {
                    coordinate[car_index][2] += 63 - (coordinate[car_index][2] - 6) % 63;
                    coordinate[car_index][4] += 63 - (coordinate[car_index][4] - 6) % 63;
                } else {
                    coordinate[car_index][2] -= (coordinate[car_index][2] - 6) % 63;
                    coordinate[car_index][4] -= (coordinate[car_index][4] - 6) % 63;
                }
                invalidate();
                break;
        }
    }
}
