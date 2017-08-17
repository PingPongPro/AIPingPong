package com.example.myapplication;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by 叶明林 on 2017/8/17.
 */

public class DataCache {
    private class Entry
    {
        private long population;
        private String id;
        private Object data;
        public Entry(String id,Object dataSet,long population)
        {
            this.id=id;
            this.data=dataSet;
            this.population=population;
        }
    }
    private final int size;
    private Queue<Entry> priorityQueue;
    public DataCache(int size)
    {
        this.size=size;
        this.priorityQueue=new PriorityQueue<Entry>(size,
                new Comparator<Entry>() {
                    @Override
                    public int compare(Entry o1, Entry o2) {
                        long population_o1=o1.population;
                        long population_o2=o2.population;
                        if(population_o1<population_o2)
                            return -1;
                        else if(population_o1>population_o2)
                            return 1;
                        return 0;
                    }
                }
        );
    }
    private void updatePopulation(String id,long newPopulation)
    {
        for(Entry x:priorityQueue)
        {
            if(x.id.equals(id))
            {
                x.population=newPopulation;
                break;
            }
        }
    }
    public boolean isEntryContained(String id)
    {
        for(Entry x:priorityQueue)
            if(x.id.equals(id))
                return true;
        return false;
    }
    public synchronized void addEntry(String id,Object data,long visitTime)
    {
        if(isEntryContained(id))
        {
            updatePopulation(id,visitTime);
            return ;
        }
        Entry entry=new Entry(id,data,visitTime);
        this.priorityQueue.add(entry);
        if(priorityQueue.size()>this.size)
            priorityQueue.remove();
    }
    public Object getData(String id)
    {
        for(Entry x:priorityQueue)
            if(x.id.equals(id))
                return x.data;
        return null;
    }
    public void displayAll()
    {
        for(Entry x:priorityQueue)
        {
            System.out.println( x.id+" "+x.population);
        }
        System.out.println("-----------");
    }

}
