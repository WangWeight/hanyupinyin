package com.ev4ngel.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/9/22.
 */
public class CrossView extends View {
    Paint mPaint=null;

    public CrossView(Context c, AttributeSet attrs){
        super(c,attrs);
        init_paint();
    }
    public CrossView(Context c){
        super(c, null);
        init_paint();
    }
    private void init_paint(){
        mPaint=new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.argb(0, 0, 0, 0));
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(5);
        canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth() / 2 - 20, getMeasuredHeight() / 2, mPaint);
        canvas.drawLine(getMeasuredWidth() / 2 + 20, getMeasuredHeight() / 2, getMeasuredWidth() , getMeasuredHeight() / 2, mPaint);
        canvas.drawLine(getMeasuredWidth()/2,0, getMeasuredWidth() / 2, getMeasuredHeight() / 2 - 20, mPaint);
        canvas.drawLine(getMeasuredWidth() / 2, getMeasuredHeight() / 2 + 20, getMeasuredWidth() / 2, getMeasuredHeight() , mPaint);

        mPaint.setAlpha(150);
        mPaint.setStrokeWidth(1);
        canvas.drawLine(getMeasuredWidth() / 2 - 10, getMeasuredHeight() / 2, getMeasuredWidth() / 2 + 10, getMeasuredHeight() / 2, mPaint);
        canvas.drawLine(getMeasuredWidth()/2,getMeasuredHeight()/2- 10,getMeasuredWidth()/2,getMeasuredHeight()/2+10,mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
