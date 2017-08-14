package com.example.myapplication;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 叶明林 on 2017/8/7.
 */

public class BarChartManager {
    private BarChart barChart;
    private List<IBarDataSet> dataSets=new ArrayList<IBarDataSet>();
    public BarChartManager(BarChart chart)
    {
        this.barChart=chart;
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setDrawAxisLine(true);                          //显示x轴
        barChart.getXAxis().setAxisLineColor(Color.parseColor("#FF8C00")); //设置x轴颜色
        barChart.getXAxis().setAxisLineWidth(1f);                           //设置x轴宽度
        barChart.getXAxis().setLabelCount(21);
        barChart.getXAxis().setTextColor(Color.parseColor("#FF8C00"));
        barChart.getXAxis().setTextSize(12);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(2000);
        barChart.setScaleEnabled(false);

        MyXFormatter myXFormatter=new MyXFormatter();
        barChart.getXAxis().setValueFormatter(myXFormatter);

        barChart.getAxisLeft().setAxisMinimum(-0.5f);
        barChart.getAxisRight().setAxisMinimum(-0.5f);
    }
    private List<BarEntry> convertDataToList(List<Float> data)
    {
        List<BarEntry> list=new ArrayList<BarEntry>();
        BarEntry barEntry=new BarEntry(0,0);
        list.add(barEntry);
        for(int i=0;i<data.size();i++)
        {
            barEntry=new BarEntry(i+1,data.get(i));
            list.add(barEntry);
        }
        barEntry=new BarEntry(data.size()+1,0);
        list.add(barEntry);
        return list;
    }
    public void setData(List<Float> floatData)
    {
        List<BarEntry> data=this.convertDataToList(floatData);
        BarDataSet barDataSet = new BarDataSet(data,"击球次数");
        barDataSet.setDrawValues(false);                  //柱状图顶部不显示数据
        barDataSet.setColor(Color.parseColor("#FF8C00"));
        this.dataSets.add(barDataSet);

        this.barChart.getLegend().setEnabled(false);
        BarData barData=new BarData(this.dataSets);

        barChart.setData(barData);
        barChart.invalidate();
    }
    private class MyXFormatter implements IAxisValueFormatter
    {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if(((int)value-1)%3==0)
                return (int)value+"";
            return "";
        }
    }
}
