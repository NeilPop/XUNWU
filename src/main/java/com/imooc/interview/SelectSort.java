package com.imooc.interview;

public class SelectSort {
    public static final int[] array = {6,12,20,7,3,8,9,14,5,1};
    public static void swap(int[] array ,int i ,int j){
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    public static int[] selectSort(int[] array){
        for (int i=0;i<array.length-1;i++)
        {
            int min = i;
            for (int j=min+1;j<array.length;j++)
            {
                if (array[j] < array[min]){
                    min =j;
                }

            }
            if (min != i){
                swap(array,i,min);
            }
        }
        return array;
    }
    public static void main(String[] args) {
        System.out.println("before JiWei sort");
        for (int i : array) {
            System.out.print(i+" ");
        }
        int[] sourtedArray = selectSort(array);
        System.out.println();
        System.out.println("after pop sort:");
        for (int i : sourtedArray) {
            System.out.print(i+" ");
        }
    }
}
