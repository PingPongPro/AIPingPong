package com.example.myapplication;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;

/**
 * Created by 叶明林 on 2017/7/23.
 */

public class MediaPlayerManager
{
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder surfaceHolder;
    private int currentIndex=0;
    private String mediaPath;                   // 视频路径
    private boolean isSurfaceCreated=false;
    private boolean isLoop=false;
    private Drawable getVedioPicture(String pathName,long time)
    {
        Drawable result=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
                retriever.setDataSource(pathName); 
                result=new BitmapDrawable(retriever.getFrameAtTime());
        } catch(IllegalArgumentException e) {
                 e.printStackTrace();  
        } catch (RuntimeException e) {
                e.printStackTrace();  
        } 
        finally {
            try {
                retriever.release();
                } catch (RuntimeException e) {
                   e.printStackTrace();  
            }
        }
        return result;
    }
    public MediaPlayerManager(SurfaceView surfaceView1, String path, boolean loop)
    {
        this.isLoop=loop;
        this.surfaceView=surfaceView1;
        this.mediaPath=path;
        this.createSurface();
        this.prepareVideo();
        this.surfaceView.setBackground(this.getVedioPicture(this.mediaPath,this.currentIndex));
    }
    public int getCurrentIndex()
    {
        return this.player.getCurrentPosition();
    }
    public void addOnClickListener(View.OnClickListener listener)
    {
        if(this.surfaceView!=null)
            this.surfaceView.setOnClickListener(listener);
    }
    public void setLoopState(boolean loop)
    {
        this.isLoop=loop;
    }
    private void createSurface()
    {
        this.surfaceHolder=surfaceView.getHolder();
        this.surfaceHolder.addCallback(new MyCallBack());
        surfaceHolder.setFixedSize(100,100);
    }
    private void prepareVideo()
    {
        try
        {
            player=new MediaPlayer();
            player.setVolume(0,0);
            player.setLooping(this.isLoop);
            this.player.setDataSource(this.mediaPath);
            this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.player.prepare();
            this.player.setDisplay(this.surfaceHolder);
            player.seekTo(this.currentIndex);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    //视频开始或暂停后恢复调用
    public void startVideo()
    {
        if(surfaceView.getBackground()!=null)
            this.surfaceView.setBackground(null);
        if(this.player.isPlaying()==false)
            player.start();
    }
    public void pauseVideo()
    {
        if(this.player!=null&&this.player.isPlaying())
        {
            currentIndex=player.getCurrentPosition();
            this.player.pause();
        }
    }
    //视频从后台恢复调用
    public void reStartVideo()
    {
        if(!isSurfaceCreated)
            createSurface();
        this.prepareVideo();
        this.startVideo();
    }
    public void changeVideo(String newPath)
    {
        this.mediaPath=newPath;
        this.currentIndex=0;
        player.release();
        try
        {
            if(!isSurfaceCreated)
            {
                this.surfaceHolder=surfaceView.getHolder();
                this.surfaceHolder.addCallback(new MyCallBack());
                surfaceHolder.setFixedSize(100,100);
            }
            player=new MediaPlayer();
            player.setVolume(0,0);
            player.setLooping(this.isLoop);
            this.player.setDataSource(this.mediaPath);
            this.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.player.prepare();
            player.setDisplay(surfaceHolder);
            if(this.isLoop)
                startVideo();
            else
                this.surfaceView.setBackground(this.getVedioPicture(this.mediaPath,this.currentIndex));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void relesePlayer()
    {
        if(this.player!=null)
            this.player.release();
    }
    public boolean getPlayerSate()
    {
        if(this.player!=null)
            return this.player.isPlaying();
        return false;
    }

    public String getMediaPath() {
        return mediaPath;
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
        public void surfaceDestroyed(SurfaceHolder holder)
        {
            isSurfaceCreated=false;
            if(player.isPlaying())
            {
                currentIndex=player.getCurrentPosition();
                player.stop();
            }
        }
    }
}
