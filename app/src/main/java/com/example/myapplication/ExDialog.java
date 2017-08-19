package com.example.myapplication;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExDialog extends ListActivity {
	private List<Map<String, Object>> mData;
	private String mDir;
	private String DirTitle = "ballGame";
	private String RootDir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"ballGame";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		try
		{
			Bundle bundle=intent.getExtras();
			DirTitle= bundle.getString("title");
			RootDir=bundle.getString("rootdir");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(DirTitle);
        System.out.println(RootDir);
		setTitle(DirTitle);
		mDir = RootDir;
		mData = getData();

		try {
			MyAdapter adapter = new MyAdapter(this);
			setListAdapter(adapter);
		}catch (Exception e)
		{

		}

		WindowManager DirWin = getWindowManager();
		Display Dis = DirWin.getDefaultDisplay();
		LayoutParams Type = getWindow().getAttributes();
		Type.height = (int) (Dis.getHeight()*0.8);
		Type.width = (int) (Dis.getWidth());
		getWindow().setAttributes(Type);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		File f = new File(mDir);
		List files = Arrays.asList(f.listFiles());

		Collections.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile())
					return -1;
				if (o1.isFile() && o2.isDirectory())
					return 1;
				return o1.getName().compareTo(o2.getName());
			}
		});

		if (files != null) {
			for (int i = 0; i < files.size(); i++) {
				map = new HashMap<String, Object>();
				map.put("Name", ((File)(files.get(i))).getName());
				map.put("Url", ((File)(files.get(i))).getPath());
				if (((File)(files.get(i))).isDirectory())
				{
					map.put("img", R.drawable.check);
					list.add(map);
				}
			}
		}
		return list;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
        finishWithResult((String) mData.get(position).get("Url"));
	}

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView Url;
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
				convertView = mInflater.inflate(R.layout.listview, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.name);
				holder.Url = (TextView) convertView.findViewById(R.id.url);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.img.setBackgroundResource((Integer) mData.get(position).get("img"));
			holder.title.setText((String) mData.get(position).get("Name"));
			holder.Url.setText((String) mData.get(position).get("Url"));
			return convertView;
		}
	}

	private void finishWithResult(String path) {
		Intent intent = new Intent();
		intent.putExtra("path",path);
		setResult(RESULT_OK, intent);
		finish();
	}
};

