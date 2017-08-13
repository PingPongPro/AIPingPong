package com.example.myapplication;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by 叶明林 on 2017/2/16.
 */

public class DatabaseService {
    private AIPingPongHelper dbOpenHelper;
    private String user_id;
     //通过构造方法，实例化DBOpenHelper
    public DatabaseService(Context context,String user) {
        dbOpenHelper = new AIPingPongHelper(context);
        this.user_id=user;
    }
    //删表
    public void dropTable(String taleName) {
        dbOpenHelper.getWritableDatabase().execSQL(
                "DROP TABLE IF EXISTS " + taleName);

    }
    //插入信息
    public void InsertDataToDailyRecord(String day,int hit,float aver_speed,float max_speed,int sport_time)
    {
        try
        {
            dbOpenHelper.getWritableDatabase().execSQL(
                    "insert into DailyRecord " +
                            "(user_id,day,hit,aver_speed,max_speed,sport_time) " +
                            "values" +
                            "(?,?,?,?,?,?)",
                    new Object[] {
                            this.user_id,day,hit,aver_speed,max_speed,sport_time
                    });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    //取得所有数据
    public void getAllFromDailyRecord()
    {
        Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
                "select * from DailyRecord",
                null);
        if(cursor.moveToFirst()==false)
            return;
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
        {
            Object[] keep = new Object[]{cursor.getString(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5),
            };
            for (Object x : keep)
                System.out.println(x.toString());
        }
    }
    /**
     * 计算表的大小
     * */
    public long getDataCount() {
        Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(
                "select count(*) from accData" , null);
        cursor.moveToFirst();
        return cursor.getLong(0);
    }
    public String getMinDayFromDatabase(String databaseName)
    {
        Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery(
                "select min(day) from "+databaseName , null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public void close() {
        dbOpenHelper.close();
    }
}
