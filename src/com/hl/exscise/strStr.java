package com.hl.exscise;

public class strStr {
    public static  int strStr(String haystack, String needle) {

        if (!needle.equals("")){
            int l1=haystack.length();
            int l2=needle.length();
            return 1;
        }



        return  0;

    }

    public static void main(String[] args) {
        String haystack="dsa";
        String needle="q";
        int i=strStr(haystack,needle);
        System.out.println(i);
    }
}
