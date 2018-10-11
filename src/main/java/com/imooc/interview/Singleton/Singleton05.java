package com.imooc.interview.Singleton;

import com.imooc.util.TTest;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Singleton05 {

    private Singleton05(){}
    private enum InnerEnum{
        SINGLETON;
        private Singleton05 single;
        InnerEnum(){
            this.single = new Singleton05();
        }
        public  Singleton05 getInstance(){
            return single;
        }
    }

    public static Singleton05 getInstance(){

        return InnerEnum.SINGLETON.getInstance();
    }
    /*public static void main(String[] args) {
        final Set set=new CopyOnWriteArraySet();
        TTest.timeTasks(1000, 100, new Runnable() {
            @Override
            public void run() {
                set.add(Singleton05.InnerEnum..hashCode());
            }
        });
        System.out.println(set.size());
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }*/
}
