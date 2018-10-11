package com.imooc.interview;

public class JiWeiSort {
    public static final int[] array = {6,12,20,7,3,8,9,14,5,1};
    public static void swap(int[] array ,int i ,int j){
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static int[] jiWeiSort(int[] array) {
        int left = 0;
        int right = array.length-1;
        while (left<right){
            for (int i = left; i < right;i++){
                if (array[i]>array[i+1]){
                    swap(array,i,i+1);
                }
            }
            right--;
            for (int i = right;i>left;i--){
                if (array[i-1]>array[i]){
                    swap(array,i-1,i);
                }
            }
            left++;
        }
        return array;
    }

    public static void main(String[] args) {
        System.out.println("before JiWei sort");
        for (int i : array) {
            System.out.print(i+" ");
        }
        int[] sourtedArray = jiWeiSort(array);
        System.out.println();
        System.out.println("after pop sort:");
        for (int i : sourtedArray) {
            System.out.print(i+" ");
        }
    }
}
