package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ModeChoose extends Activity {

    private Button btnBallGame;
    private Button btnFight;
    private Button btnPlayBack;
    private String DeviceName;
    private String DeviceAddress;

    public void myListener(){
        btnBallGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ModeChoose.this, BallGame.class);
                intent.putExtra("DeviceName", DeviceName);
                intent.putExtra("DeviceAddress", DeviceAddress);
                startActivity(intent);
                finish();
            }
        });
        btnFight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ModeChoose.this, FightActivity.class);
                intent.putExtra("DeviceName", DeviceName);
                intent.putExtra("DeviceAddress", DeviceAddress);
                startActivity(intent);
                finish();
            }
        });
        btnPlayBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ModeChoose.this, PlayBack.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_choose);

        Intent intent = getIntent();
        DeviceName = intent.getStringExtra("DeviceName");
        DeviceAddress = intent.getStringExtra("DeviceAddress");

        {
            btnBallGame = (Button) findViewById(R.id.btnBallGame);
            btnFight = (Button) findViewById(R.id.btnFight);
            btnPlayBack = (Button) findViewById(R.id.btnPlayBack);
            Drawable BALLGAME = getResources().getDrawable(R.drawable.ballgame);
            Drawable FIGHT = getResources().getDrawable(R.drawable.fight);
            Drawable PLAYBACK = getResources().getDrawable(R.drawable.playback);
            BALLGAME.setBounds(70, 0, 120, 50);
            FIGHT.setBounds(60, 0, 160, 100);
            PLAYBACK.setBounds(60, 0, 160, 100);
            btnBallGame.setCompoundDrawables(BALLGAME, null, null, null);
            btnFight.setCompoundDrawables(FIGHT, null, null, null);
            btnPlayBack.setCompoundDrawables(PLAYBACK, null, null, null);
        }

        myListener();
    }
}
