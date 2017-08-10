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
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MenuInflater;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.util.List;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnData;
    private Button btnTendency;
    private Button btnChooseMode;
    private TextView textView_TrainingTime;
    private TextView textView_AvgBallNum;
    private TextView textView_MaxSpeed;

    //蓝牙选项
    private MenuItem blueToothItem;
    private String blueToothItemTitle="";
    private TextView textView_blueTooth;

    private TabManager tabManager;
    //时间相关
    //private SystemTimeManager systemTimeManager;
    private CircularRingPercentageView timerView;

    //private boolean changed=false;
    //视频相关对象
    private SurfaceView surfaceView;
    private MediaPlayerManager mediaPlayerManagerForReal;

    private SurfaceView surfaceView_pingpang;
    private MediaPlayerManager mediaPlayerManager;
    //文件名称
    private String chooseFilePath= Environment.getExternalStorageDirectory().getAbsolutePath()+
            File.separator+"ballGame/default";
    private String nameOfReal="real.mp4";
    private String nameOfData="data.txt";
    private String nameOfCounter="counterdata.txt";
    private String nameOfDrop="dropdata.txt";

    //  更新UI标志
    public final static int UPDATETIME=0;
    private static final int REQUEST = 1;
    private static final int REQUEST1 = 2;
    private static final int REQUEST_BULETOOTH=3;
    private static final int REQUEST_PLAYBACK = 4;
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

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST) {
//                this.chooseFilePath=intent.getExtras().getString("path");
//                this.resetController(this.chooseFilePath);
//                this.shouldPause=true;
            }
            else if(requestCode == REQUEST1){
                RankString = intent.getExtras().getString("ReturnRank");
                //new AlertDialog.Builder(this).setMessage(RankString).show();

            }
            else if(requestCode == REQUEST_BULETOOTH)
            {
                try
                {
                    //System.out.println(blueToothItem.getTitle());
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        {
            btnData = (Button)findViewById(R.id.btnData);
            btnTendency = (Button)findViewById(R.id.btnTendency);
            btnChooseMode = (Button)findViewById(R.id.btnChooseMode);
            Drawable START = getResources().getDrawable(R.drawable.start);
            Drawable DATA = getResources().getDrawable(R.drawable.data);
            Drawable TENDENCY = getResources().getDrawable(R.drawable.tendency);
            START.setBounds(60, 0, 160, 100);
            DATA.setBounds(0,60,80,140);
            TENDENCY.setBounds(0,60,80,140);
            btnData.setCompoundDrawables(null,DATA,null,null);
            btnTendency.setCompoundDrawables(null,TENDENCY,null,null);
            btnChooseMode.setCompoundDrawables(START,null,null,null);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        if(mConnected == true)
            unbindService(mServiceConnection);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        if (id == R.id.nav_bluetooth) {
            if(mConnected == false){
                Intent intent = new Intent();
                intent.setClass(Main2Activity.this, DeviceScanActivity.class);
                startActivityForResult(intent, REQUEST_BULETOOTH);
            }
            else{
                mConnected = false;
                unbindService(mServiceConnection);
                mBluetoothLeService = null;
                textView_blueTooth.setText("未连接蓝牙");
            }
        } else if (id == R.id.nav_data) {

        } else if (id == R.id.nav_file) {
            Intent intent = new Intent();
            intent.setClass(Main2Activity.this, ExDialog.class);
            startActivityForResult(intent, REQUEST);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
