package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextView textView_forehand;
    private TextView textView_backhand;
    private TextView textView_time;

    private TabManager tabManager;
    //时间相关
    //private SystemTimeManager systemTimeManager;
    private CircularRingPercentageView timerView;
    //图像相关对象
    //private ReadDataFromFile dataFile;
    private LineChart lineChart;
    private DataChartManager dataChartManager;
    private int counter_zheng=0;
    private int counter_fan=0;
    //private boolean changed=false;
    //视频相关对象
    private SurfaceView surfaceView;
    private MediaPlayerManager mediaPlayerManagerForReal;

    private SurfaceView surfaceView_pingpang;
    private MediaPlayerManager mediaPlayerManager;
    //文件名称
    private String chooseFilePath=Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/default";
    private String nameOfReal="real.mp4";
    private String nameOfData="data.txt";
    private String nameOfCounter="counterdata.txt";
    //文件路径
    private String mediaPath= Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/real.mp4";        // 视频路径
    private String dataPath=Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/data.txt";         //数据路径
    private String counterPath=Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/counterdata.txt";           //左右手数据
    private String mediaPath_pingpang_zheng= Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/demo_zheng.mp4";            //正手3D
    private String mediaPath_pingpang_fan= Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/demo_fan.mp4";            //反手3D
    //  更新UI标志
    public final static int UPDATETIME=0;
    private static final int REQUEST = 1;
    private static final int REQUEST1 = 2;
    private Button btnStart;
    private Button btnPause;
    //正反计数
    private CounterActivity counterTimer;
    //是否已经开始
    private boolean ifEverStarted=false;
    private void myListener() {
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseController();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ifEverStarted)
                {
                    ifEverStarted=true;
                    startController();
                }
                else
                    restartController();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        this.resetFilePath(this.chooseFilePath);
        btnPause=(Button)findViewById(R.id.btnPause);
        btnStart=(Button)findViewById(R.id.btnStart);

        this.timerView=(CircularRingPercentageView)findViewById(R.id.timer);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);;
        this.timerView.setCollapsingToolbarLayout(collapsingToolbar);

        TabHost tabHost=(TabHost)findViewById(R.id.tabhost);
        tabHost.setup();
        this.tabManager=new TabManager(this,tabHost);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textView_backhand =(TextView)findViewById(R.id.textView_fan);
        textView_forehand =(TextView)findViewById(R.id.textView_zheng);
        textView_time=(TextView)findViewById(R.id.textView_time);
        this.lineChart=(LineChart)findViewById(R.id.linechart) ;

        this.dataChartManager =new DataChartManager(this.lineChart,dataPath,
                new String[]{"accX","accY","accZ"},new int[]{Color.RED,Color.BLUE,Color.GREEN},this);
        List<View.OnClickListener> listeners=this.dataChartManager.getListeners();

        counterTimer =new CounterActivity(this.counterPath);
        surfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        this.mediaPlayerManagerForReal=new MediaPlayerManager(this.surfaceView,mediaPath,false);
        /*this.mediaPlayerManagerForReal.addOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediaPlayerManagerForReal.getPlayerSate()==true)
                            pauseController();
                        else
                            restartController();
                    }
                }
        );*/
        this.surfaceView_pingpang=(SurfaceView)findViewById(R.id.surfaceView2);
        this.mediaPlayerManager=new MediaPlayerManager(this.surfaceView_pingpang,mediaPath_pingpang_zheng,true);
        System.out.println(this.mediaPath);
        //this.startController();
        myListener();
        this.timerView.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.pauseController();
        //this.mediaPlayerManager.pauseVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ifEverStarted)
            this.restartController();
        //this.mediaPlayerManager.reStartVideo();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_file) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ExDialog.class);
            startActivityForResult(intent, REQUEST);
        } else if (id == R.id.nav_training){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SelectDifficulty.class);
            startActivityForResult(intent, REQUEST1);
        } else if (id == R.id.nav_statistic) {

        }  else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST) {
                this.chooseFilePath=intent.getExtras().getString("path");
                this.resetController(this.chooseFilePath);
                //new AlertDialog.Builder(this).setMessage(" "+ uri).show();
            }
            else if(requestCode == REQUEST1){
                String stmp = intent.getExtras().getString("ReturnRank");
                new AlertDialog.Builder(this).setMessage(stmp).show();
            }
        }
    }
    private class CounterActivity extends Thread
    {
        private ReadDataFromFile readDataFromFile;
        private Timer timer = new Timer();
        private boolean existTask=false;
        private double lastTime=0;              //从文件中读取到的上一次时间
        private long lastFinishedTime=0;        //上一次成功执行任务的真实时间
        private long pauseTime=0;               //暂停的真实时间
        private long delayTime=0;               //任务延迟时间
        private int mark=0;
        private int symbol=0;
        private List<Object>data=null;
        private boolean isRunning=false;
        public CounterActivity(String path)
        {
            this.readDataFromFile=new ReadDataFromFile(path,false);
        }
        @Override
        public void run()
        {
            try
            {
                while(!this.readDataFromFile.endFlag)
                {
                    if(!existTask&&isRunning)
                    {
                        data = this.readDataFromFile.nextData(new int[]{0, 1}, "\t");
                        if (data != null)
                        {
                            double newTaskTime = Double.valueOf(data.get(0).toString());
                            this.mark = Integer.valueOf(data.get(1).toString());
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(new Change3DVedioTask(), (long) ((newTaskTime - this.lastTime) * 1000));
                            this.delayTime=(long)((newTaskTime-lastTime)*1000);
                            this.lastTime = newTaskTime;
                            existTask = true;
                        }
                        else
                        {
                            readDataFromFile.endFlag = true;
                            mediaPlayerManager.pauseVideo();
                        }
                    }
                }
            }
            catch(Exception e){e.printStackTrace();}
        }
        public void pauseCounter()
        {
            if(this.isRunning)
            {
                this.isRunning=false;
                this.pauseTime=System.currentTimeMillis();
                this.timer.cancel();
                existTask=false;
            }
        }
        public void restartCounter()
        {
            if(!isRunning&&delayTime!=0)
            {
                if(this.timer!=null)
                    this.timer.cancel();
                this.timer=new Timer();
                timer.schedule(new Change3DVedioTask(),this.delayTime-this.pauseTime+this.lastFinishedTime);
                this.isRunning=true;
                existTask=true;
            }
        }
        public void startCounter()
        {
            this.isRunning=true;
        }
        public void resetCounter(String newPath)
        {
            pauseCounter();
            this.readDataFromFile.closeAllReaders();
            this.readDataFromFile=new ReadDataFromFile(newPath,false);
            lastTime=0;
            lastFinishedTime=0;
            pauseTime=0;
            delayTime=0;
            symbol=0;
        }
        private class Change3DVedioTask extends TimerTask
        {
            @Override
            public void run()
            {
                try
                {
                    if(mark==1)
                    {
                        counter_fan++;
                        if(symbol==0)
                        {
                            symbol=1;
                            mediaPlayerManager.changeVideo(mediaPath_pingpang_fan);
                        }
                    }
                    else
                    {
                        counter_zheng++;
                        if(symbol==1)
                        {
                            symbol=0;
                            mediaPlayerManager.changeVideo(mediaPath_pingpang_zheng);
                        }
                    }
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    textView_forehand.setText("正手: "+counter_zheng);
                                    textView_backhand.setText("反手: "+counter_fan);
                                }
                            }
                    );
                    lastFinishedTime=System.currentTimeMillis();
                    existTask=false;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startController()
    {
        timerView.start();
        this.counterTimer.startCounter();
        this.counterTimer.start();
        dataChartManager.start();
        this.mediaPlayerManagerForReal.startVideo();
        this.mediaPlayerManager.startVideo();
    }
    private void pauseController()
    {
        mediaPlayerManagerForReal.pauseVideo();
        mediaPlayerManager.pauseVideo();
        dataChartManager.pauseChart();
        this.counterTimer.pauseCounter();
        timerView.pause();
    }
    private void restartController()
    {
        mediaPlayerManagerForReal.startVideo();
        mediaPlayerManager.startVideo();
        this.counterTimer.restartCounter();
        dataChartManager.startChart();
        timerView.start();
    }
    private void resetFilePath(String newPath)
    {
        this.mediaPath=newPath+File.separator+this.nameOfReal;
        this.counterPath=newPath+File.separator+this.nameOfCounter;
        this.dataPath=newPath+File.separator+this.nameOfData;
    }
    private void resetController(String newPath)
    {
        counter_zheng=0;
        counter_fan=0;
        textView_forehand.setText("正手:0");
        textView_backhand.setText("反手:0");
        this.resetFilePath(newPath);
        //TODO 文件存在检查
        
        this.timerView.reset();
        this.mediaPlayerManagerForReal.changeVideo(this.mediaPath);
        //this.mediaPlayerManager.pauseVideo();
        this.counterTimer.resetCounter(this.counterPath);
        this.dataChartManager.resetChart(this.dataPath);
        pauseController();
    }
}
