package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.R;

import java.io.File;
import java.util.Calendar;

public class PlayBack extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "PlayBack";
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
    private File dir, folderPath;
    private TextView textView;
    private int text = 0;
    private String DateNow;

    private Drawable RECORDING;
    private Drawable RECORDINGSTOP;

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
                            DateNow = getDate();
                            dir = new File(path + "/ballGame/Video");
                            if (!dir.exists()) {
                                dir.mkdir();
                            }
                            folderPath = new File(dir + "/" + DateNow);
                            if(!folderPath.exists()){
                                folderPath.mkdir();
                            }
                            path = folderPath + "/" + DateNow + ".mp4";
                            mRecorder.setOutputFile(path);
                            mRecorder.prepare();
                            mRecorder.start();
                            mStartedFlg = true;
                            mBtnStartStop.setBackground(RECORDINGSTOP);
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
                            mBtnStartStop.setBackground(RECORDING);
                            if (camera != null) {
                                camera.release();
                                camera = null;
                            }
                            final EditText ETmp = new EditText(PlayBack.this);
                            ETmp.setHint(DateNow);
                            ETmp.setHintTextColor(Color.GRAY);
                            new AlertDialog.Builder(PlayBack.this)
                                    .setTitle("请输入保存文件夹")
                                    .setView(ETmp)
                                    .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(ETmp.getText() != null){
                                                FileCheck FCTmp = new FileCheck();
                                                if(FCTmp.isValidFileName(ETmp.getText().toString())){
                                                    String newPath = dir + "/" + ETmp.getText().toString();
                                                    FCTmp.moveFile(path, newPath);
                                                    FCTmp.deleteDirectory(folderPath.getPath());
                                                }
                                            }
                                        }
                                    })
                                    .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FileCheck FCTmp = new FileCheck();
                                            FCTmp.deleteDirectory(folderPath.getPath());
                                        }
                                    })
                                    .show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mStartedFlg = false;
                }
            }
        });

//        mBtnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mIsPlay = true;
//                mImageView.setVisibility(View.GONE);
//                mBtnPlay.setBackground(VIDEO_PAUSE);
//                if (mediaPlayer == null) {
//                    mediaPlayer = new MediaPlayer();
//                }
//                mediaPlayer.reset();
//                Uri uri = Uri.parse(path);
//                mediaPlayer = MediaPlayer.create(PlayBack.this, uri);
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                mediaPlayer.setDisplay(mSurfaceHolder);
//                try{
//                    mediaPlayer.prepare();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                mediaPlayer.start();
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_back);

        mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        mImageView = (ImageView) findViewById(R.id.imageview);
        mBtnStartStop = (Button) findViewById(R.id.btnStartStop);
        //mBtnPlay = (Button) findViewById(R.id.btnPlayVideo);
        //textView = (TextView)findViewById(R.id.text);

        RECORDING = getResources().getDrawable(R.drawable.recording);
        RECORDINGSTOP = getResources().getDrawable(R.drawable.recording_stop);

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

    @Override
    protected void onPause(){
        super.onPause();
    }

    /**
     * 获取系统时间
     *
     * @return
     */
    public static String getDate() {
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);           // 获取年份
        int month = ca.get(Calendar.MONTH) + 1;     // 获取月份
        int day = ca.get(Calendar.DATE);            // 获取日
        int minute = ca.get(Calendar.MINUTE);       // 分
        int hour = ca.get(Calendar.HOUR_OF_DAY);   // 小时
        int second = ca.get(Calendar.SECOND);       // 秒

        String date = "" + year + "-";
        if(month < 10)
            date = date + "0";
        date = date + month + "-";
        if(day < 10)
            date = date + "0";
        date = date + day + "-";
        if(hour < 10)
            date = date + "0";
        date = date + hour + "-";
        if(minute < 10)
            date = date + "0";
        date = date + minute + "-";
        if(second < 10)
            date = date + "0";
        date = date + second;
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