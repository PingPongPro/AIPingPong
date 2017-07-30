package com.example.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 叶明林 on 2017/7/21.
 */

public class DataChartManager extends Thread{
    private DataChart datachart;
    private List<String> dataName=new ArrayList<String>();
    private List<Integer> dataColor=new ArrayList<Integer>();
    private Timer timer = new Timer();
    private List<View.OnClickListener> listeners=new ArrayList<View.OnClickListener>();
    private TimerTask task;
    private boolean isPassingData=false;
    private final int XRangeMaximum=100;
    private Activity activity;
    private ReadDataFromFile dataFile;
    public DataChartManager(LineChart chart,String dataPath,String[] name,int[] color,Activity a)
    {
        this.activity=a;
        this.dataFile=new ReadDataFromFile(dataPath,true);
        this.task=new TaskForChart();
        //TODO 图像名称
        for(int i=0;i<name.length;i++)
            this.dataName.add(name[i]);
        //TODO 图像颜色
        for(int i=0;i<color.length;i++)
            this.dataColor.add(color[i]);
        this.datachart=new DataChart(chart,this.dataName,this.dataColor);
        //TODO 图像相关设置
        this.datachart.setDescription("");
        //TODO 生成监听器
        for(int i=0;i<name.length;i++)
        {
            final int count=i;
            this.listeners.add(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            datachart.chooseIndex(count);
                        }
                    }
            );
        }
    }
    public List<View.OnClickListener> getListeners()
    {
        return this.listeners;
    }

    public void setPassingData(boolean passingData) {
        isPassingData = passingData;
    }
    public boolean isPassingData() {
        return isPassingData;
    }

    @Override
    public void run()
    {
        isPassingData=true;
        timer = new Timer();
        timer.schedule(task, 20,20);
    }
    private class DataChart
    {
        private LineChart lineChart;
        private YAxis leftAxis;
        private YAxis rightAxis;
        private XAxis xAxis;
        private LineData lineData;
        private LineDataSet lineDataSet;
        private List<ILineDataSet> lineDataSets = new ArrayList<ILineDataSet>();
        private boolean lineVisiable[]={true,true,true};
        //一条曲线
    /*public DataChartManager(LineChart mLineChart, String name, int color) {
        this.lineChart = mLineChart;
        //leftAxis = lineChart.getAxisLeft();
        lineChart.getAxisLeft().setEnabled(false);
        xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false);
        initLineChart();
        initLineDataSet(name, color);
    }*/

        //多条曲线
        public DataChart(LineChart mLineChart, List<String> names, List<Integer> colors) {
            this.lineChart = mLineChart;
            lineChart.getAxisLeft().setEnabled(false);
            xAxis = lineChart.getXAxis();
            xAxis.setLabelCount(50,true);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawLabels(false);
            xAxis.setDrawGridLines(false);
            //xAxis.setEnabled(false);
            initLineChart();
            initLineDataSet(names, colors);
        }

        /**
         * 初始化LineChar
         */
        private void initLineChart() {

            lineChart.setDrawGridBackground(false);
            //显示边界
            lineChart.setDrawBorders(true);
            //折线图例 标签 设置
            Legend legend = lineChart.getLegend();
            legend.setForm(Legend.LegendForm.LINE);
            legend.setTextSize(11f);
            //显示位置
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);

            //保证Y轴从0开始，不然会上移一点
            //leftAxis.setAxisMinimum(0f);
            //rightAxis.setAxisMinimum(0f);
        }

        /**
         * 初始化折线(一条线)
         *
         * @param name
         * @param color
         */
    /*private void initLineDataSet(String name, int color) {

        lineDataSet = new LineDataSet(null, name);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(1.5f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setHighLightColor(color);
        //设置曲线填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();

    }*/
        /**
         * 动态添加数据（一条折线图）
         *
         * @param number
         */
    /*public void addEntry(double number) {

        //最开始的时候才添加 lineDataSet（一个lineDataSet 代表一条线）
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        lineChart.setData(lineData);
        System.out.println(number+" "+lineDataSet.getEntryCount());
        Entry entry = new Entry(lineDataSet.getEntryCount() ,(float)number);
        lineData.addEntry(entry, 0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置在曲线图中显示的最大数量
        lineChart.setVisibleXRangeMaximum(250);
        //移到某个位置
        lineChart.moveViewToX(lineData.getEntryCount() - 5);
        leftAxis.resetAxisMaximum();
    }*/

        /**
         * 初始化折线（多条线）
         *
         * @param names
         * @param colors
         */
        private void initLineDataSet(List<String> names, List<Integer> colors) {

            for (int i = 0; i < names.size(); i++) {
                lineDataSet = new LineDataSet(null, names.get(i));
                lineDataSet.setColor(colors.get(i));
                lineDataSet.setLineWidth(1f);
                lineDataSet.setDrawCircles(false);
                //lineDataSet.setCircleRadius(1.5f);
                lineDataSet.setColor(colors.get(i));

                lineDataSet.setDrawFilled(true);
                lineDataSet.setCircleColor(colors.get(i));
                lineDataSet.setHighLightColor(colors.get(i));
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet.setValueTextSize(10f);
                lineDataSets.add(lineDataSet);
            }
            //添加一个空的 LineData
            lineData = new LineData();
            lineChart.setData(lineData);
            lineChart.invalidate();
            lineChart.setTouchEnabled(false);

            preAddData(250);
        }
        private void preAddData(int len)
        {
            if (lineDataSets.get(0).getEntryCount() == 0) {
                lineData = new LineData(lineDataSets);
            }
            lineChart.setData(this.lineData);
            for(int j=0;j<len;j++)
            {
                for (int i = 0; i < lineDataSets.size(); i++) {
                    Entry entry = new Entry(lineDataSet.getEntryCount(), 0.0f);
                    lineData.addEntry(entry, i);
                }
            }
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(XRangeMaximum);
            lineChart.moveViewToX(lineData.getEntryCount() - 5);
        }
        /**
         * 动态添加数据（多条折线图）
         *
         * @param numbers
         */
        public void addEntry(List<Double> numbers) {
            if (lineDataSets.get(0).getEntryCount() == 0) {
                lineData = new LineData(lineDataSets);
            }
            lineChart.setData(this.lineData);
            for (int i = 0; i < numbers.size(); i++) {
                Entry entry = new Entry(lineDataSet.getEntryCount(), numbers.get(i).floatValue());
                lineData.addEntry(entry, i);
                lineData.notifyDataChanged();
                lineChart.notifyDataSetChanged();
                lineChart.setVisibleXRangeMaximum(XRangeMaximum);
                lineChart.moveViewToX(lineData.getEntryCount() - 5);
            }
        }
        public void chooseIndex(int index)
        {
            if(index>=0&&index<3)
            {
                this.lineVisiable[index]=!this.lineVisiable[index];
                lineDataSets.get(index).setVisible(this.lineVisiable[index]);
            }
        }
        /**
         * 设置Y轴值
         *
         * @param max
         * @param min
         * @param labelCount
         */
        public void setYAxis(float max, float min, int labelCount) {
            if (max < min) {
                return;
            }
            //leftAxis.setAxisMaximum(max);
            //leftAxis.setAxisMinimum(min);
            //leftAxis.setLabelCount(labelCount, false);

            rightAxis.setAxisMaximum(max);
            rightAxis.setAxisMinimum(min);
            rightAxis.setLabelCount(labelCount, false);
            lineChart.invalidate();
        }

        /**
         * 设置高限制线
         *
         * @param high
         * @param name
         */
        public void setHightLimitLine(float high, String name, int color) {
            if (name == null) {
                name = "高限制线";
            }
            LimitLine hightLimit = new LimitLine(high, name);
            hightLimit.setLineWidth(4f);
            hightLimit.setTextSize(10f);
            hightLimit.setLineColor(color);
            hightLimit.setTextColor(color);
            leftAxis.addLimitLine(hightLimit);
            lineChart.invalidate();
        }

        /**
         * 设置低限制线
         *
         * @param low
         * @param name
         */
        public void setLowLimitLine(int low, String name) {
            if (name == null) {
                name = "低限制线";
            }
            LimitLine hightLimit = new LimitLine(low, name);
            hightLimit.setLineWidth(4f);
            hightLimit.setTextSize(10f);
            leftAxis.addLimitLine(hightLimit);
            lineChart.invalidate();
        }

        /**
         * 设置描述信息
         *
         * @param str
         */
        public void setDescription(String str) {
            Description description = new Description();
            description.setText(str);
            lineChart.setDescription(description);
            lineChart.invalidate();
        }
    }
    public void pauseChart()
    {
        this.isPassingData=false;
    }
    //start or restart
    public void startChart()
    {
        this.isPassingData=true;
    }
    public void resetChart(String newPath)
    {
        this.pauseChart();
        this.dataFile.closeAllReaders();
        this.dataFile=new ReadDataFromFile(newPath,true);
    }
    private class TaskForChart extends TimerTask
    {
        private List<Double> dataList=new ArrayList<Double>();
        private int counter=0;
        @Override
        public void run()
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(isPassingData)
                    {
                        try
                        {
                            List<Object> list=dataFile.nextData(new int[]{0,1,2},"\t");
                            if(!dataFile.endFlag)
                            {
                                if(counter%10==0)
                                {
                                    dataList.add(Double.valueOf(list.get(0).toString()));
                                    dataList.add(Double.valueOf(list.get(1).toString()));
                                    dataList.add(Double.valueOf(list.get(2).toString()));
                                    datachart.addEntry(dataList);
                                    dataList.clear();
                                }
                                counter++;
                            }
                            else
                            {
                                if(counter%10==0)
                                {
                                    for(int i=0;i<3;i++)
                                        dataList.add(0.0);
                                    datachart.addEntry(dataList);
                                    dataList.clear();
                                }
                                counter++;
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

}
