package com.example.myapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by 叶明林 on 2017/7/21.
 */

public class ReadDataFromFile {
    private String dataPath;
    private int number;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private boolean state=false;       //是否忽略第一行
    public boolean endFlag=false;       //结束标志

    public ReadDataFromFile(String path,boolean s)
    {
        this.state=s;
        this.dataPath=path;
        number=0;
        this.openDataFile();
    }
    private boolean openDataFile()
    {
        try {
            String encoding="GBK";
            File file=new File(this.dataPath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                this.inputStreamReader = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                this.bufferedReader = new BufferedReader(this.inputStreamReader);
                //读第一行的解释数据
                if(this.state)
                    bufferedReader.readLine();
                return true;
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return false;
    }
    public double nextData(int index,String regx){
        try{
                String lineTxt = null;
                if((lineTxt = this.bufferedReader.readLine()) != null){
                    //以TAB分割
                    String []keep=lineTxt.split(regx);
                    return Double.valueOf(keep[index]);
                }
                else
                    this.endFlag=true;
        }
        catch (Exception e) {
            System.out.println("解析数据出错");
            e.printStackTrace();
        }
        this.endFlag=true;
        return 0;
    }
    public List<Object> nextData(int[] index, String regx){
        try{
            String lineTxt = null;
            List<Object> data=new ArrayList<Object>();
            if((lineTxt = this.bufferedReader.readLine()) != null){
                //以TAB分割
                String []keep=lineTxt.split(regx);
                for(int i=0,j=0;i<keep.length;i++)
                    if(index[j]==i)
                    {
                        data.add(keep[i]);
                        j++;
                    }
                return data;
            }
            else
                this.endFlag=true;
        }
        catch (Exception e) {
            System.out.println("解析数据出错");
            e.printStackTrace();
        }
        this.endFlag=true;
        return null;
    }
    public int getNumberOfData(){return number;}
}
