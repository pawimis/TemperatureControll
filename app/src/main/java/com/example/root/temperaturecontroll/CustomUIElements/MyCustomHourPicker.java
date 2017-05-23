package com.example.root.temperaturecontroll.CustomUIElements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;



public class MyCustomHourPicker extends View{
    Paint paint;
    Paint textPaint;
    int spot = 0;
    ArrayList<Paint> paintArrayList;
    public MyCustomHourPicker(Context context) {
        super(context);
        init();
    }
    public MyCustomHourPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCustomHourPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        paint = new Paint();
        paint.setTextSize(30);
        paint.setColor(Color.BLUE);
        textPaint = new Paint();
        textPaint.setTextSize(30);
        textPaint.setColor(Color.WHITE);
        paintArrayList = new ArrayList<>();
        int i = 0;
        while(i<24){
            paintArrayList.add(new Paint(paint));
            i++;
        }
    }
    public void touch(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        if(x > getWidth()-100  && y > 50 && y <getHeight()-50) {
            float place = y / (getHeight() - 100);
            spot = (int) (24 * place);
            invalidate();
        }
    }
    public int getSpot(){
        return spot;
    }
    public void setSpot(int spot){
        this.spot = spot;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight()-100;
        int currStartPoint = 50;
        height = height / 24;
        int i = 1;
        for(Paint currPaint : paintArrayList ) {
            canvas.drawRect(getWidth() - 40, currStartPoint, getWidth() - 20, currStartPoint + height, currPaint);
            if(i==spot){
                Paint tempPaint = new Paint(textPaint);
                tempPaint.setTextSize(90);
                canvas.drawText(String.valueOf(i), getWidth() - 100, currStartPoint + height / 2, tempPaint);
            }else {
                canvas.drawText(String.valueOf(i), getWidth() - 90, currStartPoint + height / 2, textPaint);
            }
            currStartPoint += height;
            i++;
        }
    }

    public void colorize(int red, int green, int blue) {
        if (spot > 0 && spot < 25) {
            Log.i("spot",String.valueOf(spot));
            paintArrayList.get(spot-1).setARGB(255, red, green, blue);
            invalidate();
        }
    }

}
