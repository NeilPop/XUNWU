package com.imooc.interview.Singleton;

import com.imooc.util.TTest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Singleton02 {
    private static Singleton02 single = null;
    private Singleton02(){}
    public static synchronized Singleton02 getInstance(){
        if (single == null){
            single =  new Singleton02();
        }
        return single;

    }
    public static void main(String[] args) {
        final Set set=new CopyOnWriteArraySet();
        TTest.timeTasks(1000, 100, new Runnable() {
            @Override
            public void run() {
                set.add(Singleton05.getInstance().hashCode());
            }
        });
        System.out.println(set.size());
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
