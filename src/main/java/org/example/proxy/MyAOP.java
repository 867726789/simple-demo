package org.example.proxy;

public interface MyAOP {

    public String before(String methodName);

    public String after(String methodName);

    public String around(String methodName);

}
