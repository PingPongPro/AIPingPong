package com.example.myapplication;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 叶明林 on 2017/7/29.
 */

public class TabRender {
    public static void TabHostRender(int[]  pageIDs, final int images[], final TabHost tabHost, final Activity activity)
    {
        if(images.length/2!=pageIDs.length)
            return ;
        final Resources resources = activity .getResources();
        for(int i=0;i<pageIDs.length;i++)
        {
            TabHost.TabSpec page = tabHost.newTabSpec(i+"")
                    .setIndicator(new TextView(activity))
                    .setContent(pageIDs[i]);
            tabHost.addTab(page);
        }
        tabHost.setOnTabChangedListener(
                new TabHost.OnTabChangeListener() {
                    @Override
                    public void onTabChanged(String tabId) {
                        try
                        {
                            int maxCount=tabHost.getTabWidget().getChildCount();
                            TabWidget tabWidget=tabHost.getTabWidget();
                            for(int i=0;i<maxCount;i++)
                            {
                                if(i==tabHost.getCurrentTab())
                                    continue;
                                Drawable drawable=resources.getDrawable(images[2*i]);
                                tabWidget.getChildAt(i).setBackground(drawable);
                            }
                            int currenIndex=tabHost.getCurrentTab();
                            Drawable drawable =resources.getDrawable(images[2*currenIndex+1]);
                            tabWidget.getChildAt(currenIndex).setBackground(drawable);
                        }
                        catch(Exception e)
                        {

                        }

                    }
                }
        );
        int maxCount=tabHost.getTabWidget().getChildCount();
        for(int i=1;i<maxCount;i++)
        {
            Drawable drawable = resources.getDrawable(images[2*i]);
            tabHost.getTabWidget().getChildAt(i).setBackground(drawable);
        }
        Drawable drawable =resources.getDrawable(images[1]);
        tabHost.getTabWidget().getChildAt(0).setBackground(drawable);
    }
}
