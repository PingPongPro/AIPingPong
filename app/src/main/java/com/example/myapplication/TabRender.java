package com.example.myapplication;

import android.app.Activity;
import android.graphics.Color;
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
    public static void TabHostRender(String[] titles,int[]  pageIDs,final TabHost tabHost,final Activity activity)
    {
        if(titles.length!=pageIDs.length)
            return ;
        final List<TextView> textViewList=new ArrayList<TextView>();
        for(int i=0;i<titles.length;i++)
        {
            TextView textView=new TextView(activity);
            //Resources resources=activity.getResources();
            //Drawable drawable=resources.getDrawable(R.drawable.shape_corner);
            //textView.setBackground(drawable);
            textView.setGravity(Gravity.CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(12);
            textView.setText(titles[i]);
            textViewList.add(textView);

            TabHost.TabSpec page = tabHost.newTabSpec(titles[i])
                    .setIndicator(textView)
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
                                tabWidget.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
                                TextView tv= textViewList.get(i);
                                tv.setTextColor(Color.parseColor("#F1A75A"));
                            }
                            tabWidget.getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#F1A75A"));
                            TextView tv= textViewList.get(tabHost.getCurrentTab());
                            tv.setTextColor(Color.parseColor("#ffffff"));
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
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
            TextView tv= textViewList.get(i);
            tv.setTextColor(Color.parseColor("#F1A75A"));
        }
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#F1A75A"));
        TextView tv= textViewList.get(0);
        tv.setTextColor(Color.parseColor("#ffffff"));
    }
}
