package com.example.root.temperaturecontroll.Activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class MyCustomTemperaturePicker extends View {
    Paint paint;
    int radius = 100;


    private static final int MAX_SIZE = 250;
    public MyCustomTemperaturePicker(Context context) {
        super(context);
        getRootView().setBackgroundColor(Color.BLACK);
        init();
    }

    public MyCustomTemperaturePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCustomTemperaturePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
        paint.setARGB(255,65,105,225);
        paint.setStrokeWidth(25);
    }
    public int getRadius(){
        return radius;
    }
    public void setRadius(int radius){
        this.radius = radius;
        invalidate();
    }
    public void touch(MotionEvent event){

        int posX = (int) Math.pow(Math.abs(getWidth() / 2 - event.getX()), 2);
        int posY = (int) Math.pow(Math.abs(getHeight() / 2 - event.getY()), 2);
        if(isInBounds(posX,posY)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Log.i("Touch", "Move");
                    radius = (int) Math.sqrt(posX + posY);
                    invalidate();
                    break;
                case MotionEvent.ACTION_DOWN:
                    Log.i("Touch", "down");
                    break;
                case MotionEvent.ACTION_UP:
                    Log.i("Touch", "up");

                    break;
            }
        }
        else
            radius = MAX_SIZE;
    }
    private boolean isInBounds(float x ,float y){
        int rad = (int) Math.sqrt(x + y);
        return (rad <= MAX_SIZE);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("Width",String.valueOf(getWidth()));
        Log.i("Height",String.valueOf(getHeight()));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth()/2, getHeight()/2, MAX_SIZE, paint);

        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, paint);
    }
    public void colorize(int R,int G,int B){
        paint.setARGB(255,R,G,B);
        invalidate();
    }
}
