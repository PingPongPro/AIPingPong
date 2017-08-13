package com.example.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/18.
 */
public class MediaEntity implements Serializable{

    public String fileName;
    public String filePath;
    public transient Bitmap bitmap;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
