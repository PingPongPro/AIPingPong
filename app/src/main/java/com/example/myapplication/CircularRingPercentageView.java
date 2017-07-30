package com.example.myapplication;

/**
 * Created by 叶明林 on 2017/7/28.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class CircularRingPercentageView extends View {
    private Paint paint;
    private int circleWidth;                                             //圆直径
    private int roundBackgroundColor;                                   //背景颜色
    private int textColor;                                               //字体颜色
    private float textSize;                                              //字体大小
    private float roundWidth;                                           //环宽度
    private float progress = 0;                                         //当前着色数
    private int[] colors = {0xffff4639, 0xffCDD513, 0xff3CDF5F};
    private int radius;                                                 //圆环半径
    private RectF oval;
    private Paint mPaintText;
    private int maxColorNumber;                                         //圆等分数
    private float singlPoint = 6;
    private float lineWidth = 0f;                                       //等分线宽
    private int circleCenter;                                           //圆心
    private SweepGradient sweepGradient;
    private boolean isLine;
    private int positionX;                                              //X轴偏移量
    private int positionY;                                              //Y轴偏移量
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private long startTime=0;
    private long saveTime=0;
    private boolean isRunning=false;

    private int minite=0;
    private int second=0;
    private boolean isChanged=true;
    public int timeOfRank = 0;
    public void setCollapsingToolbarLayout(CollapsingToolbarLayout c)
    {
        this.collapsingToolbarLayout=c;
        this.collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);
        this.collapsingToolbarLayout.setExpandedTitleMarginStart(Gravity.CENTER);
    }
    private void updateTime(long currentTime)
    {
        if((this.saveTime+currentTime-this.startTime)/1000%60!=second)
        {
            this.minite=(int)(this.saveTime+currentTime-this.startTime)/60000;
            this.second=(int)(this.saveTime+currentTime-this.startTime)/1000%60;
            this.isChanged=true;
        }
    }

    public int getTimeByInt(){
        if(second == 0)
        {
            if(minite == 1)
                return 1;
            else if(minite == 3)
                return 2;
            else if(minite == 5)
                return 3;
        }
        return 1;
    }

    public String getTime()
    {
        String time="  ";
        if(minite>=10)
            time+=minite+":";
        else
            time+="0"+minite+":";
        if(second>=10)
            time+=""+second;
        else
            time+="0"+second;
        return time;
    }
    /**
     * 分割的数量
     *
     * @param maxColorNumber 数量
     */
    public void setMaxColorNumber(int maxColorNumber) {
        this.maxColorNumber = maxColorNumber;
        singlPoint = (float) 360 / (float) maxColorNumber;
        invalidate();
    }

    /**
     * 是否是线条
     *
     * @param line true 是 false否
     */
    public void setLine(boolean line) {
        isLine = line;
        invalidate();
    }

    public int getCircleWidth() {
        return circleWidth;
    }

    public CircularRingPercentageView(Context context) {
        this(context, null);
    }

    public CircularRingPercentageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CircularRingPercentageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularRing);
        maxColorNumber = mTypedArray.getInt(R.styleable.CircularRing_circleNumber, 600);
        circleWidth = mTypedArray.getDimensionPixelOffset(R.styleable.CircularRing_circleWidth, getDpValue(180));
        roundBackgroundColor = mTypedArray.getColor(R.styleable.CircularRing_roundColor, 0xffdddddd);
        textColor = mTypedArray.getColor(R.styleable.CircularRing_circleTextColor, 0xff999999);
        roundWidth = mTypedArray.getDimension(R.styleable.CircularRing_circleRoundWidth, 40);
        textSize = mTypedArray.getDimension(R.styleable.CircularRing_circleTextSize, getDpValue(8));
        colors[0] = mTypedArray.getColor(R.styleable.CircularRing_circleColor1, 0xffff4639);
        colors[1] = mTypedArray.getColor(R.styleable.CircularRing_circleColor2, 0xffcdd513);
        colors[2] = mTypedArray.getColor(R.styleable.CircularRing_circleColor3, 0xff3cdf5f);
        initView();
        mTypedArray.recycle();
    }
    /**
     * 空白出颜色背景
     *
     * @param roundBackgroundColor
     */
    public void setRoundBackgroundColor(int roundBackgroundColor) {
        this.roundBackgroundColor = roundBackgroundColor;
        paint.setColor(roundBackgroundColor);
        invalidate();
    }

    /**
     * 刻度字体颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mPaintText.setColor(textColor);
        invalidate();
    }

    /**
     * 刻度字体大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        mPaintText.setTextSize(textSize);
        invalidate();
    }

    /**
     * 渐变颜色
     *
     * @param colors
     */
    public void setColors(int[] colors) {
        if (colors.length < 2) {
            throw new IllegalArgumentException("colors length < 2");
        }
        this.colors = colors;
        sweepGradientInit();
        invalidate();
    }


    /**
     * 间隔角度大小
     *
     * @param lineWidth
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }


    private int getDpValue(int w) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 圆环宽度
     *
     * @param roundWidth 宽度
     */
    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
        if (roundWidth > circleCenter) {
            this.roundWidth = circleCenter;
        }
        radius = (int) (circleCenter - this.roundWidth / 2); // 圆环的半径
        oval.left = circleCenter - radius;
        oval.right = circleCenter + radius;
        oval.bottom = circleCenter + radius;
        oval.top = circleCenter - radius;
        paint.setStrokeWidth(this.roundWidth);
        invalidate();
    }

    /**
     * 圆环的直径
     *
     * @param circleWidth 直径
     */
    public void setCircleWidth(int circleWidth) {
        this.circleWidth = circleWidth;
        circleCenter = circleWidth / 2;

        if (roundWidth > circleCenter) {
            roundWidth = circleCenter;
        }
        setRoundWidth(roundWidth);
        sweepGradient = new SweepGradient(this.circleWidth / 2, this.circleWidth / 2, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, this.circleWidth / 2, this.circleWidth / 2);
        sweepGradient.setLocalMatrix(matrix);
    }

    /**
     * 渐变初始化
     */
    public void sweepGradientInit()
    {
        //渐变颜色
        sweepGradient = new SweepGradient(this.circleWidth / 2, this.circleWidth / 2, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90, this.circleWidth / 2, this.circleWidth / 2);
        sweepGradient.setLocalMatrix(matrix);
    }

    public void initView() {

        circleCenter = circleWidth / 2;//半径
        singlPoint = (float) 360 / (float) maxColorNumber;
        radius = (int) (circleCenter - roundWidth / 2); // 圆环的半径
        sweepGradientInit();
        mPaintText = new Paint();
        mPaintText.setColor(textColor);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(textSize);
        mPaintText.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(roundBackgroundColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(roundWidth);
        paint.setAntiAlias(true);

        // 用于定义的圆弧的形状和大小的界限
        oval = new RectF(circleCenter - radius, circleCenter - radius, circleCenter + radius, circleCenter + radius);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景渐变颜色
        paint.setShader(sweepGradient);
        canvas.drawArc(oval, -90, (float) (progress * singlPoint), false, paint);
        paint.setShader(null);

        //是否是线条模式
        if (!isLine) {
            float start = -90f;
            float p = ((float) maxColorNumber / (float) 100);
            p = (int) (progress * p);
            for (int i = 0; i < p; i++) {
                paint.setColor(roundBackgroundColor);
                canvas.drawArc(oval, start + singlPoint - lineWidth, lineWidth, false, paint); // 绘制间隔快
                start = (start + singlPoint);
            }
        }
        //绘制剩下的空白区域
        paint.setColor(roundBackgroundColor);
        canvas.drawArc(oval, -90, (float) (-(maxColorNumber - progress) * singlPoint), false, paint);
        if(isRunning)
        {
            long currenTime=System.currentTimeMillis();
            if(startTime==0)
                startTime=currenTime;
            this.progress=(float)((this.saveTime+currenTime-this.startTime)/100%maxColorNumber);
            this.updateTime(currenTime);
            if(isChanged)
            {
                timeOfRank = getTimeByInt();
                this.collapsingToolbarLayout.setTitle(getTime());
                isChanged=false;
            }
            invalidate();
        }

        //绘制文字刻度
        /*for (int i = 1; i <= 10; i++) {
            canvas.save();// 保存当前画布
            canvas.rotate(360 / 10 * i, circleCenter, circleCenter);
            canvas.drawText(i * 10 + "", circleCenter, circleCenter - radius + roundWidth / 2 + getDpValue(4) + textSize, mPaintText);
            canvas.restore();//
        }*/
    }


    OnProgressScore onProgressScore;

    public interface OnProgressScore {
        void setProgressScore(float score);

    }

    public synchronized void setProgress(final float p) {
        progress = p;
        postInvalidate();
    }

    /**
     * @param p
     */
    public synchronized void setProgress(final float p, OnProgressScore onProgressScore) {
        this.onProgressScore = onProgressScore;
        progress = p;
        postInvalidate();
    }
    public void setZero(){
        
    }
    public void start()
    {
        if(!this.isRunning)
        {
            long keep=System.currentTimeMillis();
            if(this.startTime!=0)
                this.saveTime+=keep-this.startTime;
            this.startTime=keep;
            this.isRunning=true;
            invalidate();
        }
    }
    public void pause()
    {
        if(this.isRunning)
        {
            this.isRunning=false;
            this.saveTime+=System.currentTimeMillis()-this.startTime;
            this.startTime=0;
        }
    }
    public void reset()
    {
        this.pause();
        this.startTime=0;
        this.saveTime=0;
    }
    //TODO test
    public boolean getState()
    {
        return this.isRunning;
    }
}
