package com.imooc.interview.Singleton;

import com.imooc.util.TTest;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Singleton04 {

    final static Set threadSet = new CopyOnWriteArraySet();
    private Singleton04(){}
    private static class SingletonHolder{
        private static final Singleton04 SINGLE = new Singleton04();
        private static void inner(){
            System.out.println("inner");
        }
    }
    public static void main(String[] args) {
        final Set set=new CopyOnWriteArraySet();
        TTest.timeTasks(1000, 100, new Runnable() {
            @Override
            public void run() {
                //set.add(SingletonHolder.SINGLE.hashCode());
                //SingletonHolder.inner();
                //System.out.println();
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
