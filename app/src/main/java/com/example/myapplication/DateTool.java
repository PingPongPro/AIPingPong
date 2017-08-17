package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by 叶明林 on 2017/8/12.
 */

public class DateTool {
    public static final int WEEK=0;
    public static final int MONTH=1;
    public static final int YEAR=2;
    //获得指定日期该周的周一的日期
    public static String getFirstDayOfWeek(String date)
    {
        int weekNumber=dayForWeek(date);
        if(weekNumber==7)
            return date;
        String ans=date;
        ans=nextOrLastDay(ans,-weekNumber);
        return ans;
    }
    //获得指定日期该周的周日的日期
    public static String getLastDayOfWeek(String date)
    {
        int weekNumber=dayForWeek(date);
        String ans=date;
        ans=nextOrLastDay(ans,6-weekNumber);
        return ans;
    }
    //获得指定日期的月头
    public static String getFirstDayOfMonth(String date)
    {
        try
        {
            int monthNumber=getDateMessage(date,DateTool.MONTH);
            String ans=getDateMessage(date,DateTool.YEAR)+"-"+
                    (monthNumber<10?"0"+monthNumber:monthNumber)+"-"+"01";
            return ans;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    //获得指定日期的月尾
    public static String getLastDayOfMonth(String date)
    {
        try
        {
            int yearNumber=getDateMessage(date,DateTool.YEAR);
            int monthNumber=getDateMessage(date,DateTool.MONTH);
            int lastDay=getDaysOfMonth(yearNumber,monthNumber);
            String ans=yearNumber+"-"+
                    (monthNumber<10?"0"+monthNumber:monthNumber)+"-"+lastDay;
            return ans;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    //获得指定日期的年头
    public static String getFirstDayOfYear(String date)
    {
        try
        {
            String ans=getDateMessage(date,DateTool.YEAR)+"-01-01";
            return ans;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    //获得指定日期的年尾
    public static String getLastDayOfYear(String date)
    {
        try
        {
            int yearNumber=getDateMessage(date,DateTool.YEAR);
            String ans=yearNumber+"-12-31";
            return ans;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
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
    //计算指定日期位于该年份的第几周
    public static int calculateWeekNumber(String date)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date newTime=format.parse(date);
            String time=(newTime.getYear()+1900)+"-01-01";
            Date oldTime=format.parse(time);
            int dif=(int) ((newTime.getTime() - oldTime.getTime()) / (1000*3600*24))+1;
            if(dif<=7)
                return 1;
            else
            {
                int week_oldTime=dayForWeek(time);
                int week_newTime=dayForWeek(date);
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


    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前日期是该月的第几天
     *
     * @return
     */
    public static int getCurrentDayOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前日期是该周的第几天
     *
     * @return
     */
    public static int getCurrentDayOfWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当前是几点
     */
    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);//二十四小时制
    }

    /**
     * 获取当前是几分
     *
     * @return
     */
    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒
     *
     * @return
     */
    public static int getSecond() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }

    /**
     * 根据传入的年份和月份，获取当前月份的日历分布
     *
     * @param year
     * @param month
     * @return
     */
    public static int[][] getDayOfMonthFormat(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);//设置时间为每月的第一天
        //设置日历格式数组,6行7列
        int days[][] = new int[6][7];
        //设置该月的第一天是周几
        int daysOfFirstWeek = calendar.get(Calendar.DAY_OF_WEEK);
        //设置本月有多少天
        int daysOfMonth = getDaysOfMonth(year, month);
        //设置上个月有多少天
        int daysOfLastMonth = getLastDaysOfMonth(year, month);
        int dayNum = 1;
        int nextDayNum = 1;
        //将日期格式填充数组
        for (int i = 0; i < days.length; i++) {
            for (int j = 0; j < days[i].length; j++) {
                if (i == 0 && j < daysOfFirstWeek - 1) {
                    days[i][j] = daysOfLastMonth - daysOfFirstWeek + 2 + j;
                } else if (dayNum <= daysOfMonth) {
                    days[i][j] = dayNum++;
                } else {
                    days[i][j] = nextDayNum++;
                }
            }
        }
        return days;
    }

    /**
     * 根据传入的年份和月份，判断上一个有多少天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getLastDaysOfMonth(int year, int month) {
        int lastDaysOfMonth = 0;
        if (month == 1) {
            lastDaysOfMonth = getDaysOfMonth(year - 1, 12);
        } else {
            lastDaysOfMonth = getDaysOfMonth(year, month - 1);
        }
        return lastDaysOfMonth;
    }

    /**
     * 根据传入的年份和月份，判断当前月有多少天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDaysOfMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                if (isLeap(year)) {
                    return 29;
                } else {
                    return 28;
                }
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
        }
        return -1;
    }

    /**
     * 判断是否为闰年
     *
     * @param year
     * @return
     */
    public static boolean isLeap(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        }
        return false;
    }
    public static int getDateMessage(String date,int mode)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date time=format.parse(date);
            switch (mode)
            {
                case WEEK:
                    return calculateWeekNumber(date);
                case MONTH:
                    return time.getMonth()+1;
                case YEAR:
                    return time.getYear()+1900;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return -1;
    }
    //计算相邻日期，+1代表下一天，-1代表上一天
    public static String nextOrLastDay(String time, int number)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date oldTime=format.parse(time);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(oldTime);
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)+number);
            return format.format(calendar.getTime());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static int daysBetween(String date1,String date2)
    {
        try
        {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Date fdate=sdf.parse(date1);
            Date ldate=sdf.parse(date2);
            Calendar cal = Calendar.getInstance();
            cal.setTime(fdate);
            long time1 = cal.getTimeInMillis();
            cal.setTime(ldate);
            long time2 = cal.getTimeInMillis();
            long between_days=(time2-time1)/(1000*3600*24);
            return Integer.parseInt(String.valueOf(between_days))+1;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 1;
    }
}
