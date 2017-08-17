package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ProgressBar;

import java.text.DecimalFormat;

public class ArcProgress extends ProgressBar {
    public static final int STYLE_TICK = 1;
    public static final int STYLE_ARC = 0;
    private final int DEFAULT_LINEHEIGHT = dp2px(15);
    private final int DEFAULT_mTickWidth = dp2px(2);
    private final int DEFAULT_mRadius = dp2px(63);//
    private final int DEFAULT_mUnmProgressColor = Color.parseColor("#E7E7E7");
    private final int DEFAULT_mProgressColor = Color.YELLOW;
    private final int DEFAULT_OFFSETDEGREE = 90;
    private final int DEFAULT_DENSITY = 4;
    private final int MIN_DENSITY = 2;
    private final int MAX_DENSITY = 8;
    private int mStylePogress = STYLE_ARC;
    private boolean mBgShow;
    private float mRadius;
    private int mArcbgColor;
    private int mBoardWidth;
    private int mDegree = DEFAULT_OFFSETDEGREE;
    private RectF mArcRectf;
    private Paint mLinePaint;
    private Paint mArcPaint;
    private int mUnmProgressColor;
    private int mProgressColor;
    private int mTickWidth;
    private int mTickDensity;
    private Bitmap  mCenterBitmap;
    private Canvas mCenterCanvas;
    private OnCenterDraw mOnCenter;
    private float viewCenterX;
    private float viewCenterY;
    private SweepGradient sweepGradient;
    private Rect targetRect_Top = null;
    private Rect targetRect_Middle = null;
    private Rect targetRect_Bottom = null;
    private String textTop="0";
    private String textMiddle="0";
    private String textBottom="0";
    private Paint textPaint;
    private int task=100;
    private int Mode=0;
    public static final int SPORTTIME=0;
    public static final int HITCOUNTER=1;
    private int precision=0;
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getPrecision() {
        return precision;
    }
    public void setTask(int value)
    {
        if(value>0)
            this.task=value;
    }
    public void updateMiddle(float value)
    {
        if(value<0)
            return ;
        String parse=".";
        int thisPrecision=(value>=10000?2:precision);
        for(int i=0;i<thisPrecision;i++)
            parse+="0";
        DecimalFormat decimalFormat=new DecimalFormat(parse);
        float progressValue=(value>task?task:value);
        String postfix=(value>10000?"w":"");
        value=(value>10000?value/10000:value);
        this.textMiddle=(thisPrecision==0?(int)value:decimalFormat.format(value))+postfix;
        this.setProgress((int)(progressValue*100/task));
        invalidate();
    }
    public void setMode(int value)
    {
        this.Mode=value;
        switch (Mode)
        {
            case SPORTTIME:
                this.textTop="运动时间";
                this.textBottom="h";
                break;
            case HITCOUNTER:
                this.textTop="击球总数";
                this.textBottom="个";
                break;
        }
    }
    public ArcProgress(Context context) {
        this(context, null);
    }
    public ArcProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ArcProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.ArcProgress);
        Thread.setDefaultUncaughtExceptionHandler(new myException());
        mBoardWidth = attributes.getDimensionPixelOffset(R.styleable.ArcProgress_borderWidth, DEFAULT_LINEHEIGHT);
        mUnmProgressColor = attributes.getColor(R.styleable.ArcProgress_unprogresColor, DEFAULT_mUnmProgressColor);
        mProgressColor = attributes.getColor(R.styleable.ArcProgress_progressColor, DEFAULT_mProgressColor);
        mTickWidth = attributes.getDimensionPixelOffset(R.styleable.ArcProgress_tickWidth,DEFAULT_mTickWidth);
        mTickDensity = attributes.getInt(R.styleable.ArcProgress_tickDensity,DEFAULT_DENSITY);
        mRadius = attributes.getDimensionPixelOffset(R.styleable.ArcProgress_radius,DEFAULT_mRadius);
        mArcbgColor = attributes.getColor(R.styleable.ArcProgress_arcbgColor,DEFAULT_mUnmProgressColor);
        mTickDensity = Math.max(Math.min(mTickDensity,MAX_DENSITY),MIN_DENSITY);
        mBgShow = attributes.getBoolean(R.styleable.ArcProgress_bgShow,false);
        mDegree = attributes.getInt(R.styleable.ArcProgress_degree,DEFAULT_OFFSETDEGREE);
        mStylePogress = attributes.getInt(R.styleable.ArcProgress_progressStyle,STYLE_ARC);
        boolean capRount = attributes.getBoolean(R.styleable.ArcProgress_arcCapRound,true);
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(mArcbgColor);
        if(capRount)
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeWidth(mBoardWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(mTickWidth);
        textPaint =new Paint();
        textPaint.setColor(Color.parseColor("#777777"));
        textPaint.setTextAlign(Paint.Align.CENTER);
    }
    public void sweepGradientInit()
    {
        int colors[]=new int[]{Color.parseColor("#F2B75E"),Color.parseColor("#F06B4A")};
        //渐变颜色
        sweepGradient = new SweepGradient(viewCenterX, viewCenterY, colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-235, viewCenterX, viewCenterY);
        sweepGradient.setLocalMatrix(matrix);
    }
    public void setOnCenterDraw(OnCenterDraw mOnCenter) {
        this.mOnCenter = mOnCenter;
    }
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode!=MeasureSpec.EXACTLY){
            int widthSize = (int) (mRadius*2+mBoardWidth*2);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize,MeasureSpec.EXACTLY);
        }
        if(heightMode != MeasureSpec.EXACTLY){
            int heightSize = (int) (mRadius*2+mBoardWidth*2);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        float roate = getProgress() * 1.0f / getMax();
        float x = mArcRectf.right / 2 + mBoardWidth / 2;
        float y = mArcRectf.right / 2 + mBoardWidth / 2;
        if (mOnCenter != null) {
            if(mCenterCanvas == null){
                mCenterBitmap = Bitmap.createBitmap((int)mRadius*2,(int)mRadius*2, Bitmap.Config.ARGB_8888);
                mCenterCanvas = new Canvas(mCenterBitmap);
            }
            mCenterCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mOnCenter.draw(mCenterCanvas, mArcRectf, x, y,mBoardWidth,getProgress());
            canvas.drawBitmap(mCenterBitmap, 0, 0, null);
        }
        int angle = mDegree/2;
        int count = (360 - mDegree )/mTickDensity;
        int target = (int) (roate * count);
        if(mStylePogress == STYLE_ARC){
            float targetmDegree = (360-mDegree)*roate;
            //绘制未完成部分
            mArcPaint.setColor(mUnmProgressColor);
            canvas.drawArc(mArcRectf,90+angle+targetmDegree,360-mDegree-targetmDegree,false,mArcPaint);
            //绘制完成部分
            //mArcPaint.setColor(mProgressColor);
            mArcPaint.setShader(this.sweepGradient);
            canvas.drawArc(mArcRectf,90+angle,targetmDegree,false,mArcPaint);
            mArcPaint.setShader(null);

        }else{
            if(mBgShow)
                canvas.drawArc(mArcRectf,90+angle,360-mDegree,false,mArcPaint);
            canvas.rotate(180+angle,x,y);
            for(int i = 0 ; i<count;i++){
                if(i<target){
                    mLinePaint.setColor(mProgressColor);
                }else{
                    mLinePaint.setColor(mUnmProgressColor);
                }
                canvas.drawLine(x,mBoardWidth+mBoardWidth/2,x,mBoardWidth-mBoardWidth/2,mLinePaint);
                canvas.rotate(mTickDensity,x,y);
            }
        }
        textPaint.setColor(Color.parseColor("#777777"));
        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(getSpValue(14));
        //paint.setColor(Color.CYAN);
        //canvas.drawRect(targetRect, paint);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (targetRect_Top.bottom + targetRect_Top.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(textTop, targetRect_Top.centerX(), baseline, textPaint);

        //Paint paint2=new Paint();
        //paint2.setColor(Color.CYAN);
        //canvas.drawRect(targetRect_Top, paint2);
        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(getSpValue(32));//mRadius/2
        fontMetrics = textPaint.getFontMetricsInt();
        baseline = (targetRect_Middle.bottom + targetRect_Middle.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(textMiddle, targetRect_Middle.centerX(), baseline, textPaint);

        textPaint.setStrokeWidth(3);
        textPaint.setTextSize(getSpValue(12));
        fontMetrics = textPaint.getFontMetricsInt();
        baseline = (targetRect_Bottom.bottom + targetRect_Bottom.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(textBottom, targetRect_Bottom.centerX(), baseline, textPaint);

        textPaint.setStrokeWidth(getDpValue(2.5f));
        textPaint.setColor(Color.parseColor("#F4A861"));
        canvas.drawLine(viewCenterX-mRadius/2, viewCenterY+mRadius/3,viewCenterX+mRadius/2,viewCenterY+mRadius/3,textPaint);
        canvas.restore();
    }
    private float getDpValue(float w) {
        return  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getContext().getResources().getDisplayMetrics());
    }
    private int getSpValue(int w) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, w, getContext().getResources().getDisplayMetrics());
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(this.viewCenterY==0||this.viewCenterX==0)
        {
            this.viewCenterY=getHeight()/2+getDpValue(10);
            this.viewCenterX=getWidth()/2;
            targetRect_Top = new Rect((int)(viewCenterX-mRadius/2), (int)(viewCenterY-6*mRadius/12), (int)(viewCenterX+mRadius/2), (int)(viewCenterY-7*mRadius/12));
            targetRect_Middle = new Rect((int)(viewCenterX-mRadius), (int)(viewCenterY-getDpValue(1)), (int)(viewCenterX+mRadius), (int)(viewCenterY+getDpValue(1)));
            targetRect_Bottom = new Rect((int)(viewCenterX-mRadius/2), (int)(viewCenterY+5*mRadius/9), (int)(viewCenterX+mRadius/2), (int)(viewCenterY+5*mRadius/9));
        }
        if(this.sweepGradient == null)
            sweepGradientInit();
        mArcRectf = new RectF(this.viewCenterX-mRadius,
                this.viewCenterY-mRadius,
                this.viewCenterX+mRadius,
                this.viewCenterY+mRadius);
        Log.e("DEMO","right == "+mArcRectf.right+"   mRadius == "+mRadius*2);
    }
    /**
     * dp 2 px
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
    public interface  OnCenterDraw {
        /**
         *
         * @param canvas
         * @param rectF  圆弧的Rect
         * @param x      圆弧的中心x
         * @param y      圆弧的中心y
         * @param storkeWidth   圆弧的边框宽度
         * @param progress      当前进度
         */
        public  void draw(Canvas canvas, RectF rectF, float x, float y, float storkeWidth, int progress);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mCenterBitmap!=null){
            mCenterBitmap.recycle();
            mCenterBitmap = null;
        }

    }
}
