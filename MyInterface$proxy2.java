package org.example.proxy;

public class MyInterface$proxy2 implements MyInterface {
MyInterface myInterface;

    @Override
    public void func1() {
        System.out.println("before");
            myInterface.func1();

    }

    @Override
    public void func2() {
        System.out.println("before");
            myInterface.func2();

    }

    @Override
    public void func3() {
        System.out.println("before");
            myInterface.func3();

    }
}
