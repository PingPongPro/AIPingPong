package com.example.myapplication;

import java.io.File;
import java.io.FileWriter;

//写入txt
public class WriteDataToFile
{
    private String filePath;
    public WriteDataToFile(String path)
    {
        this.filePath=path;
        try
        {
            File file =new File(path);
            if(!file.exists())
                file.createNewFile();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void write(String value)
    {
        try
        {
            FileWriter fileWriter=new FileWriter(this.filePath,true);
            fileWriter.write(value);
            fileWriter.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
