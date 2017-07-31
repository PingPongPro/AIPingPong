package com.example.myapplication;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 叶明林 on 2017/7/31.
 */

public class BlueToothManager extends ListActivity {
    private List<Map<String, Object>> mData;
    private List<String> serviceName=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceName.add("BlueTooth_ML");
        serviceName.add("H701");
        serviceName.add("LIU718A");
        mData = getData();

        try {
            MyAdapter adapter = new MyAdapter(this);
            setListAdapter(adapter);
        }catch (Exception e)
        {

        }
        WindowManager DirWin = getWindowManager();
        Display Dis = DirWin.getDefaultDisplay();
        WindowManager.LayoutParams Type = getWindow().getAttributes();
        Type.height = (int) (Dis.getHeight()*0.5);
        Type.width = (int) (Dis.getWidth());
        getWindow().setAttributes(Type);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;
        for (int i = 1; i <= this.serviceName.size(); i++) {
            int tmp = 2*i-1;
            map = new HashMap<String, Object>();
            map.put("serviceName",  this.serviceName.get(i-1));
            list.add(map);
        }
        return list;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        finishWithResult((String) mData.get(position).get("serviceName"));
    }

    public final class ViewHolder {
        public TextView diff;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }
        public int getCount() {
            return mData.size();
        }
        public Object getItem(int arg0) {
            return null;
        }
        public long getItemId(int arg0) {
            return 0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.activity_select_difficulty, null);
                holder.diff = (TextView) convertView.findViewById(R.id.hard);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.diff.setText((String) mData.get(position).get("serviceName"));
            return convertView;
        }
    }

    private void finishWithResult(String path) {
        Intent intent = new Intent();
        intent.putExtra("serviceName",path);
        setResult(RESULT_OK, intent);
        finish();
    }
}
