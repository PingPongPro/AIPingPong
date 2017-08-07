package com.example.myapplication;

import android.graphics.Color;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class PieChartManager {
    /**
     * 设置饼图样式
     *
     * @param pieChart
     * @param pieValues
     * @param title
     * @param showLegend 是否显示图例
     */

    public static int TotalNum = 0;
    public static String Title;


    public static final int[] PIE_COLORS = {
            Color.rgb(255, 165, 0),
            Color.rgb(255, 69, 0)
    };

    public static void setPieChart(PieChart pieChart, Map<String, Float> pieValues, String title, boolean showLegend) {
        pieChart.setUsePercentValues(true);//设置使用百分比
        pieChart.getDescription().setEnabled(false);//设置描述
        pieChart.setExtraOffsets(20, 15, 20, 15);
        Title = title;
        //pieChart.setCenterText(title + ":\n" + TotalNum);//设置环中的文字
        pieChart.setCenterTextSize(22f);//设置环中文字的大小
        pieChart.setDrawCenterText(true);//设置绘制环中文字
        pieChart.setRotationAngle(120f);//设置旋转角度
        pieChart.setAlpha(0.5f);

        //图例设置
        Legend legend = pieChart.getLegend();
        if (showLegend) {
            legend.setEnabled(true);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        } else {
            legend.setEnabled(false);
        }

        //设置饼图数据
        setPieChartData(pieChart, pieValues);

        pieChart.animateX(1500, Easing.EasingOption.EaseInOutQuad);//数据显示动画
    }

    /**
     * 设置饼图数据源
     */
    private static void setPieChartData(PieChart pieChart, Map<String, Float> pieValues) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        Set set = pieValues.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            entries.add(new PieEntry(Float.valueOf(entry.getValue().toString()), entry.getKey().toString()));
            TotalNum += Float.valueOf(entry.getValue().toString());
        }

        pieChart.setCenterText(Title + "\n" + TotalNum);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);//设置饼块之间的间隔
        dataSet.setSelectionShift(5f);//设置饼块选中时偏离饼图中心的距离

        dataSet.setColors(PIE_COLORS);//设置饼块的颜色
        dataSet.setValueLinePart1OffsetPercentage(80f);//数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setValueLineColor(Color.BLACK);//设置连接线的颜色
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.DKGRAY);

        pieChart.setData(pieData);
        pieChart.highlightValues(null);
        pieChart.invalidate();
    }
}
