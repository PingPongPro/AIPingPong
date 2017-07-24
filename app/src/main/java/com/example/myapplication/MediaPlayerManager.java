package com.example.myapplication;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;

/**
 * Created by 叶明林 on 2017/7/23.
 */

public class MediaPlayerManager{
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder surfaceHolder;
    private int currentIndex=0;
    private boolean isSurfaceCreated=false;
    private boolean isPlaying=false;
    private boolean isLoop=false;

    private String mediaPath;        // 视频路径

    public MediaPlayerManager(SurfaceView surfaceView1,String path,boolean loop)
    {
        this.isLoop=loop;
        this.surfaceView=surfaceView1;
        this.mediaPath=path;
        this.createSurface();
        this.startVideo();
    }
    public void changeLoop(boolean loop)
    {
        this.isLoop=loop;
    }
    private void createSurface()
    {
        this.surfaceHolder=surfaceView.getHolder();
        this.surfaceHolder.addCallback(new MyCallBack());
        surfaceHolder.setFixedSize(100,100);
    }
    public void pause(){this.player.pause();}
    private void startVideo()
    {
        try
        {
            player=new MediaPlayer();
            player.setLooping(this.isLoop);
            this.player.setDataSource(this.mediaPath);
            this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.player.prepare();
            player.seekTo(this.currentIndex);
            player.start();
            isPlaying=true;
        }
        catch(Exception e)
        {
            isPlaying=false;
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
    public void changeVideo(String newPath)
    {
        this.mediaPath=newPath;
        this.currentIndex=0;
        player.release();
        isPlaying=false;
        //if(!isSurfaceCreated)
        //this.createSurface();
        try
        {
            player=new MediaPlayer();
            player.setDisplay(surfaceHolder);
            player.setLooping(this.isLoop);
            this.player.setDataSource(this.mediaPath);
            this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.player.prepare();
            player.seekTo(this.currentIndex);
            player.start();
            isPlaying=true;
        }
        catch(Exception e)
        {
            isPlaying=false;
            e.printStackTrace();
        }
    }
    public void onRestart()
    {
        if(!isSurfaceCreated)
            createSurface();
        this.startVideo();
    }
}
