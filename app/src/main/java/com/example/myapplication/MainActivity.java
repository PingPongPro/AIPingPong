package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
    //时间相关
    //private SystemTimeManager systemTimeManager;
    private CircularRingPercentageView timerView;
    //图像相关对象
    private ReadDataFromFile dataFile;
    private LineChart lineChart;
    private DataChartManager dataChartManager;
    private int counter_zheng=0;
    private int counter_fan=0;
    private boolean changed=false;
    //视频相关对象
    private SurfaceView surfaceView;
    private MediaPlayerManager mediaPlayerManagerForReal;

    private SurfaceView surfaceView_pingpang;
    private MediaPlayerManager mediaPlayerManager;
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

    private TimerActivity myTimer;
    private Handler mainHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MainActivity.UPDATETIME:
                    Bundle bundle= msg.getData();
                    textView_time.setText("时间: "+bundle.get("minite")+":"+bundle.get("second"));
                    int second=Integer.valueOf(bundle.get("second").toString());
                    timerView.setProgress(second, new CircularRingPercentageView.OnProgressScore() {
                        @Override
                        public void setProgressScore(float score) {
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.pic1);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);*/
        try
        {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_main);
            this.timerView=(CircularRingPercentageView)findViewById(R.id.timer);
            /*timerView.setProgress(0, new CircularRingPercentageView.OnProgressScore() {
                @Override
                public void setProgressScore(float score) {
                    Log.e("12", score + "");

                }

            });*/
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        textView_backhand =(TextView)findViewById(R.id.textView_fan);
        textView_forehand =(TextView)findViewById(R.id.textView_zheng);
        textView_time=(TextView)findViewById(R.id.textView_time);
        this.lineChart=(LineChart)findViewById(R.id.linechart) ;
        this.dataFile=new ReadDataFromFile(this.dataPath,true);

        this.dataChartManager =new DataChartManager(this.lineChart,new TaskForChart(),
                new String[]{"accX","accY","accZ"},new int[]{Color.RED,Color.BLUE,Color.GREEN});
        Button accx=(Button)findViewById(R.id.accX);
        Button accy=(Button)findViewById(R.id.accY);
        Button accz=(Button)findViewById(R.id.accZ);
        List<View.OnClickListener> listeners=this.dataChartManager.getListeners();
        accx.setOnClickListener(listeners.get(0));
        accy.setOnClickListener(listeners.get(1));
        accz.setOnClickListener(listeners.get(2));

        myTimer=new TimerActivity(this.counterPath);
        surfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        this.mediaPlayerManagerForReal=new MediaPlayerManager(this.surfaceView,mediaPath,false);
        this.mediaPlayerManagerForReal.addOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediaPlayerManagerForReal.getPlayerSate()==true)
                        {
                            mediaPlayerManagerForReal.pauseVideo();
                            mediaPlayerManager.pauseVideo();
                            dataChartManager.setPassingData(false);
                            timerView.pause();
                        }
                        else
                        {
                            try
                            {
                                mediaPlayerManagerForReal.startVideo();
                                mediaPlayerManager.startVideo();
                                dataChartManager.setPassingData(true);
                                timerView.start();
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        //this.systemTimeManager=new SystemTimeManager();
        this.mediaPlayerManagerForReal.startVideo();
        timerView.start();
        //this.systemTimeManager.start();
        this.myTimer.start();
        dataChartManager.start();

        this.surfaceView_pingpang=(SurfaceView)findViewById(R.id.surfaceView2);
        this.mediaPlayerManager=new MediaPlayerManager(this.surfaceView_pingpang,mediaPath_pingpang_zheng,true);
        this.mediaPlayerManager.startVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mediaPlayerManager.pauseVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.mediaPlayerManager.reStartVideo();
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
                Uri uri = intent.getData();
                new AlertDialog.Builder(this).setMessage(" "+ uri).show();
            }
        }
    }

    private class TaskForChart extends TimerTask
    {
        private List<Double> dataList=new ArrayList<Double>();
        private int counter=0;
        @Override
        public void run()
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(dataChartManager.isPassingData())
                    {
                        try
                        {
                            List<Object> list=dataFile.nextData(new int[]{0,1,2},"\t");
                            if(!dataFile.endFlag)
                            {
                                if(counter%10==0)
                                {
                                    dataList.add(Double.valueOf(list.get(0).toString()));
                                    dataList.add(Double.valueOf(list.get(1).toString()));
                                    dataList.add(Double.valueOf(list.get(2).toString()));
                                    dataChartManager.addEntry(dataList);
                                    dataList.clear();
                                }
                                counter++;
                                if(changed)
                                {
                                    try
                                    {
                                        textView_forehand.setText("正手: "+counter_zheng);
                                        textView_backhand.setText("反手: "+counter_fan);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                    changed=false;
                                }
                            }
                            else
                            {
                                if(counter%10==0)
                                {
                                    for(int i=0;i<3;i++)
                                        dataList.add(0.0);
                                    dataChartManager.addEntry(dataList);
                                    dataList.clear();
                                }
                                counter++;
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
    private class TimerActivity extends Thread
    {
        private ReadDataFromFile readDataFromFile;
        private Timer timer = new Timer();
        private boolean existTask=false;
        private double lastTime=0;
        private int mark=0;
        private int symbol=0;
        private List<Object>data=null;
        public TimerActivity(String path)
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
                    if(!existTask)
                    {
                        data = this.readDataFromFile.nextData(new int[]{0, 1}, "\t");
                        if (data != null)
                        {
                            double newTaskTime = Double.valueOf(data.get(0).toString());
                            this.mark = Integer.valueOf(data.get(1).toString());
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(new myTask(), (long) ((newTaskTime - this.lastTime) * 1000));
                            this.lastTime = newTaskTime;
                            existTask = true;
                        }
                        else
                        {
                            readDataFromFile.endFlag = true;
                            mediaPlayerManager.pauseVideo();
                        }
                    }
                    // 1s后执行task,经过1s再次执行
                }
            }
            catch(Exception e)
            {
                for(int i=0;i<10;i++)
                {
                    //System.out.println(data.get(0)+" "+data.get(1));
                    e.printStackTrace();
                }
            }

        }
        private class myTask extends TimerTask
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
                        changed=true;
                    }
                    else
                    {
                        counter_zheng++;
                        if(symbol==1)
                        {
                            symbol=0;
                            mediaPlayerManager.changeVideo(mediaPath_pingpang_zheng);
                        }
                        changed=true;
                    }
                    existTask=false;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
