package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.TabHost;

/**
 * Created by 叶明林 on 2017/7/29.
 */

public class TabManager{
    private TabHost tabHost;
    private Activity activity;
    public TabManager(Activity a,TabHost tabhost)//Context context
    {
        this.tabHost=tabhost;
        try
        {
            this.activity=a;
            Drawable drawable= ResourcesCompat.getDrawable(this.activity.getResources(), R.drawable.chart, null);
            TabHost.TabSpec page1 = tabHost.newTabSpec("tab1")
                    .setIndicator("",drawable)
                    .setContent(R.id.tab1);
            tabHost.addTab(page1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        TabHost.TabSpec page2 = tabHost.newTabSpec("tab2")
                .setIndicator("参数")
                .setContent(R.id.tab2);
        tabHost.addTab(page2);

        TabHost.TabSpec page3 = tabHost.newTabSpec("tab3")
                .setIndicator("视频模型")
                .setContent(R.id.tab3);
        tabHost.addTab(page3);
    }
}
