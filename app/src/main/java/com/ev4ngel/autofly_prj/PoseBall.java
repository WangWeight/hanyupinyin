package com.ev4ngel.autofly_prj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/9/22.
 */
public class PoseBall extends View {
    float mRoll;
    float mPitch;
    Paint mPaint;
    public PoseBall(Context c){
        super(c,null);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
    }
    public PoseBall(Context c, AttributeSet as){
        super(c,as);
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float radius_x2=getMeasuredWidth();
        //绘制背景
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(0, 0, radius_x2, radius_x2, 0, 360, false, mPaint);
        canvas.drawArc(radius_x2 / 6, radius_x2 / 6, radius_x2 * 5 / 6, radius_x2 * 5 / 6, 0, 360, false, mPaint);
        canvas.drawArc(radius_x2 / 3, radius_x2 / 3, radius_x2 * 2 / 3, radius_x2 * 2 / 3, 0, 360, false, mPaint);
        canvas.drawLine(0, radius_x2 / 2, radius_x2, radius_x2 / 2, mPaint);
        canvas.drawLine(radius_x2 / 2, 0, radius_x2 / 2, radius_x2, mPaint);
        mPaint.setAlpha(120);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(radius_x2 / 2, radius_x2 / 2, radius_x2 / 2, mPaint);
        //绘制状态
        mPaint.setColor(Color.argb(100, 0, 0, 255));
        float ang=(float)(Math.asin(mPitch/90)*180/3.14159);
        canvas.drawArc(0, 0, radius_x2, radius_x2, ( mRoll-ang), 180 + 2 * ang, false, mPaint);
    }

    public void updateStatus(float roll,float pitch){
        mPitch=pitch;
        mRoll=roll;
        invalidate();
    }

}
