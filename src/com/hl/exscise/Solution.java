package com.hl.exscise;

public class Solution {
    public static boolean isUnique(String astr) {
        int N=astr.length();
        for(int i=0;i<N;i++)
        {
            int j=i+1;
            while(j<N)
            {
                if(astr.charAt(i)==astr.charAt(j))
                    return false;
                else
                    j++;
            }
        }
        return true;


    }

    public static void main(String[] args) {
        String aa="aaa";
        boolean b=isUnique(aa);
        System.out.println(String.valueOf(b));
    }
}
