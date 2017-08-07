package com.example.myapplication;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextView textView_forehand;
    private TextView textView_backhand;
    private TextView textView_time;
    private TextView textView_drop;
    //蓝牙选项
    private MenuItem blueToothItem;
    private String blueToothItemTitle="";
    private TextView textView_blueTooth;

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
    private int totalCounter = 0;
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
    private String nameOfDrop="dropdata.txt";
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
    private String dropPath= Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/dropdata.txt";
    //  更新UI标志
    public final static int UPDATETIME=0;
    private static final int REQUEST = 1;
    private static final int REQUEST1 = 2;
    private static final int REQUEST_BULETOOTH=3;
    private Button btnStart;
    private Button btnPause;
    //正反计数
    private CounterActivity counterTimer;
    private CounterDrop counterDrop;
    //是否已经开始
    private String RankString;//选择的难度
    //标志位
    private boolean ifEverStarted=false;            //是否已经开始
    private boolean shouldPause=false;
    private boolean counterEverRelease=false;       //计数器是否曾被销毁
    //蓝牙相关
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    BluetoothGattCharacteristic mGattCharacteristics;

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

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                textView_blueTooth.setText("未连接蓝牙");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                ReadDataFromCharacteristic();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //TODO
                String data=intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println(data);
            }
        }
    };

    public void FindBlueToothList(List<BluetoothGattService> gattServices){
        if (gattServices == null) return;
        for (BluetoothGattService gattService : gattServices) {
            if(gattService.getUuid().toString().equals("6e400001-b5a3-f393-e0a9-e50e24dcca9e")){
                mGattCharacteristics = gattService.getCharacteristics().get(0);
                break;
            }
        }
    }

    public void ReadDataFromCharacteristic(){
        FindBlueToothList(mBluetoothLeService.getSupportedGattServices());
        if (mGattCharacteristics != null) {
            final BluetoothGattCharacteristic characteristic = mGattCharacteristics;
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(
                            mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(
                        characteristic, true);
            }
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
//                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        textView_drop=(TextView)findViewById(R.id.textView_time);
        this.lineChart=(LineChart)findViewById(R.id.linechart) ;

        this.dataChartManager =new DataChartManager(this.lineChart,dataPath,
                new String[]{"accX","accY","accZ"},new int[]{Color.RED,Color.BLUE,Color.GREEN},this);
        List<View.OnClickListener> listeners=this.dataChartManager.getListeners();

        counterDrop = new CounterDrop(this.dropPath);

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
        this.surfaceView_pingpang.setZOrderMediaOverlay(true);
        this.surfaceView_pingpang.setAlpha(0.8f);
        this.mediaPlayerManager=new MediaPlayerManager(this.surfaceView_pingpang,mediaPath_pingpang_zheng,true);

        counterTimer =new CounterActivity(this.counterPath);
        //this.startController();
        myListener();
        this.timerView.pause();

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.pauseController();
        unregisterReceiver(mGattUpdateReceiver);
        //this.mediaPlayerManager.pauseVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ifEverStarted&&!shouldPause)
            this.restartController();
        else
            shouldPause=false;

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            //Log.d(TAG, "Connect request result=" + result);
        }
        //this.mediaPlayerManager.reStartVideo();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
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
        MenuInflater inflater=this.getMenuInflater();
        inflater.inflate(R.menu.activity_main_drawer,menu);
        this.blueToothItem=menu.findItem(R.id.nav_bluetooth);
        getMenuInflater().inflate(R.menu.main, menu);
        //this.blueToothItem.setTitle("连接蓝牙");
        //invalidateOptionsMenu();
        this.textView_blueTooth=(TextView)findViewById(R.id.textView_blueTooth);
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
        if(id == R.id.nav_bluetooth)
        {
            if(mConnected == false){
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DeviceScanActivity.class);
                startActivityForResult(intent, REQUEST_BULETOOTH);
            }
            else{
                mConnected = false;
                unbindService(mServiceConnection);
                textView_blueTooth.setText("未连接蓝牙");
            }
        }
        else if (id == R.id.nav_file) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ExDialog.class);
            startActivityForResult(intent, REQUEST);
        } else if (id == R.id.nav_training){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SelectDifficulty.class);
            startActivityForResult(intent, REQUEST1);
        } else if (id == R.id.nav_statistic) {
            //数据统计
        }  else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Handler handler=new Handler()
    {
          public void handleMessage(Message msg)
          {
              blueToothItem.setTitle(blueToothItemTitle);
          }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST) {
                this.chooseFilePath=intent.getExtras().getString("path");
                this.resetController(this.chooseFilePath);
                this.shouldPause=true;
                //new AlertDialog.Builder(this).setMessage(" "+ uri).show();
            }
            else if(requestCode == REQUEST1){
                RankString = intent.getExtras().getString("ReturnRank");
                //new AlertDialog.Builder(this).setMessage(RankString).show();

            }
            else if(requestCode == REQUEST_BULETOOTH)
            {
                try
                {
                    System.out.println(blueToothItem.getTitle());
                    mDeviceName = intent.getExtras().getString("Name");
                    mDeviceAddress = intent.getExtras().getString("Address");

                    Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

                    this.textView_blueTooth.setText("已连接蓝牙设备:" + mDeviceName);
                    invalidateOptionsMenu();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class CounterDrop extends Thread
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
        private float justTime = 0;
        double newTaskTime;

        public CounterDrop(String path)
        {
            this.readDataFromFile=new ReadDataFromFile(path,false);
        }
        @Override
        public void run(){
            while(!this.readDataFromFile.endFlag)
            {
                if(!existTask&&isRunning)
                {
                    data = this.readDataFromFile.nextData(new int[]{0, 1}, "\t");
                    if (data != null)
                    {
                        newTaskTime = Double.valueOf(data.get(0).toString());
                        this.mark = Integer.valueOf(data.get(1).toString());
                        timer.cancel();
                        timer = new Timer();
                        justTime = (float)(newTaskTime - this.lastTime);
                        if(lastTime != 0)
                        {
                            timer.schedule(new CounterDrop.ChangeTimer(), (long) ((newTaskTime - this.lastTime) * 1000));
                            existTask = true;
                        }
                        else
                        {
                            try {
                                Thread.sleep((long)(newTaskTime*1000));
                            }catch (Exception e){

                            }
                        }
                        this.delayTime=(long)((newTaskTime-lastTime)*1000);
                        this.lastTime = newTaskTime;
                    }
                    else
                    {
                        readDataFromFile.endFlag = true;
                    }
                }
            }
        }
        private class ChangeTimer extends TimerTask
        {
            @Override
            public void run()
            {
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                textView_drop.setText(""+ justTime);
                            }
                        }
                );
                existTask=false;
            }
        }
        public void startCounter()
        {
            this.isRunning=true;
        }
        public void resetCounter()
        {
            this.timer.cancel();
            this.readDataFromFile.closeAllReaders();
            justTime = 0;
            textView_drop.setText("0");
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
            if(mediaPlayerManager.getMediaPath().equals(mediaPath_pingpang_fan))
                this.symbol=1;
            else
                this.symbol=0;
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
                            timerView.pause();
                            mediaPlayerManager.pauseVideo();
                        }
                    }
                }
            }
            catch(Exception e){

            }
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
        public void releaseCounter()
        {
            counterEverRelease=true;
            this.timer.cancel();
            this.readDataFromFile.closeAllReaders();
        }
        public void restartCounter()
        {
            if(!isRunning&&delayTime!=0)
            {
                if(this.timer!=null)
                    this.timer.cancel();
                this.timer=new Timer();
                try{
                    timer.schedule(new Change3DVedioTask(),this.delayTime-this.pauseTime+this.lastFinishedTime);
                }catch (Exception e){
                }

                this.isRunning=true;
                existTask=true;
            }
        }
        public void startCounter()
        {
            this.isRunning=true;
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
                            //mediaPlayerManager.startVideo();
                        }
                    }
                    else
                    {
                        counter_zheng++;
                        if(symbol==1)
                        {
                            symbol=0;
                            mediaPlayerManager.changeVideo(mediaPath_pingpang_zheng);
                            mediaPlayerManager.startVideo();
                        }
                    }
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    textView_forehand.setText(""+counter_zheng);
                                    textView_backhand.setText(""+counter_fan);
                                    gameOver();
                                }
                            }
                    );
                    lastFinishedTime=System.currentTimeMillis();
                    existTask=false;
                }
                catch(Exception e)
                {

                }
            }
        }
    }
    private void resetCounter()
    {
        counter_zheng=0;
        counter_fan=0;
        textView_forehand.setText("0");
        textView_backhand.setText("0");
        this.counterTimer.releaseCounter();
        this.counterDrop.resetCounter();
        this.counterDrop=new CounterDrop(this.dropPath);
        this.counterTimer=new CounterActivity(this.counterPath);
    }
    private void startController()
    {
        timerView.start();
        this.counterTimer.startCounter();
        this.counterTimer.start();
        this.counterDrop.startCounter();
        this.counterDrop.start();
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
        if(counterEverRelease)
        {
            this.counterTimer.startCounter();
            this.counterTimer.start();
            this.counterDrop.startCounter();
            this.counterDrop.start();
            counterEverRelease=false;
        }
        else
            this.counterTimer.restartCounter();
        dataChartManager.startChart();
        timerView.start();
    }
    private void resetFilePath(String newPath)
    {
        this.mediaPath=newPath+File.separator+this.nameOfReal;
        this.counterPath=newPath+File.separator+this.nameOfCounter;
        this.dataPath=newPath+File.separator+this.nameOfData;
        this.dropPath=newPath+File.separator+this.nameOfDrop;
    }
    private void resetController(String newPath)
    {
        this.resetFilePath(newPath);
        //TODO 文件存在检查
        this.timerView.reset();
        this.counterDrop.resetCounter();
        this.mediaPlayerManagerForReal.changeVideo(this.mediaPath);
        this.mediaPlayerManager.pauseVideo();
        this.resetCounter();
        this.dataChartManager.resetChart(this.dataPath);
        pauseController();
    }

    private char rankJudge(int time, int total){
        if(total < time * 30)
            return 'E';
        else if(total < time * 60)
            return 'D';
        else if (total < time * 90)
            return 'C';
        else if (total < time * 120)
            return 'B';
        else if (total < time * 150)
            return 'A';
        else
            return 'S';
    }

    private void gameOver(){
        if(RankString != null){
            if(RankString.equals("" + timerView.timeOfRank)){
                AlertDialog.Builder builder;
                totalCounter = counter_fan + counter_zheng;
                builder = new AlertDialog.Builder(this);
                builder.setTitle("评级结果");

                builder.setMessage("在"+timerView.timeOfRank +"分钟内颠球总数为：" + totalCounter + '\n' + "您的评级结果为：" + rankJudge(timerView.timeOfRank , totalCounter));
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("记录结果", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                pauseController();
                RankString = null;
                timerView.timeOfRank = 0;
            }
        }
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
