package com.example.druge.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2018/9/21/021.
 */

public class FingerDrawView extends android.support.v7.widget.AppCompatImageView{
    private Context mContext;
    private Canvas mPathCanvas;
    private Paint mPaint;
    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;
    private Path mPath;
    public FingerDrawView(Context context) {
        this(context,null);
    }

    public FingerDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FingerDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight= getMeasuredHeight();
        mWidth=getMeasuredWidth();
        init();
    }

    public Bitmap getmBitmap(){
        return  mBitmap;
    }

    //初始数据的方法
    private void init() {
        mBitmap= Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);

        mPathCanvas=new Canvas(mBitmap);
        mPath=new Path();
        //设置画笔属性
        setPaintStyle();
    }
//设置画笔属性
    private void setPaintStyle() {
        mPaint=new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.STROKE);
    }
    float lastX;
    float lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY =  event.getY();
                mPath.moveTo(lastX,lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx=lastX-event.getX();
                float dy=lastY-event.getY();
                float slop=Math.max(Math.abs(dx),Math.abs(dy));
                if (slop>3){
                    //执行操作
                    mPath.lineTo(event.getX(),event.getY());
                    lastX=dx;
                    lastY=dy;
                    mPathCanvas.drawPath(mPath,mPaint);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:

                break;

        }

        return  true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制线条

    //绘制图到自定义view上
    canvas.drawBitmap(mBitmap,0,0,mPaint);
        canvas.save();

    }

//重置画板
    public void resetCanvas(){
        mPath=new Path();
        mPathCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        /*mPath=null;
        mPath=new Path();
        mBitmap=null;
        mBitmap= Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        mPathCanvas=new Canvas(mBitmap);*/
        invalidate();
    }

    public String saveFingerBitmap(String iconname){
        File file = new File(Environment.getExternalStorageDirectory()+"/"+iconname);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
        }
}
