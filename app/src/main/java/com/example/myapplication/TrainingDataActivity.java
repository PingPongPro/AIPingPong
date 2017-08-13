package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by 叶明林 on 2017/8/11.
 */

public class TrainingDataActivity extends AppCompatActivity {
    private ArcProgress arcProgress;
    private ArcProgress arcProgress1;

    private CombinedChartManager combinedChartManager_week;
    private CombinedChartManager combinedChartManager_month;
    private CombinedChartManager combinedChartManager_year;

    private final int SELECTWEEK=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new myException());
        setContentView(R.layout.trainingdata_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_train);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Resources resources=this.getResources();
        //Drawable drawable=resources.getDrawable(R.drawable.bluetooth);
        //getSupportActionBar().setHomeAsUpIndicator(drawable);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        try
        {
//            BarChart barChart=(BarChart)findViewById(R.id.barChart);
//            BarChartManager barChartManager=new BarChartManager(barChart);
//            List<Float> floatList=new ArrayList<Float>();
//            Random random=new Random();
//            for(int i=1;i<=19;i++)
//                floatList.add(random.nextFloat()*50);
//            barChartManager.setData(floatList);
            List<Float> floatList=new ArrayList<Float>();
            Random random=new Random();

            floatList.clear();
            CombinedChart combinedChart2 =(CombinedChart) findViewById(R.id.combinedChart2);
            combinedChartManager_week=new CombinedChartManager(combinedChart2);
            for(int i=1;i<=7;i++)
                floatList.add(random.nextFloat()*50);
            combinedChartManager_week.setData("击球数",floatList);

            floatList.clear();
            CombinedChart combinedChart3 =(CombinedChart) findViewById(R.id.combinedChart3);
            combinedChartManager_month=new CombinedChartManager(combinedChart3);
            for(int i=1;i<=30;i++)
                floatList.add(random.nextFloat()*50);
            combinedChartManager_month.setData("击球数",floatList);

            floatList.clear();
            CombinedChart combinedChart4 =(CombinedChart) findViewById(R.id.combinedChart4);
            combinedChartManager_year=new CombinedChartManager(combinedChart4);
            for(int i=1;i<=12;i++)
                floatList.add(random.nextFloat()*50);
            combinedChartManager_year.setData("击球数",floatList);


            TabHost tabHost=(TabHost)findViewById(R.id.tabhost);
            tabHost.setup();

            TabRender.TabHostRender(new int[]{R.id.tab2, R.id.tab3, R.id.tab4},
                    new int[]{R.drawable.week,R.drawable.week_click,
                            R.drawable.month,R.drawable.month_click,R.drawable.year,R.drawable.year_click,},tabHost,this);

            arcProgress=(ArcProgress) findViewById(R.id.arcProgress1);
            arcProgress.setMode(ArcProgress.SPORTTIME);
            arcProgress.setTask(15);
            arcProgress.updateMiddle(12);

            arcProgress1=(ArcProgress) findViewById(R.id.arcProgress2);
            arcProgress1.setMode(ArcProgress.HITCOUNTER);
            arcProgress1.setTask(150);
            arcProgress1.updateMiddle(100);

            final TextView textView2=(TextView)findViewById(R.id.textView_change2);
            initTextView(textView2);
            textView2.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text="";
                            int size=0;
                            if(combinedChartManager_week.getDataDescription().equals("时间"))
                            {
                                setDataForManager("击球数",combinedChartManager_week);
                                text="击球数/时间";
                                size=3;
                            }
                            else
                            {
                                setDataForManager("时间",combinedChartManager_week);
                                text="时间/击球数";
                                size=2;
                            }
                            Spannable spannable=new SpannableString(text);
                            //spannable.setSpan(new TypefaceSpan("monospace"),0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#f1a75a")),
                                    0,size,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#AAAAAA")),
                                    size+1,6,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new AbsoluteSizeSpan((int)sp2px(14)),0,size,0);
                            spannable.setSpan(new AbsoluteSizeSpan((int)sp2px(12)),size+1,6,0);
                            textView2.setText(spannable);
                        }
                    }
            );

            final TextView textView3=(TextView)findViewById(R.id.textView_change3);
            initTextView(textView3);
            textView3.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text="";
                            int size=0;
                            if(combinedChartManager_month.getDataDescription().equals("时间"))
                            {
                                setDataForManager("击球数",combinedChartManager_month);
                                text="击球数/时间";
                                size=3;
                            }
                            else
                            {
                                setDataForManager("时间",combinedChartManager_month);
                                text="时间/击球数";
                                size=2;
                            }
                            Spannable spannable=new SpannableString(text);
                            //spannable.setSpan(new TypefaceSpan("monospace"),0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#f1a75a")),
                                    0,size,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#AAAAAA")),
                                    size+1,6,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new AbsoluteSizeSpan((int)sp2px(14)),0,size,0);
                            spannable.setSpan(new AbsoluteSizeSpan((int)sp2px(12)),size+1,6,0);
                            textView3.setText(spannable);
                        }
                    }
            );

            final TextView textView4=(TextView)findViewById(R.id.textView_change4);
            initTextView(textView4);
            textView4.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text="";
                            int size=0;
                            if(combinedChartManager_year.getDataDescription().equals("时间"))
                            {
                                setDataForManager("击球数",combinedChartManager_year);
                                text="击球数/时间";
                                size=3;
                            }
                            else
                            {
                                setDataForManager("时间",combinedChartManager_year);
                                text="时间/击球数";
                                size=2;
                            }
                            Spannable spannable=new SpannableString(text);
                            //spannable.setSpan(new TypefaceSpan("monospace"),0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#f1a75a")),
                                    0,size,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#AAAAAA")),
                                    size+1,6,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new AbsoluteSizeSpan((int)sp2px(14)),0,size,0);
                            spannable.setSpan(new AbsoluteSizeSpan((int)sp2px(12)),size+1,6,0);
                            textView4.setText(spannable);
                        }
                    }
            );

            TextView textView=(TextView)findViewById(R.id.textView_chartTitle2);
            textView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent();
                            intent.setClass(TrainingDataActivity.this,ComboBoxActivity.class);
                            intent.putExtra("Mode",ComboBoxActivity.WEEK);
                            intent.putExtra("Number",DateTool.calculateWeekNumber());
                            startActivityForResult(intent,SELECTWEEK);
                        }
                    }
            );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    private void initTextView(TextView textView)
    {
        final String message="击球数/时间";
        Spannable spannable=new SpannableString(message);
        //spannable.setSpan(new TypefaceSpan("monospace"),0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#f1a75a")),
                0,3,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#AAAAAA")),
                4,6,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan((int)sp2px(14)),0,3,0);
        spannable.setSpan(new AbsoluteSizeSpan((int)sp2px(12)),4,6,0);
        textView.setText(spannable);
    }
    private void setDataForManager(String descripton,CombinedChartManager combinedChartManager)
    {
        List<Float> floatList=new ArrayList<Float>();
        Random random=new Random();
        for(int i=1;i<=24;i++)
            floatList.add(random.nextFloat()*50);
        combinedChartManager.setData(descripton,floatList);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private float sp2px(float spValue) {
        final float fontScale = this.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == SELECTWEEK)
            {
                Main2Activity.databaseService.getAllFromDailyRecord();
                final String message=intent.getExtras().getString("String");
                final TextView textView=(TextView)findViewById(R.id.textView_chartTitle2);
                textView.setText(message);
            }
        }
    }
}
