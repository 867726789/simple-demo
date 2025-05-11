package org.example.jvm.code;

public class Demo {
    public static void main(String[] args) {
        System.out.println(1);
        System.out.println(max(1,4));
    }

    public static int max(int a, int b){
        if (a > b) {
            return a;
        }
        return b;
    }
}
