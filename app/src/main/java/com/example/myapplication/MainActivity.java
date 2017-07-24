package com.example.myapplication;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
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

import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;


public class MainActivity extends AppCompatActivity{
    private TextView textView_zhengshou;
    private TextView textView_fanshou;
    //图像相关对象
    private ReadDataFromFile dataFile;
    private LineChart lineChart;
    private myChart mychart;
    private int counter_zheng=0;
    private int counter_fan=0;
    private boolean changed=false;
    //视频相关对象
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder surfaceHolder;
    private int currentIndex=0;
    private boolean isSurfaceCreated=false;

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

    private TimerActivity myTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.pic1);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        myTimer=new TimerActivity(this.counterPath);

        this.lineChart=(LineChart)findViewById(R.id.linechart) ;
        this.dataFile=new ReadDataFromFile(this.dataPath,true);
        this.mychart=new myChart(this.lineChart);

        textView_fanshou=(TextView)findViewById(R.id.textView_fan);
        textView_zhengshou=(TextView)findViewById(R.id.textView_zheng);
        surfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        createSurface();
        this.startVideo();
        mychart.start();
        //mychart.onResume();

        this.surfaceView_pingpang=(SurfaceView)findViewById(R.id.surfaceView2);
        this.mediaPlayerManager=new MediaPlayerManager(this.surfaceView_pingpang,mediaPath_pingpang_zheng,true);


    }
    private void createSurface()
    {
        this.surfaceHolder=surfaceView.getHolder();
        this.surfaceHolder.addCallback(new MyCallBack());
        surfaceHolder.setFixedSize(100,100);
    }
    private void startVideo()
    {
        try
        {
            player=new MediaPlayer();
            this.player.setDataSource(this.mediaPath);
            this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.player.prepare();
            player.seekTo(this.currentIndex);
            player.start();
            this.myTimer.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private class MyCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(surfaceHolder);
            isSurfaceCreated=true;
        }
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            isSurfaceCreated=false;
        }
    }
    private class myChart extends Thread
    {
        private DataChartManager chartManager;
        private List<Double> dataList=new ArrayList<Double>();
        private List<String> dataName=new ArrayList<String>();
        private List<Integer> dataColor=new ArrayList<Integer>();
        private Timer timer = new Timer();
        public myChart(LineChart chart)
        {
            //TODO 图像名称
            this.dataName.add("accX");
            this.dataName.add("accY");
            this.dataName.add("accZ");
            //TODO 图像颜色
            this.dataColor.add(Color.RED);
            this.dataColor.add(Color.BLUE);
            this.dataColor.add(Color.GREEN);
            this.chartManager=new DataChartManager(chart,this.dataName,this.dataColor);
            //TODO 图像相关设置
            //this.chartManager.setYAxis(200,-200, 400);
            this.chartManager.setDescription("");
            //TODO 选择显示图像
            Button accx=(Button)findViewById(R.id.accX);
            Button accy=(Button)findViewById(R.id.accY);
            Button accz=(Button)findViewById(R.id.accZ);

            accx.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chartManager.chooseIndex(0);
                        }
                    }
            );

            accy.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chartManager.chooseIndex(1);
                        }
                    }
            );

            accz.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chartManager.chooseIndex(2);
                        }
                    }
            );
        }
        @Override
        public void run()
        {
            timer = new Timer();
            timer.schedule(new myTask(), 20,20);
        }
        private class myTask extends TimerTask
        {
            private int counter=0;
            @Override
            public void run()
            {
                    runOnUiThread(
                        new Runnable()
                        {
                              public void run() {
                                  //往图像里添加数据
                                  try
                                  {
                                      List<Object> list=dataFile.nextData(new int[]{0,1,2},"\t");
                                      if(counter%10==0)
                                      {
                                          dataList.add(Double.valueOf(list.get(0).toString()));
                                          dataList.add(Double.valueOf(list.get(1).toString()));
                                          dataList.add(Double.valueOf(list.get(2).toString()));
                                          chartManager.addEntry(dataList);
                                          dataList.clear();
                                      }
                                      counter++;
                                      if(changed)
                                      {
                                          textView_fanshou.setText("反手: "+counter_fan);
                                          textView_zhengshou.setText("正手: "+counter_zheng);
                                          changed=false;
                                      }
                                  }
                                  catch(Exception e){
                                      e.printStackTrace();
                                  }
                              }
                        }
                    );
            }
        }
    }
    private class TimerActivity extends Thread {
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
        public void run() {
            try
            {
                while(!this.readDataFromFile.endFlag)
                {
                    if(!existTask) {
                        data = this.readDataFromFile.nextData(new int[]{0, 1}, "\t");
                        if (data != null) {
                            double newTaskTime = Double.valueOf(data.get(0).toString());
                            this.mark = Integer.valueOf(data.get(1).toString());
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(new myTask(), (long) ((newTaskTime - this.lastTime) * 1000));
                            this.lastTime = newTaskTime;
                            existTask = true;
                        } else
                        {
                            readDataFromFile.endFlag = true;
                            mediaPlayerManager.pause();
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

        /*Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            };
        };  */
        private class myTask extends TimerTask {
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
