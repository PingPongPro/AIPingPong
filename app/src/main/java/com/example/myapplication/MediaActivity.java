package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.content.Intent;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class MediaActivity extends AppCompatActivity {

    private final static String TAG = MediaActivity.class.getSimpleName();
    private final int CHOOSEFILE=0;
    private final int RECORDING = 2;
    private final String VEDIONAME="real.mp4";
    private String lastFilePath=null;
    private String nextFilePath=null;
    private boolean shouldStart=true;
    private RelativeLayout rl;
    private int currentPosition=0;
    private boolean isSurfaceViewCreated=false;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private SeekBar seekBar;
    private ImageView rePlay;
    private ImageView lastFile;
    private ImageView ivPauseOrStart;
    private ImageView nextFile;
    private LinearLayout llController;
    private TextView tvTime;
    private TextView tvTimeMax;

    private Button button_vedio;
    private Button button_choose;
    private Button button_demo;
    private MediaPlayer player;

    private final int CONTROLLERHEIGHT=90;

    public final static String FirstFolder = "Idami";    //一级目录名称
    private final String DEMO_PATH = Environment.getExternalStorageDirectory()
            + File.separator +"ballGame"+ File.separator+ "demo";
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory()
            + File.separator + FirstFolder + File.separator;

    private String vedioPath = null;
    private List<MediaEntity> mList;

    private final static int SINGLE_MEDIA = 0;
    private final static int MULTI_MEDIA = 1;
    private int currentPlayType = SINGLE_MEDIA;
    private int currentMedia = 0;
    private final int REAL=0;
    private final int DEMO=1;
    private final int RECORD = 2;
    private int currentChoose=REAL;


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

    private void addOnclickListener()
    {
        this.button_vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChoose = RECORD;
                Intent intent = new Intent();
                intent.setClass(MediaActivity.this,PlayBack.class);
                startActivityForResult(intent, RECORDING);
            }
        });
        this.button_choose.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentChoose=REAL;
                        Intent intent = new Intent();
                        intent.setClass(MediaActivity.this, ExDialog.class);
                        startActivityForResult(intent, CHOOSEFILE);
                    }
                }
        );

        this.button_demo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentChoose=DEMO;
                        Intent intent = new Intent();
                        intent.putExtra("title","演示视频");
                        intent.putExtra("rootdir",DEMO_PATH);
                        intent.setClass(MediaActivity.this, ExDialog.class);
                        startActivityForResult(intent, CHOOSEFILE);
                    }
                }
        );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSEFILE) {
                try
                {
                    ///storage/emulated/0/ballGame/data/REAL.mp4
                    if(currentChoose==REAL)
                        this.vedioPath=data.getExtras().getString("path")+File.separator+VEDIONAME;
                    if(currentChoose==DEMO)
                        this.vedioPath=data.getExtras().getString("path")+File.separator+"demo_fan.mp4";
                    changeVedio();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }
    private void changeVedio()
    {
        try
        {
            pausePro();
            player.release();
            //if(player!=null&&player.isPlaying())
            //player.pause();
            player=new MediaPlayer();
            player.setDataSource(vedioPath);
            player.prepare();
            player.setDisplay(this.surfaceHolder);
            this.currentPosition=0;
            this.seekBar.setMax(player.getDuration());
            this.seekBar.setProgress(0);
            this.lastFilePath=getLastFilePath();
            this.nextFilePath=getNextFilePath();
            if(lastFilePath==null)
                lastFile.setVisibility(View.INVISIBLE);
            else
                lastFile.setVisibility(View.VISIBLE);
            if(nextFilePath==null)
                nextFile.setVisibility(View.INVISIBLE);
            else
                nextFile.setVisibility(View.VISIBLE);
            isPlaying=true;
            player.start();
            startPro();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    private String getLastFilePath()
    {
        try
        {
            final String RootDir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"ballGame";
            File f = new File(RootDir);
            List files = Arrays.asList(f.listFiles());

            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });
            String url=null;

            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    File file=((File)(files.get(i)));
                    if (file.isDirectory())
                    {
                        if(this.vedioPath.equals(file.getPath()+File.separator+VEDIONAME)&&url!=null)
                            return url+File.separator+VEDIONAME;
                        else
                            url=((File)(files.get(i))).getPath();
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private String getNextFilePath()
    {
        try
        {
            final String RootDir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"ballGame";
            File f = new File(RootDir);
            List files = Arrays.asList(f.listFiles());

            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });

            boolean found=false;
            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    String url=((File)(files.get(i))).getPath();
                    if (((File)(files.get(i))).isDirectory())
                    {
                        if(found&&url!=null)
                            return url+File.separator+VEDIONAME;
                        else if(this.vedioPath.equals(url+File.separator+VEDIONAME))
                            found=true;
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

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
        Drawable drawable = DensityUtil.scaleImage(this,R.drawable.seekbar,30,30,DensityUtil.DP);
        seekBar.setThumb(drawable);
        rePlay=(ImageView) findViewById(R.id.replay);
        rePlay.setImageDrawable(DensityUtil.scaleImage(this,R.drawable.playagain,60,60,DensityUtil.DP));
        lastFile=(ImageView) findViewById(R.id.lastfile);
        lastFile.setImageDrawable(DensityUtil.scaleImage(this,R.drawable.lastfile,60,60,DensityUtil.DP));
        ivPauseOrStart = (ImageView) findViewById(R.id.iv_pause_or_start);
        ivPauseOrStart.setImageDrawable(DensityUtil.scaleImage(this,R.drawable.video_pause,100,100,DensityUtil.DP));
        nextFile=(ImageView) findViewById(R.id.nextfile);
        nextFile.setImageDrawable(DensityUtil.scaleImage(this,R.drawable.nextfile,60,60,DensityUtil.DP));
        llController = (LinearLayout) findViewById(R.id.ll_control);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvTimeMax = (TextView) findViewById(R.id.tv_time_max);

        this.lastFilePath=getLastFilePath();
        this.nextFilePath=getNextFilePath();
        if(lastFilePath==null)
            lastFile.setVisibility(View.INVISIBLE);
        if(nextFilePath==null)
            nextFile.setVisibility(View.INVISIBLE);

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


        ivPauseOrStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    currentPosition=player.getCurrentPosition();
                    player.pause();
                    ivPauseOrStart.setImageResource(R.drawable.video_start);
                    isPlaying = false;
                    shouldStart=false;
                    pausePro();
                } else {
                    player.seekTo(currentPosition);
                    player.start();
                    seekBar.setMax(player.getDuration());
                    startPro();
                    ivPauseOrStart.setImageResource(R.drawable.video_pause);
                    isPlaying = true;
                    shouldStart=true;
                }
            }
        });

        rePlay.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MediaActivity.this.player.seekTo(0);
                        MediaActivity.this.player.start();
                    }
                }
        );

        lastFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(lastFilePath!=null)
                        {
                            vedioPath=lastFilePath;
                            changeVedio();
                        }
                    }
                }
        );
        nextFile.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(nextFilePath!=null)
                        {
                            vedioPath=nextFilePath;
                            changeVedio();
                        }
                    }
                }
        );

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

        button_vedio=(Button) findViewById(R.id.startrecording);
        button_choose=(Button)findViewById(R.id.data);
        button_demo=(Button) findViewById(R.id.exit);

        Drawable startRecording = getResources().getDrawable(R.drawable.startrecording);
        startRecording.setBounds(60, 0, 160, 100);
        button_vedio.setCompoundDrawables(startRecording, null, null, null);

        Drawable choose = getResources().getDrawable(R.drawable.choosefile);
        choose.setBounds(60, 0, 160, 100);
        button_choose.setCompoundDrawables(choose, null, null, null);

        Drawable demo = getResources().getDrawable(R.drawable.demovideo);
        demo.setBounds(60, 0, 160, 100);
        button_demo.setCompoundDrawables(demo, null, null, null);

        addOnclickListener();
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
            preparePlayer(mList.get(currentMedia).filePath);
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
                int currentPosition1 = player.getCurrentPosition();
                //update vedio progress
                //seekBar.setMax(player.getDuration());
                //System.out.println("startPro "+currentPosition1+" "+seekBar.getMax());
                seekBar.setProgress(currentPosition1);
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
        if(timer!=null)
            timer.cancel();
    }

    private void startPlayer()
    {
        player.seekTo(currentPosition);
        player.start();
        seekBar.setMax(player.getDuration());
        System.out.println(seekBar.getMax());
        startPro();
        isPlaying = true;
        //max time
        tvTimeMax.setText(getPlayPro(player.getDuration()-player.getCurrentPosition()));

    }

    private void preparePlayer(String path){
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
                    System.out.println(shouldStart);

                    if(shouldStart)
                        startPlayer();
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
        pro+=500;
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


    @Override
    protected void onPause() {
        super.onPause();
        isPlaying = false;
        currentPosition=player.getCurrentPosition();
        player.pause();
        pausePro();
        //player.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        pausePro();
        player.release();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (surfaceHolder == null)
        {
            surfaceHolder = surfaceView.getHolder();

            //当surface被显示时保持屏幕打开状态
            surfaceHolder.setKeepScreenOn(true);
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    //System.out.println("callBack");
                    isSurfaceViewCreated=true;
                    player = new MediaPlayer();
                    player.setOnErrorListener(
                            new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    return false;
                                }
                            }
                    );
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
                        preparePlayer(vedioPath);

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
                    isSurfaceViewCreated=false;
                }
            });
        }
        else if(isSurfaceViewCreated)
        {
            try
            {
                if(shouldStart)
                {
                    isPlaying = true;
                    player.seekTo(currentPosition);
                    player.start();
                    startPro();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
