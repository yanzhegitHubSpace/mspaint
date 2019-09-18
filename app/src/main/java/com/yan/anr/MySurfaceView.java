package com.yan.anr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.logging.Handler;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable, GlobalHandler.HandleMsgListener {


    private SurfaceHolder mSurfaceHolder;

    private Canvas mCanvas;

    private boolean isDrawing;

    private Paint mPaint;

    private Path mPath = new Path();

    private float mLastX = 0;
    private float mLastY = 0;

    private Handler handler;

    public MySurfaceView(Context context) {
        super(context);

    }

    // 初始化
    private void init() {
        mSurfaceHolder = getHolder();//得到SurfaceHolder对象
        mSurfaceHolder.addCallback(this);//注册SurfaceHolder
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);//保持屏幕长亮
        mPaint = new Paint();


        //画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStrokeWidth(10f);
        mPaint.setColor(Color.parseColor("#FF4081"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        //路径
        mPath = new Path();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    @Override
    public SurfaceHolder getHolder() {
        return super.getHolder();
    }


    // surfaceHolder的回调接口
    @Override
    public void surfaceCreated(SurfaceHolder holder) { // 创建
        isDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { // 改变

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { // 销毁
        isDrawing = false;
    }

    // 开启线程
    @Override
    public void run() {
        while (isDrawing) {
            drawing();
        }
    }

    private void drawing() {

        try {
            // 锁定画布
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            mPaint.setColor(Color.RED);

            mCanvas.drawPath(mPath, mPaint);
        } finally {
            // 释放画布
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
//        Log.d("yanzhe", "x = " + x + "  y = " + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mLastX = x;
                mLastY = y;
                new Thread(this).start();
                mPath.moveTo(mLastX, mLastY);
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mLastX);
                float dy = Math.abs(y - mLastY);
                if (dx >= 3 || dy >= 3) {
                    // 垂直或者水平方向移动距离大于3时 绘制一条贝塞尔曲线
                    mPath.quadTo(mLastX, mLastY, (mLastX + x) / 2, (mLastY + y) / 2);
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                break;
        }

        return true;
    }

    public void clear() {
        mSurfaceHolder.addCallback(this);
        mPath = new Path();
        mCanvas.drawColor(Color.WHITE);
        isDrawing = true;
        new Thread(this).start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);


        // wrap_content 时设置的最小宽度
        if (wSpecMode == MeasureSpec.AT_MOST && hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, 300);
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(300, hSpecSize);
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(wSpecSize, 300);
        }
    }


    @Override
    public void handleMsg(Message msg) {
        clear();
//        Log.d("yanzhe", "msg == " + msg.what);
    }
}
