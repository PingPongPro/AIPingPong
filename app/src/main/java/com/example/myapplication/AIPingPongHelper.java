package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 叶明林 on 2017/2/15.
 */

public class AIPingPongHelper extends SQLiteOpenHelper {
    private static  final String DATABASE_NAME="AIPingPong.db";
    private static  final int SCHEME_VERSION=1;
    public AIPingPongHelper(Context context)
    {
        super(context,DATABASE_NAME,null,SCHEME_VERSION);
    }
    public void onCreate(SQLiteDatabase db)
    {
        final String struct=
                "(user_id varchar(20)," +
                    "day date NOT NULL,"+//  date(CURRENT_TIME,'localtime')//DEFAULT
                    "hit integer DEFAULT 0," +
                    "aver_speed float DEFAULT 0," +
                    "max_speed float DEFAULT 0," +
                    "sport_time integer DEFAULT 0" +
                    ",primary key(user_id,day)" +//
                    ")";
        //
        db.execSQL("CREATE TABLE IF NOT EXISTS DailyRecord" +struct);
        final String struct2=
                "(id integer primary key autoincrement," +
                        "user_id varchar(20) NOT NULL," +
                        "filename varchar(20) NOT NULL,"+
                        "hit integer DEFAULT 0," +
                        "aver_speed float DEFAULT 0," +
                        "max_speed float DEFAULT 0," +
                        "sport_time int DEFAULT 0)";
        db.execSQL("CREATE TABLE IF NOT EXISTS ContestRecord" +struct2);
    }
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
    {

    }
}
