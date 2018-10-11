package com.imooc.interview.Singleton;

import com.imooc.util.TTest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Singleton01 {
    private static Singleton01 single = new Singleton01();

    private Singleton01() {
    }

    public static Singleton01 getInstance() {
        return single;
    }

    public static void main(String[] args) {
        final Set set=new CopyOnWriteArraySet();
        TTest.timeTasks(1000, 100, new Runnable() {
            @Override
            public void run() {
                set.add(Singleton01.getInstance().hashCode());
            }
        });
        System.out.println(set.size());
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
