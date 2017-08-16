package com.example.myapplication;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class FightActivity extends AppCompatActivity {

    //存储当前界面view临时变量
    private View vtmp;
    //页面变量
    private View upView;
    private View downView;
    //蓝牙相关
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    //竖向滑动界面
    private FragAdapter adapter;
    private VerticalViewPager VVP;
    //界面主按钮相关
    private Button btnStartStop;
    private Boolean isPressedBtnStartStop = false;
    private boolean hasEverPressedBtnStartStop = false;
    private Drawable START, STOP;
    //界面文字域
    private TextView textView_ForeHandNum;
    private TextView textView_BackHandNum;
    private TextView textView_DropTime;
    private TextView textView_AvgBallSpeed;
    //界面其他
    private Toolbar toolbar;
    private CircularRingPercentageView timer_HitBall;
    //3D模型区块
    private SurfaceView surfaceView_3Dmodel;
    private MediaPlayerManager mediaPlayerManagerFor3D;
    private String mediaPath_Forehand= Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/demo_zheng.mp4";            //正手3D
    private String mediaPath_Backhand= Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/demo_fan.mp4";
    //折线图
    private LineChart lineChart_Fight;


    BluetoothGattCharacteristic mGattCharacteristics;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
//                textView_blueTooth.setText("未连接蓝牙");
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

    public void findViewOfUpPage(){
        upView = vtmp = adapter.currentFragment.getView();
        timer_HitBall = (CircularRingPercentageView)vtmp.findViewById(R.id.timer_HitBall);
        textView_ForeHandNum = (TextView)vtmp.findViewById(R.id.textView_ForeHandNum);
        textView_BackHandNum = (TextView)vtmp.findViewById(R.id.textView_BackHandNum);
        textView_DropTime = (TextView)vtmp.findViewById(R.id.textView_DropTime);
        textView_AvgBallSpeed = (TextView)vtmp.findViewById(R.id.textView_AvgBallSpeed);
    }

    public void findViewOfDownPage(){
        downView = vtmp = adapter.currentFragment.getView();
        lineChart_Fight = (LineChart)vtmp.findViewById(R.id.linechart_Fight);
        surfaceView_3Dmodel = (SurfaceView)vtmp.findViewById(R.id.surfaceView_3Dmodel);
    }

    public void myListener(){
        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(VVP.getCurrentItem() == 0){
                findViewOfUpPage();
                if(isPressedBtnStartStop == false) {
                    isPressedBtnStartStop = true;
                    btnStartStop.setCompoundDrawables(STOP,null,null,null);
                    btnStartStop.setText("停止        ");
                    timer_HitBall.setMode(CircularRingPercentageView.TIMER);
                    timer_HitBall.start();
                }
                else{
                    isPressedBtnStartStop = false;
                    btnStartStop.setCompoundDrawables(START,null,null,null);
                    btnStartStop.setText("开始        ");
                }
            }
            else {
                finish();
            }
            }
        });
        VVP.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    toolbar.setTitle("00:00");
                    btnStartStop.setCompoundDrawables(STOP,null,null,null);
                    btnStartStop.setText("清零/退出           ");
                }
                else{
                    toolbar.setTitle("实战");
                    if(isPressedBtnStartStop == false) {
                        btnStartStop.setCompoundDrawables(START,null,null,null);
                        btnStartStop.setText("开始        ");
                    }
                    else{
                        btnStartStop.setCompoundDrawables(STOP,null,null,null);
                        btnStartStop.setText("停止        ");
                    }
                }
            }


            class Task extends TimerTask{
                public void run(){
                    vtmp = adapter.currentFragment.getView();
                    if(upView != null && vtmp != upView && isPressedBtnStartStop){
                        findViewOfDownPage();
                        hasEverPressedBtnStartStop = true;
                        mediaPlayerManagerFor3D = new MediaPlayerManager(surfaceView_3Dmodel, mediaPath_Forehand, true);
                        mediaPlayerManagerFor3D.startVideo();
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == 0){
                    if(hasEverPressedBtnStartStop == false){
                        if(isPressedBtnStartStop && vtmp == upView)
                        {
                            Timer tTmp = new Timer();
                            Task ttTmp = new Task();
                            tTmp.schedule(ttTmp, 50);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);

        Intent intent = getIntent();
        mDeviceName = intent.getStringExtra("DeviceName");
        mDeviceAddress = intent.getStringExtra("DeviceAddress");

        toolbar = (Toolbar) findViewById(R.id.toolbar_Fight);
        toolbar.setTitle("实战");
        setSupportActionBar(toolbar);


        btnStartStop = (Button)findViewById(R.id.btnStartStop);
        START = getResources().getDrawable(R.drawable.start);
        START.setBounds(60, 0, 160, 100);
        STOP = getResources().getDrawable(R.drawable.stop);
        STOP.setBounds(60, 0, 160, 100);
        btnStartStop.setCompoundDrawables(START,null,null,null);

        List<Fragment> fragments=new ArrayList<Fragment>();
        fragments.add(new FightActivityUpFragment());
        fragments.add(new FightActivityDownFragment());
        adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        VVP = (VerticalViewPager)findViewById(R.id.container);
        VVP.setAdapter(adapter);
        myListener();
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        //this.mediaPlayerManager.pauseVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
