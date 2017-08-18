package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;

public class PlayBack extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "MainActivity";
    private SurfaceView mSurfaceview;
    private Button mBtnStartStop;
    private Button mBtnPlay;
    private boolean mStartedFlg = false;//是否正在录像
    private boolean mIsPlay = false;//是否正在播放录像
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    private ImageView mImageView;
    private Camera camera;
    private MediaPlayer mediaPlayer;
    private String path;
    private TextView textView;
    private int text = 0;

    private Drawable VIDEO_PAUSE;
    private Drawable VIDEO_START;

    private SeekBar seekBar;
    private LinearLayout videoPlay_BigScreen;
    private RelativeLayout wholeLayout;
    private int CONTROLLERHEIGHT = 90;
    private boolean isShowController = true;

    private android.os.Handler handler = new android.os.Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            text++;
            textView.setText(text+"");
            handler.postDelayed(this,1000);
        }
    };

    private void startAnimHide(){
        ObjectAnimator.ofFloat(videoPlay_BigScreen, View.TRANSLATION_Y,
                0, DensityUtil.dip2px(this, this.CONTROLLERHEIGHT))
                .setDuration(200).start();
    }
    private void startAnimShow(){
        ObjectAnimator.ofFloat(videoPlay_BigScreen, View.TRANSLATION_Y,
                DensityUtil.dip2px(this, this.CONTROLLERHEIGHT), 0)
                .setDuration(200).start();
    }

    private void myListener(){
        mBtnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsPlay) {
                    if (mediaPlayer != null) {
                        mIsPlay = false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
                if (!mStartedFlg) {
                    //handler.postDelayed(runnable,1000);
                    mImageView.setVisibility(View.GONE);
                    if (mRecorder == null) {
                        mRecorder = new MediaRecorder();
                    }

                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    if (camera != null) {
                        camera.setDisplayOrientation(0);
                        camera.unlock();
                        mRecorder.setCamera(camera);
                    }

                    try {
                        // 这两项需要放在setOutputFormat之前
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                        // Set output file format
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                        // 这两项需要放在setOutputFormat之后
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

                        mRecorder.setVideoFrameRate(30);
                        mRecorder.setOrientationHint(0);

                        mRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
                        //设置记录会话的最大持续时间（毫秒）
                        mRecorder.setMaxDuration(30 * 1000);
                        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                        path = getSDPath();
                        if (path != null) {
                            File dir = new File(path + "/recordtest");
                            if (!dir.exists()) {
                                dir.mkdir();
                            }
                            path = dir + "/" + getDate() + ".mp4";
                            mRecorder.setOutputFile(path);
                            mRecorder.prepare();
                            mRecorder.start();
                            mStartedFlg = true;
                            mBtnStartStop.setBackground(VIDEO_PAUSE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //stop
                    if (mStartedFlg) {
                        try {
                            //handler.removeCallbacks(runnable);
                            mRecorder.stop();
                            mRecorder.reset();
                            mRecorder.release();
                            mRecorder = null;
                            mBtnStartStop.setBackground(VIDEO_START);
                            if (camera != null) {
                                camera.release();
                                camera = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mStartedFlg = false;
                }
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsPlay = true;
                mImageView.setVisibility(View.GONE);
                mBtnPlay.setBackground(VIDEO_PAUSE);
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                mediaPlayer.reset();
                Uri uri = Uri.parse(path);
                mediaPlayer = MediaPlayer.create(PlayBack.this, uri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(mSurfaceHolder);
                try{
                    mediaPlayer.prepare();
                }catch (Exception e){
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        });
        wholeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShowController){
                    startAnimHide();
                }
                else{
                    startAnimShow();
                }
                isShowController = !isShowController;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_back);

        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        mImageView = (ImageView) findViewById(R.id.imageview);
        mBtnStartStop = (Button) findViewById(R.id.btnStartStop);
        mBtnPlay = (Button) findViewById(R.id.btnPlayVideo);
        //textView = (TextView)findViewById(R.id.text);
        seekBar = (SeekBar)findViewById(R.id.seekbar_BigScreen);
        wholeLayout = (RelativeLayout)findViewById(R.id.wholeLayout);
        videoPlay_BigScreen = (LinearLayout)findViewById(R.id.videoPlay_BigScreen);

        VIDEO_START = getResources().getDrawable(R.drawable.video_start);
        VIDEO_PAUSE = getResources().getDrawable(R.drawable.video_pause);
        myListener();
        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (!mStartedFlg) {
            mImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);           // 获取年份
        int month = ca.get(Calendar.MONTH);         // 获取月份
        int day = ca.get(Calendar.DATE);            // 获取日
        int minute = ca.get(Calendar.MINUTE);       // 分
        int hour = ca.get(Calendar.HOUR);           // 小时
        int second = ca.get(Calendar.SECOND);       // 秒

        String date = "" + year + (month + 1) + day + hour + minute + second;
        Log.d(TAG, "date:" + date);

        return date;
    }

    /**
     * 获取SD path
     *
     * @return
     */
    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
            return sdDir.toString();
        }

        return null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        handler.removeCallbacks(runnable);
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
            Log.d(TAG, "surfaceDestroyed release mRecorder");
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}