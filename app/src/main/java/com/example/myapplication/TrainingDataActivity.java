package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;

import java.text.DecimalFormat;
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

    private TextView textView_week;
    private TextView textView_month;
    private TextView textView_year;
    private TextView textView_aver_speed;
    private TextView textView_max_speed;
    private TextView textView_energy;

    private final int SELECTDATE =0;
    private int currentChoose=DateTool.WEEK;

    private String currentDate=DateTool.getCurrentDate();
    private DataCache dataCache=new DataCache(30);
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
            setDataForManager("击球数",combinedChartManager_week,DateTool.WEEK);

            floatList.clear();
            CombinedChart combinedChart3 =(CombinedChart) findViewById(R.id.combinedChart3);
            combinedChartManager_month=new CombinedChartManager(combinedChart3);
            setDataForManager("击球数",combinedChartManager_month,DateTool.MONTH);

            floatList.clear();
            CombinedChart combinedChart4 =(CombinedChart) findViewById(R.id.combinedChart4);
            combinedChartManager_year=new CombinedChartManager(combinedChart4);
            setDataForManager("击球数",combinedChartManager_year,DateTool.YEAR);

            TabHost tabHost=(TabHost)findViewById(R.id.tabhost);
            tabHost.setup();

            TabHostRender(new int[]{R.id.tab2, R.id.tab3, R.id.tab4},
                    new int[]{R.drawable.week,R.drawable.week_click,
                            R.drawable.month,R.drawable.month_click,R.drawable.year,R.drawable.year_click,},tabHost);

            arcProgress=(ArcProgress) findViewById(R.id.arcProgress1);
            arcProgress.setMode(ArcProgress.SPORTTIME);
            arcProgress.setTask(14);

            arcProgress1=(ArcProgress) findViewById(R.id.arcProgress2);
            arcProgress1.setMode(ArcProgress.HITCOUNTER);
            arcProgress1.setTask(2100);

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
                                setDataForManager("击球数",combinedChartManager_week,DateTool.WEEK);
                                text="击球数/时间";
                                size=3;
                            }
                            else
                            {
                                setDataForManager("时间",combinedChartManager_week,DateTool.WEEK);
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
                                setDataForManager("击球数",combinedChartManager_month,DateTool.MONTH);
                                text="击球数/时间";
                                size=3;
                            }
                            else
                            {
                                setDataForManager("时间",combinedChartManager_month,DateTool.MONTH);
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
                                setDataForManager("击球数",combinedChartManager_year,DateTool.YEAR);
                                text="击球数/时间";
                                size=3;
                            }
                            else
                            {
                                setDataForManager("时间",combinedChartManager_year,DateTool.YEAR);
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

            this.textView_week=(TextView)findViewById(R.id.textView_chartTitle2);
            this.textView_month=(TextView)findViewById(R.id.textView_chartTitle3);
            this.textView_year=(TextView)findViewById(R.id.textView_chartTitle4);

            this.textView_week=(TextView)findViewById(R.id.textView_chartTitle2);
            this.textView_week.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent();
                            intent.setClass(TrainingDataActivity.this,CalendarActivity.class);
                            startActivityForResult(intent, SELECTDATE);
                        }
                    }
            );

            this.textView_month=(TextView)findViewById(R.id.textView_chartTitle3);
            this.textView_month.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent();
                            intent.setClass(TrainingDataActivity.this,CalendarActivity.class);
                            startActivityForResult(intent, SELECTDATE);
                        }
                    }
            );

            this.textView_year=(TextView)findViewById(R.id.textView_chartTitle4);
            this.textView_year.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent();
                            intent.setClass(TrainingDataActivity.this,CalendarActivity.class);
                            startActivityForResult(intent, SELECTDATE);
                        }
                    }
            );

            final int weekNumber=DateTool.calculateWeekNumber(currentDate);
            final int monthNumber=DateTool.getDateMessage(currentDate,DateTool.MONTH);
            final int yearNumber=DateTool.getDateMessage(currentDate,DateTool.YEAR);
            this.textView_week.setText("▼ 第"+weekNumber+"周");
            this.textView_month.setText("▼ "+monthNumber+"月");
            this.textView_year.setText("▼ "+yearNumber+"年");

            this.textView_aver_speed=(TextView)findViewById(R.id.textView_aver_speed);
            this.textView_max_speed=(TextView)findViewById(R.id.textView_max_speed);
            this.textView_energy=(TextView)findViewById(R.id.textView_energy);
            updateTextView();
            updateArcProgress(arcProgress, "时间",currentDate);
            updateArcProgress(arcProgress1, "击球数",currentDate);
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
    private void setDataForManager(String descripton,CombinedChartManager combinedChartManager,int type)
    {
        String colName=descripton.equals("时间")?"sport_time":"hit";
        List <Float> floatList=null;
        String dataCacheId=null;
        switch (type)
        {
            case DateTool.WEEK:
                String firstDayOfWeek=DateTool.getFirstDayOfWeek(currentDate);
                dataCacheId=Main2Activity.databaseService.getUser_id()+DateTool.WEEK+colName+firstDayOfWeek;
                if(this.dataCache.isEntryContained(dataCacheId))
                    floatList=(ArrayList<Float>)dataCache.getData(dataCacheId);
                else
                    floatList=searchDataFromDatabase(DateTool.WEEK,descripton,currentDate);
                break;
            case DateTool.MONTH:
                String firstDayOfMonth=DateTool.getFirstDayOfMonth(currentDate);
                dataCacheId=Main2Activity.databaseService.getUser_id()+DateTool.MONTH+colName+firstDayOfMonth;
                if(this.dataCache.isEntryContained(dataCacheId))
                    floatList=(ArrayList<Float>)dataCache.getData(dataCacheId);
                else
                    floatList=searchDataFromDatabase(DateTool.MONTH,descripton,currentDate);
                break;
            case DateTool.YEAR:
                int year=DateTool.getDateMessage(currentDate,DateTool.YEAR);
                dataCacheId=Main2Activity.databaseService.getUser_id()+DateTool.YEAR+colName+year;
                if(this.dataCache.isEntryContained(dataCacheId))
                    floatList=(ArrayList<Float>)dataCache.getData(dataCacheId);
                else
                    floatList=searchDataFromDatabase(DateTool.YEAR,descripton,currentDate);
                break;
        }
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
            if (requestCode == SELECTDATE)
            {
                //Main2Activity.databaseService.getAllFromDailyRecord();
                final String message=intent.getExtras().getString("date");
                final int weekNumber=DateTool.calculateWeekNumber(message);
                final int monthNumber=DateTool.getDateMessage(message,DateTool.MONTH);
                final int yearNumber=DateTool.getDateMessage(message,DateTool.YEAR);
                this.textView_week.setText("▼ 第"+weekNumber+"周");
                this.textView_month.setText("▼ "+monthNumber+"月");
                this.textView_year.setText("▼ "+yearNumber+"年");
                this.currentDate=message;
                setDataForManager(combinedChartManager_week.getDataDescription(),combinedChartManager_week,DateTool.WEEK);
                setDataForManager(combinedChartManager_month.getDataDescription(),combinedChartManager_month,DateTool.MONTH);
                setDataForManager(combinedChartManager_year.getDataDescription(),combinedChartManager_year,DateTool.YEAR);
                updateArcProgress(arcProgress, "时间",currentDate);
                updateArcProgress(arcProgress1, "击球数",currentDate);
                updateTextView();
            }
        }
    }
    private List<Float> searchDataFromDatabase(int type,String description,String date)
    {
        List<Float> floatList=new ArrayList<Float>();
        String colName=description.equals("时间")?"sport_time":"hit";
        String dataCacheId;
        switch (type)
        {
            case DateTool.WEEK:
                String firstDayOfWeek=DateTool.getFirstDayOfWeek(date);
                String lastDayOfWeek=DateTool.getLastDayOfWeek(date);
                floatList=Main2Activity.databaseService.
                        getFloatDataFromDailyRecordByDate(firstDayOfWeek, lastDayOfWeek,colName);
                dataCacheId=Main2Activity.databaseService.getUser_id()
                        +type+colName+firstDayOfWeek;
                if(floatList.size()==0)
                    for(int i=0;i<7;i++)
                        floatList.add(0f);
                this.dataCache.addEntry(dataCacheId,floatList,System.currentTimeMillis());
                break;
            case DateTool.MONTH:
                String firstDayOfMonth=DateTool.getFirstDayOfMonth(date);
                String lastDayOfMonth=DateTool.getLastDayOfMonth(date);
                floatList=Main2Activity.databaseService.
                        getFloatDataFromDailyRecordByDate(firstDayOfMonth, lastDayOfMonth,colName);
                dataCacheId=Main2Activity.databaseService.getUser_id()
                        +type+colName+firstDayOfMonth;
                if(floatList.size()==0)
                    for(int i=0;i<DateTool.getDaysOfMonth(
                            DateTool.getDateMessage(currentDate,DateTool.YEAR),DateTool.getDateMessage(currentDate,DateTool.MONTH));i++)
                        floatList.add(0f);
                this.dataCache.addEntry(dataCacheId,floatList,System.currentTimeMillis());
                break;
            case DateTool.YEAR:
                floatList=new ArrayList<Float>();
                int year=DateTool.getDateMessage(date,DateTool.YEAR);
                for(int i=1;i<=12;i++)
                {
                    String time=year+"-"+(i<10?"0"+i:i)+"-01";
                    float ans=Main2Activity.databaseService.searchDataByTime(
                            DateTool.getFirstDayOfMonth(time),DateTool.getLastDayOfMonth(time),colName
                    );
                    floatList.add(ans);
                }
                dataCacheId=Main2Activity.databaseService.getUser_id()
                        +type+colName+year;
                if(floatList.size()==0)
                    for(int i=0;i<12;i++)
                        floatList.add(0f);
                this.dataCache.addEntry(dataCacheId,floatList,System.currentTimeMillis());
                break;
        }
        return floatList;
    }
    private void updateTextView()
    {
        String firstDayOfWeek,lastDayOfWeek;
        float ans;
        switch (currentChoose)
        {
            case DateTool.WEEK:
                firstDayOfWeek=DateTool.getFirstDayOfWeek(currentDate);
                lastDayOfWeek=DateTool.getLastDayOfWeek(currentDate);
                ans=Main2Activity.databaseService.calculateEnergy(firstDayOfWeek,lastDayOfWeek);
                textView_energy.setText(DensityUtil.floatPrecision(2,ans)+"Cal");
                ans=Main2Activity.databaseService.getAverageSpeedBetweenDate(firstDayOfWeek,lastDayOfWeek);
                textView_aver_speed.setText(DensityUtil.floatPrecision(2,ans)+"m/s");
                ans=Main2Activity.databaseService.getMaxSpeedBetweenDate(firstDayOfWeek,lastDayOfWeek);
                textView_max_speed.setText(DensityUtil.floatPrecision(2,ans)+"m/s");
                break;
            case DateTool.MONTH:
                firstDayOfWeek=DateTool.getFirstDayOfMonth(currentDate);
                lastDayOfWeek=DateTool.getLastDayOfMonth(currentDate);
                ans=Main2Activity.databaseService.calculateEnergy(firstDayOfWeek,lastDayOfWeek);
                textView_energy.setText(DensityUtil.floatPrecision(2,ans)+"Cal");
                ans=Main2Activity.databaseService.getAverageSpeedBetweenDate(firstDayOfWeek,lastDayOfWeek);
                textView_aver_speed.setText(DensityUtil.floatPrecision(2,ans)+"m/s");
                ans=Main2Activity.databaseService.getMaxSpeedBetweenDate(firstDayOfWeek,lastDayOfWeek);
                textView_max_speed.setText(DensityUtil.floatPrecision(2,ans)+"m/s");
                break;
            case DateTool.YEAR:
                firstDayOfWeek=DateTool.getFirstDayOfYear(currentDate);
                lastDayOfWeek=DateTool.getLastDayOfYear(currentDate);
                ans=Main2Activity.databaseService.calculateEnergy(firstDayOfWeek,lastDayOfWeek);
                textView_energy.setText(DensityUtil.floatPrecision(2,ans)+"Cal");
                ans=Main2Activity.databaseService.getAverageSpeedBetweenDate(firstDayOfWeek,lastDayOfWeek);
                textView_aver_speed.setText(DensityUtil.floatPrecision(2,ans)+"m/s");
                ans=Main2Activity.databaseService.getMaxSpeedBetweenDate(firstDayOfWeek,lastDayOfWeek);
                textView_max_speed.setText(DensityUtil.floatPrecision(2,ans)+"m/s");
                break;
        }
    }
    private void updateArcProgress(ArcProgress arcProgress,String description,String date)
    {
        int type=currentChoose;
        String colName=description.equals("时间")?"sport_time":"hit";
        String dataCacheId=Main2Activity.databaseService.getUser_id()
                +type+colName+date;
        List<Float> floatList=(ArrayList<Float>)this.dataCache.getData(dataCacheId);
        if(floatList==null)
        {
            floatList=searchDataFromDatabase(type,description,date);
            dataCache.addEntry(dataCacheId,floatList,System.currentTimeMillis());
        }
        float ans=0;
        for(float x:floatList)
            ans+=x;
        arcProgress.updateMiddle(ans);
    }
    private void TabHostRender(int[]  pageIDs, final int images[], final TabHost tabHost)
    {
        if(images.length/2!=pageIDs.length)
            return ;
        final Resources resources = this .getResources();
        for(int i=0;i<pageIDs.length;i++)
        {
            TabHost.TabSpec page = tabHost.newTabSpec(i+"")
                    .setIndicator(new TextView(this))
                    .setContent(pageIDs[i]);
            tabHost.addTab(page);
        }
        tabHost.setOnTabChangedListener(
                new TabHost.OnTabChangeListener() {
                    @Override
                    public void onTabChanged(String tabId) {
                        try
                        {
                            switch (tabId)
                            {
                                case "0":
                                    currentChoose=DateTool.WEEK;
                                    break;
                                case "1":
                                    currentChoose=DateTool.MONTH;
                                    break;
                                case "2":
                                    currentChoose=DateTool.YEAR;
                                    break;
                            }
                            updateArcProgress(arcProgress, "时间",currentDate);
                            updateArcProgress(arcProgress1, "击球数",currentDate);
                            updateTextView();
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
                            e.printStackTrace();
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
