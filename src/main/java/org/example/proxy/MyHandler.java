package org.example.proxy;

public interface MyHandler {
    public String functionBody(String methodName);

    default public void setProxy(MyInterface proxy){};
}
