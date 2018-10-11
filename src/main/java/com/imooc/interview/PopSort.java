package com.imooc.interview;

public class PopSort {
    public static final int[] array = {};
    public static int[] popSort(int[] array) throws Exception {
        if (array == null || array.length<1){
            throw new Exception("array is empty");
        }
        if(array.length == 1){
            return array;
        }
        int temp = 0;
        for (int i = 1;i<array.length;i++){
            for (int j = 0;j<i;j++){
                if (array[i]<array[j]){
                    temp = array[j];
                    array[j] = array[i];
                    array[i] = temp;
                }
            }
        }
        return array;
    }

    public static void main(String[] args) {
        try {
            System.out.println("before pop sort");
            for (int i : array) {
                System.out.print(i+" ");
            }
            int[] sourtedArray = popSort(array);
            System.out.println();
            System.out.println("after pop sort:");
            for (int i : sourtedArray) {
                System.out.print(i+" ");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }
}
