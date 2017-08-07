package com.example.myapplication;

import android.graphics.Color;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 叶明林 on 2017/8/7.
 */

public class CombinedChartManager {
    private CombinedChart combinedChart;
    private List<IBarDataSet> dataSets=new ArrayList<IBarDataSet>();
    private int dataCount;
    //private XAxis YAxis;

    public CombinedChartManager(CombinedChart chart)
    {
        this.combinedChart =chart;
        this.combinedChart.getAxisLeft();
        combinedChart.getDescription().setEnabled(false);
        //combinedChart.setPinchZoom(true);
        combinedChart.setBackgroundColor(Color.parseColor("#D9D9D9"));           //背景颜色
        combinedChart.getXAxis().setDrawAxisLine(true);                          //显示x轴
        combinedChart.getXAxis().setAxisLineColor(Color.parseColor("#FF8C00")); //设置x轴颜色
        combinedChart.getXAxis().setAxisLineWidth(2f);                           //设置x轴宽度
        combinedChart.getXAxis().setTextColor(Color.parseColor("#FF8C00"));
        combinedChart.getXAxis().setDrawGridLines(false);
        combinedChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        combinedChart.getAxisLeft().setEnabled(false);
        combinedChart.getAxisRight().setEnabled(false);
        combinedChart.animateX(2000);
        combinedChart.setScaleEnabled(false);

        MyXFormatter myXFormatter=new MyXFormatter();
        combinedChart.getXAxis().setValueFormatter(myXFormatter);

        combinedChart.getAxisLeft().setAxisMinimum(-0.5f);
        combinedChart.getAxisRight().setAxisMinimum(-0.5f);
    }
    public void setData(List<Float> data)
    {
        this.dataCount=data.size();
        this.combinedChart.getLegend().setEnabled(false);
        CombinedData combinedData=new CombinedData();
        combinedData.setData(generateLineData(data,"折线图"));
        combinedData.setData(generateBarData(data,"柱状图"));

        combinedChart.setData(combinedData);
        combinedChart.invalidate();
    }
    private BarData generateBarData(List<Float> barValues, String barTitle)
    {

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(-1, 0));
        for (int i = 0 ; i < barValues.size(); ++i) {
            barEntries.add(new BarEntry(i, barValues.get(i)));
        }
        barEntries.add(new BarEntry(barValues.size(), 0));

        BarDataSet barDataSet = new BarDataSet(barEntries, barTitle);
        barDataSet.setColor(Color.parseColor("#FF8C00"));
        barDataSet.setValueTextColor(Color.rgb(159, 143, 186));
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setDrawValues(false);//不绘制线的数据

        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(10f);
        barData.setBarWidth(0.5f);
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return value+"";//String.format("0.00",value)
            }
        });

        return barData;
    }
    private LineData generateLineData(List<Float> lineValues, String lineTitle)
    {
        ArrayList<Entry> lineEntries = new ArrayList<>();

        for (int i = 0; i < lineValues.size(); ++i) {
            lineEntries.add(new Entry(i, lineValues.get(i)));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries, lineTitle);
        lineDataSet.setColor(Color.rgb(233, 196, 21));
        lineDataSet.setLineWidth(2.5f);//设置线的宽度
        lineDataSet.setCircleColor(Color.rgb(244, 219, 100));//设置圆圈的颜色
        lineDataSet.setCircleColorHole(Color.WHITE);//设置圆圈内部洞的颜色
        //lineDataSet.setValueTextColor(Color.rgb(254,116,139));
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);//设置线数据依赖于右侧y轴
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineData.setValueTextSize(10f);
        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return value+"";
            }
        });

        return lineData;
    }
    private class MyXFormatter implements IAxisValueFormatter
    {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value < 0 || value > dataCount-1)//使得两侧柱子完全显示
                return "";
            return (int) (value+1)+"";
        }
    }
}
