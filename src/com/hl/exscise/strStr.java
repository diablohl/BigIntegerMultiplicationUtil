package com.hl.exscise;

public class strStr {
    public static  int strStr(String haystack, String needle) {

        if (!needle.equals("")){
            int l1=haystack.length();
            int l2=needle.length();
            int i,j;
            for (i=0;i<=l1-l2;i++){
                if (haystack.charAt(i) == needle.charAt(0)) {
                    int temp=i;
                    if (haystack.substring(i, i + l2).equals(needle)) {
                        return temp;
                    }
                }
            }
            return -1;
        }

        return  0;

    }

    public static void main(String[] args) {
        String haystack="dsa";
        String needle="ab";
        int i=strStr(haystack,needle);
        System.out.println(i);
    }
}
