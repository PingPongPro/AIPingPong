package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by 叶明林 on 2017/8/12.
 */

public class DateTool {
    //计算指定日期是星期几
    public static int dayForWeek(String pTime)
    {
         try
         {
             SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
             int[] weekDays = { 7, 1, 2, 3, 4, 5, 6 };
             Calendar cal = Calendar.getInstance();
             Date datet = format.parse(pTime);
             cal.setTime(datet);
             int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
             if (w < 0)
                 w = 0;
             return weekDays[w];
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }
        return -1;
    }
    //获得今年1月1日至今的星期数
    public static int calculateWeekNumber()
    {
        try
        {
            String time=getYear(getCurrentDate())+"-01-10";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date oldTime=format.parse(time);
            final String currentDate=getCurrentDate();
            Date newTime=format.parse(currentDate);
            int dif=(int) ((newTime.getTime() - oldTime.getTime()) / (1000*3600*24))+1;
            if(dif<=7)
                return 1;
            else
            {
                int week_oldTime=dayForWeek(time);
                int week_newTime=dayForWeek(currentDate);
                return 2+(dif-(week_oldTime==7?7:7-week_oldTime)-(week_newTime==7?1:week_newTime+1))/7;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }
    public static String getYear(String time)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date keepTime=format.parse(time);
            return keepTime.getYear()+"";
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    //获得当前日期
    public static String getCurrentDate()
    {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);  
        return dateNowStr;
    }
}
