package com.example.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.widget.TabHost;

/**
 * Created by 叶明林 on 2017/7/29.
 */

public class TabManager extends FragmentActivity {
    private TabHost tabHost;
    public TabManager(TabHost tabhost)
    {
        this.tabHost=tabhost;
        TabHost.TabSpec page1 = tabHost.newTabSpec("tab1")
                .setIndicator("曲线图")
                .setContent(R.id.tab1);
        tabHost.addTab(page1);

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
