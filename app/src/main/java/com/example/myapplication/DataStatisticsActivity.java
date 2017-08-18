package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DataStatisticsActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private BarChartManager barChartManager;
    private RadarChartManager radarChartManager;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new myException());
        setContentView(R.layout.datastatisticsactivity_main);

        //Thread.setDefaultUncaughtExceptionHandler(new myException());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Resources resources=this.getResources();
        //Drawable drawable=resources.getDrawable(R.drawable.back_white);
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

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Button button_fore=(Button)findViewById(R.id.button_fore);
        Button button_back=(Button)findViewById(R.id.button_back);
        Drawable drawable1=resources.getDrawable(R.drawable.back_black);
        button_back.setBackground(drawable1);
        Drawable drawable2=resources.getDrawable(R.drawable.back_black2);
        button_fore.setBackground(drawable2);

        button_fore.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mViewPager.getCurrentItem()!=1)
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1,true);
                    }
                }
        );

        button_back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mViewPager.getCurrentItem()!=0)
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1,true);
                    }
                }
        );
        try
        {
            BarChart barChart=(BarChart)findViewById(R.id.barChart);
            barChartManager=new BarChartManager(barChart);
            List<Float> floatList=new ArrayList<Float>();
            Random random=new Random();
            for(int i=1;i<=19;i++)
                floatList.add(random.nextFloat()*50);
            barChartManager.setData(floatList);

            RadarChart radarChart=(RadarChart)findViewById(R.id.radarChart);
            radarChartManager=new RadarChartManager(radarChart);
            floatList.clear();
            for(int i=1;i<=6;i++)
                floatList.add(random.nextFloat()*50);
            radarChartManager.setData(floatList);

            TabHost tabHost=(TabHost)findViewById(R.id.tabhost);
            tabHost.setup();

            TabRender.TabHostRender(new int[]{R.id.tab1, R.id.tab2},
                    new int[]{R.drawable.contest,R.drawable.contest_click,
                            R.drawable.dayreport,R.drawable.dayreport_click},tabHost,this);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            int arg=getArguments().getInt(ARG_SECTION_NUMBER);
            PieChart pieChart=(PieChart) rootView.findViewById(R.id.chart);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            if(arg==1)
            {
                Map<String,Float> map=new HashMap<String,Float>();
                map.put("aa",3f);
                map.put("bb",7f);
                PieChartManager.setPieChart(pieChart,map,"iii",false);
            }
            else
            {
                Map<String,Float> map=new HashMap<String,Float>();
                map.put("cc",6f);
                map.put("dd",4f);
                PieChartManager.setPieChart(pieChart,map,"pppp",false);
            }
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
