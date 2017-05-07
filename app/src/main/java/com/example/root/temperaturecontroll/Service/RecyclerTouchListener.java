package com.example.root.temperaturecontroll.Service;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.example.root.temperaturecontroll.Activity.NewControlerActivity;


public  class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);

    }
    private GestureDetector gestureDetector;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, OnItemClickListener clickListener) {
        mListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                /*View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && mListener != null) {
                    mListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    return true;
                }
                return false;*/
                return true;

            }

            @Override
            public void onLongPress(MotionEvent e) {

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && mListener != null && gestureDetector.onTouchEvent(e)) {
            mListener.onClick(child, rv.getChildAdapterPosition(child));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
