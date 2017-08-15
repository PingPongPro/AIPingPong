package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.logging.SimpleFormatter;

/**
 * Created by 叶明林 on 2017/8/15.
 */

public class CalendarDateAdapter extends BaseAdapter {
    private int[] days = new int[42];
    private String[] date=new String[42];
    private Context context;
    private int year;
    private int month;

    public CalendarDateAdapter(Context context, int[][] days, int year, int month) {
        this.context = context;
        int dayNum = 0;
        //将二维数组转化为一维数组，方便使用
        for (int i = 0; i < days.length; i++) {
            for (int j = 0; j < days[i].length; j++) {
                this.days[dayNum] = days[i][j];
                if ((dayNum < 7 && this.days[dayNum] > 20))
                    this.date[dayNum]=new String(String.format("%04d-%02d-%02d",year,month-1,days[i][j]));
                else if (dayNum > 20 && this.days[dayNum] < 15)
                    this.date[dayNum]=new String(String.format("%04d-%02d-%02d",year,month+1,days[i][j]));
                else
                    this.date[dayNum]=new String(String.format("%04d-%02d-%02d",year,month,days[i][j]));
                dayNum++;
            }
        }
        this.year = year;
        this.month = month;
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int i) {
        return date[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.date_item, null);
            viewHolder = new ViewHolder();
            viewHolder.date_item = (TextView) view.findViewById(R.id.date_item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (i < 7 && days[i] > 20) {
            viewHolder.date_item.setTextColor(Color.rgb(204, 204, 204));//将上个月的和下个月的设置为灰色
        } else if (i > 20 && days[i] < 15) {
            viewHolder.date_item.setTextColor(Color.rgb(204, 204, 204));
        }
        viewHolder.date_item.setText(days[i] + "");

        return view;
    }

    /**
     * 优化Adapter
     */
    class ViewHolder {
        TextView date_item;
    }
}