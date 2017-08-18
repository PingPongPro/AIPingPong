package com.example.myapplication;

import android.graphics.Color;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 叶明林 on 2017/8/18.
 */

public class RadarChartManager {
    private RadarChart radarChart;
    private List<IRadarDataSet> dataSets=new ArrayList<IRadarDataSet>();
    public RadarChartManager(RadarChart chart)
    {
        this.radarChart =chart;
        radarChart.getDescription().setEnabled(false);
        radarChart.getXAxis().setDrawAxisLine(true);                          //显示x轴
        radarChart.getXAxis().setAxisLineColor(Color.parseColor("#FF8C00")); //设置x轴颜色
        radarChart.getXAxis().setAxisLineWidth(1f);                           //设置x轴宽度
        radarChart.getXAxis().setLabelCount(21);
        radarChart.getXAxis().setTextColor(Color.parseColor("#FF8C00"));
        radarChart.getXAxis().setTextSize(12);
        radarChart.getXAxis().setDrawGridLines(false);
        radarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        radarChart.getYAxis().setDrawLabels(false);
        radarChart.getYAxis().setAxisMinimum(0f);
        radarChart.animateY(2000);

        MyXFormatter myXFormatter=new MyXFormatter();
        radarChart.getXAxis().setValueFormatter(myXFormatter);
    }
    private List<RadarEntry> convertDataToList(List<Float> data)
    {
        List<RadarEntry> list=new ArrayList<RadarEntry>();
        for(int i=0;i<data.size();i++)
        {
            RadarEntry entry=new RadarEntry(data.get(i),i);
            list.add(entry);
        }
        return list;
    }
    public void setData(List<Float> floatData)
    {
        List<RadarEntry> data=this.convertDataToList(floatData);
        RadarDataSet radarDataSet = new RadarDataSet(data,"击球次数");
        radarDataSet.setDrawValues(false);                  //柱状图顶部不显示数据
        radarDataSet.setDrawFilled(true);
        radarDataSet.setColor(Color.parseColor("#FF8C00"));
        this.dataSets.add(radarDataSet);

        this.radarChart.getLegend().setEnabled(false);
        RadarData radarData=new RadarData(this.dataSets);

        radarChart.setData(radarData);
        radarChart.invalidate();
    }
    private class MyXFormatter implements IAxisValueFormatter
    {
        private String[] xValues=new String[]{"杀球","进攻","防守","技巧","发球","体能"};
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if((int)value<xValues.length)
                return xValues[(int)value];
            return "";
        }
    }
}
