package com.imooc.interview.Singleton;

import com.imooc.util.TTest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Singleton03 {
    private static  volatile Singleton03 single = null;
    final static Set threadSet = new CopyOnWriteArraySet();
    private Singleton03(){}
    public static Singleton03 getInstance(){
        if (single == null){
            threadSet.add(Thread.currentThread().getName());
            synchronized (Singleton03.class){
                //single = new Singleton03();
                if (single == null) {
                    System.out.println("++++"+Thread.currentThread().getName()+"+++");
                    single = new Singleton03();
                }
            }
        }
        return single;
    }
    public static void main(String[] args) {
        final Set set=new CopyOnWriteArraySet();
        TTest.timeTasks(1000, 100, new Runnable() {
            @Override
            public void run() {
                set.add(Singleton03.getInstance().hashCode());
            }
        });
        System.out.println("if set size:"+threadSet.size());
        Iterator iterator1 = threadSet.iterator();
        while(iterator1.hasNext()){
            System.out.println(iterator1.next());
        }
        System.out.println(set.size());
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
