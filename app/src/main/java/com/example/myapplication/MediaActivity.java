package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MediaActivity extends AppCompatActivity {

    private final static String TAG = MediaActivity.class.getSimpleName();

    private RelativeLayout rl;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private SeekBar seekBar;
    private ImageView ivPauseOrStart;
    private LinearLayout llController;
    private TextView tvTime;
    private TextView tvTimeMax;

    private MediaPlayer player;

    private final int CONTROLLERHEIGHT=80;

    public final static String FirstFolder = "Idami";    //一级目录名称
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory()
            + File.separator + FirstFolder + File.separator;

    private String vedioPath = null;
    private List<MediaEntity> mList;

    private final static int SINGLE_MEDIA = 0;
    private final static int MULTI_MEDIA = 1;
    private int currentPlayType = SINGLE_MEDIA;

    private int currentMedia = 0;

    private int screenWidth;
    private int screenHeight;

    private int videoWidth;
    private int videoHeight;

    private boolean isPlaying = false;

    private Handler mHandler = new Handler();
    private final static int UPDATA_TIME_CODE = 1;
    private Handler mProHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPDATA_TIME_CODE){
                tvTime.setText( getPlayPro(player.getCurrentPosition()) );
                tvTimeMax.setText( getPlayPro(player.getDuration()-player.getCurrentPosition()) );
            }
        }
    };

    private Timer timer;

    private boolean isShowController = true;
    private int llcontrollerTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new myException());
        setContentView(R.layout.activity_media);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_media);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        vedioPath = getIntent().getStringExtra("path");
        if (vedioPath == null){
            currentPlayType = MULTI_MEDIA;
            mList = (List<MediaEntity>) getIntent().getSerializableExtra("list");
        }

        //获得手机分辨率
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        rl = (RelativeLayout) findViewById(R.id.relativeLayout1);
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        ivPauseOrStart = (ImageView) findViewById(R.id.iv_pause_or_start);
        llController = (LinearLayout) findViewById(R.id.ll_control);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvTimeMax = (TextView) findViewById(R.id.tv_time_max);

        surfaceHolder = surfaceView.getHolder();

        //当surface被显示时保持屏幕打开状态
        surfaceHolder.setKeepScreenOn(true);

//        surfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//
//                if (surfaceView.getWidth() > 0) {
//                    surfaceWidth = surfaceView.getWidth();
//                }
//
//            }
//        });


        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                player = new MediaPlayer();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
//                }
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDisplay(surfaceHolder);

                player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        Log.e(TAG, "width-->>" + width + ",height-->>" + height);

//                        if ((float)width / height >= (float)screenWidth / screenHeight) {
//                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
//                            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//                            layoutParams.height = (int) ((float) screenWidth * height/ width);
//                            surfaceView.setLayoutParams(layoutParams);
//                        }else{
//                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
//                            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
//                            layoutParams.width = (int) ((float) screenHeight * width/ height);
//                            surfaceView.setLayoutParams(layoutParams);
//                        }
                    }
                });

                if (vedioPath == null && (mList == null || mList.size() <= 0)){
                    Log.e(TAG, "无视频源");
                    return;
                }

                if (vedioPath != null) {
                    startPlayer(vedioPath);

                }else if (mList != null && mList.size() > 0){
                    mHandler.post(mediaThread);
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//                Log.e(TAG, "width--->>" + width + ",height--->>" + height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//

        ivPauseOrStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    player.pause();
                    ivPauseOrStart.setImageResource(android.R.drawable.ic_media_play);
                    isPlaying = false;
                    pausePro();
                } else {
                    player.start();
                    ivPauseOrStart.setImageResource(android.R.drawable.ic_media_pause);
                    isPlaying = true;
                    startPro();
                }
            }
        });

        //显示或隐藏进度条
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowController = !isShowController;
                if (isShowController) {
//                    llController.setVisibility(View.VISIBLE);
//                    TranslateAnimation tAnim = new TranslateAnimation(0, 0, screenHeight, llcontrollerTop);
//                    tAnim.setDuration(500);
//                    tAnim.setFillAfter(true);
//                    llController.startAnimation(tAnim);
                    startAnimShow();
                }else{
                    startAnimHide();
//                    int[] position = new int[2];
//                    llController.getLocationInWindow(position);
//
//                    Rect rect = new Rect();
//                    llController.getGlobalVisibleRect(rect);
//                    Log.e(TAG, "top=" + rect.top + ",bottom=" + rect.bottom);
//                    llcontrollerTop = position[1];
//                    Log.e(TAG, "-----------" + llcontrollerTop);
//                    TranslateAnimation tAnim = new TranslateAnimation(0, 0, llcontrollerTop, screenHeight);
//                    tAnim.setDuration(500);
//                    tAnim.setFillAfter(true);
//                    llController.startAnimation(tAnim);
//                    llController.setVisibility(View.INVISIBLE);
                }
            }
        });

        Button button_vedio=(Button) findViewById(R.id.startrecording);
        Button button_choose=(Button)findViewById(R.id.data);
        Button button_demo=(Button) findViewById(R.id.exit);

        Drawable startRecording = getResources().getDrawable(R.drawable.start);
        startRecording.setBounds(60, 0, 160, 100);
        button_vedio.setCompoundDrawables(startRecording, null, null, null);

        Drawable choose = getResources().getDrawable(R.drawable.start);
        choose.setBounds(60, 0, 160, 100);
        button_choose.setCompoundDrawables(choose, null, null, null);

        Drawable demo = getResources().getDrawable(R.drawable.start);
        choose.setBounds(60, 0, 160, 100);
        button_choose.setCompoundDrawables(choose, null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * 连播
     */
    private Runnable mediaThread = new Runnable() {
        @Override
        public void run() {

            startPlayer(mList.get(currentMedia).filePath);
        }
    };

    /**
     * update progress
     */
    private void startPro(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int currentPosition = player.getCurrentPosition();
                //update vedio progress
                seekBar.setProgress(currentPosition);
                //update vedio time
                mProHandler.removeMessages(UPDATA_TIME_CODE);
                mProHandler.sendEmptyMessage(UPDATA_TIME_CODE);
            }
        }, 0, 1000);

    }

    /**
     * pause update progress
     */
    private void pausePro(){
        timer.cancel();
    }

    private void startPlayer(String path){
        try {
            player.reset();
            player.setDataSource(path);

            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoWidth = player.getVideoWidth();
                    videoHeight = player.getVideoHeight();
                    Log.e(TAG, "videoWidth=" + videoWidth + ",videoHeight=" + videoHeight);

                    //setSurficeViewSize();
                    Log.e(TAG, "-------------");

                    player.start();
                    //seekbar
                    isPlaying = true;
                    seekBar.setMax(player.getDuration());
                    //max time
                    tvTimeMax.setText(getPlayPro(player.getDuration()));
                    //update
                    startPro();
                }


            });

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    isPlaying = false;

                    pausePro();

                    if (currentPlayType == MULTI_MEDIA) {

                        currentMedia++;

                        if (mList != null && currentMedia >= mList.size()) {
                            Log.e(TAG, "播放完最后一个视频");
                            mHandler.removeCallbacks(mediaThread);
                            MediaActivity.this.finish();
                            return;
                        }

                        mHandler.removeCallbacks(mediaThread);
                        mHandler.post(mediaThread);
                        return;
                    }

                    MediaActivity.this.finish();

                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    player.seekTo(seekBar.getProgress());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPlayPro(long pro) {
        DateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(pro));
    }

    private void startAnimHide(){
        ObjectAnimator.ofFloat(llController, View.TRANSLATION_Y,
                0, DensityUtil.dip2px(MediaActivity.this, this.CONTROLLERHEIGHT))
                .setDuration(200).start();
    }
    private void startAnimShow(){
        ObjectAnimator.ofFloat(llController, View.TRANSLATION_Y,
                DensityUtil.dip2px(MediaActivity.this, this.CONTROLLERHEIGHT), 0)
                .setDuration(200).start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
//            duration = player.getDuration();
            full(true);
        }


        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            duration = player.getDuration();
            full(false);
        }

        int temp = screenWidth;
        screenWidth = screenHeight;
        screenHeight = temp;

        //setSurficeViewSize();

//        Log.e(TAG, "duration---->>" + duration);
        super.onConfigurationChanged(newConfig);
    }

    private void setSurficeViewSize(){

        if ((float) videoWidth / videoHeight >= (float) screenWidth / screenHeight) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = (int) ((float) screenWidth * videoHeight / videoWidth);
            surfaceView.setLayoutParams(layoutParams);
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            layoutParams.width = (int) ((float) screenHeight * videoWidth / videoHeight);
            surfaceView.setLayoutParams(layoutParams);
        }
    }

    /**
     * 控制状态栏 隐藏（全屏）/显示
     * @param enable
     */
    private void full(boolean enable) {
        if (enable) {

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {

            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private enum PlayState{
        start,
        Pause
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        pausePro();
        player.release();
    }

}
