package com.example.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 叶明林 on 2017/7/29.
 */

public class TabManager{
    private TabHost tabHost;
    private Activity activity;
    private List<TextView> textViewList=new ArrayList<TextView>(2);
    //调用次数和textViewL的容量一致
    private View composeLayout(String s, int i)
    {
        LinearLayout layout = new LinearLayout(this.activity);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#f08f4a"));
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView imageview = new ImageView(this.activity);
        imageview.setImageResource(i);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,  
            LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 0, 0, 0);
        layout.addView(imageview, layoutParams);
        final TextView textView = new TextView(this.activity);
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine(true);
        textView.setText(s);
        textView.setTextSize(14);
        this.textViewList.add(textView);
        textView.setDuplicateParentStateEnabled(true);
        layout.addView(textView,  layoutParams);
        return layout;  
    }
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    public TabManager(Activity a, final TabHost tabhost)//Context context
    {
        this.tabHost=tabhost;
        try
        {
            this.activity=a;
            View view1=composeLayout("视频模型",R.drawable.video);
            TabHost.TabSpec page1 = tabHost.newTabSpec("tab3")
                    .setIndicator(view1)//
                    .setContent(R.id.tab3);

            tabHost.addTab(page1);
        }
        catch(Exception e)
        {

        }

        View view1=composeLayout("曲线图",R.drawable.chart);
        TabHost.TabSpec page3 = tabHost.newTabSpec("tab1")
                .setIndicator(view1)//
                .setContent(R.id.tab1);
        tabHost.addTab(page3);
        tabhost.setOnTabChangedListener(
                new TabHost.OnTabChangeListener() {
                    @Override
                    public void onTabChanged(String tabId) {
                        try
                        {
                            int maxCount=tabhost.getTabWidget().getChildCount();
                            TabWidget tabWidget=tabhost.getTabWidget();
                            for(int i=0;i<maxCount;i++)
                            {
                                tabWidget.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
                                TextView tv= textViewList.get(i);
                                tv.setTextColor(Color.parseColor("#000000"));
                            }
                            tabWidget.getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#f08f4a"));
                            TextView tv= textViewList.get(tabhost.getCurrentTab());
                            tv.setTextColor(Color.parseColor("#ffffff"));
                        }
                        catch(Exception e)
                        {

                        }

                    }
                }

        );
        int maxCount=tabhost.getTabWidget().getChildCount();
        for(int i=1;i<maxCount;i++)
        {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
            TextView tv= textViewList.get(i);
            tv.setTextColor(Color.parseColor("#000000"));
        }
        TextView tv= textViewList.get(0);
        tv.setTextColor(Color.parseColor("#ffffff"));
    }
}
