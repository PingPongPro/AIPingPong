package com.example.myapplication;

import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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



public class MainActivity extends AppCompatActivity{
    private TextView textView_forehand;
    private TextView textView_backhand;
    private TextView textView_time;
    //时间相关
    private SystemTimeManager systemTimeManager;
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
    public final static int UPDATEHAND=1;

    private TimerActivity myTimer;
    private Handler mainHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MainActivity.UPDATETIME:
                    textView_time.setText("时间: "+msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.pic1);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

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
                            systemTimeManager.pauseTimer();
                        }
                        else
                        {
                            try
                            {
                                mediaPlayerManagerForReal.startVideo();
                                mediaPlayerManager.startVideo();
                                dataChartManager.setPassingData(true);
                                systemTimeManager.startTimer(mediaPlayerManagerForReal.getCurrentIndex());
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        this.systemTimeManager=new SystemTimeManager();
        this.mediaPlayerManagerForReal.startVideo();
        this.systemTimeManager.start();
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
    private class SystemTimeManager extends Thread
    {
        private Timer timer;
        private int totalSecond=0;
        public void startTimer()
        {
            this.timer=new Timer();
            this.timer.schedule(new timeTask(),1000,1000);
        }
        public void startTimer(int indexTime)
        {
            this.timer=new Timer();
            this.timer.schedule(new timeTask(),1000-(indexTime%1000),1000);
        }
        public void pauseTimer()
        {
            this.timer.cancel();
        }
        public void resetTimer()
        {
            this.totalSecond=0;
        }

        @Override
        public void run()
        {
            this.startTimer();
        }

        private class timeTask extends TimerTask
        {
            @Override
            public void run()
            {
                totalSecond++;
                int second=totalSecond%60;
                int minite=totalSecond/60;
                Message message=new Message();
                message.what=MainActivity.UPDATETIME;
                if(second<10)
                    message.obj=minite+":0"+second;
                else
                    message.obj=minite+":"+second;
                mainHandler.sendMessage(message);
            }
        }
    }
}
