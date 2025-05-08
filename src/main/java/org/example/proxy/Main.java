package org.example.proxy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) throws Exception {
        MyInterface proxyOrigin = MyInterfaceFactory.createProxyObject(new PrintFunctionName());
        MyInterface proxyObject = MyInterfaceFactory.createProxyObject(new LogHandler(proxyOrigin));
        proxyObject.func1();
        proxyObject.func2();
        proxyObject.func3();
    }

    static class PrintFunctionName implements MyHandler {

        @Override
        public String functionBody(String methodName) {
           return "System.out.println(\""+methodName+"\");";
        }
    }

    static class PrintFunctionName1 implements MyHandler {

        @Override
        public String functionBody(String methodName) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("System.out.println(1);\n")
                    .append("System.out.println(\""+methodName+"\");");
            return stringBuilder.toString();
        }
    }


    static class LogAOP implements MyAOP {

        @Override
        public String before(String methodName) {
            String context = "System.out.println(\"before\");\n" +
                    "            myInterface."+methodName+"();\n";
            return context;
        }

        @Override
        public String after(String methodName) {
            String context = "            myInterface."+methodName+"();\n" +
                    "            System.out.println(\"after\");";
            return context;
        }

        @Override
        public String around(String methodName) {
            String context = "System.out.println(\"before\");\n" +
                    "            myInterface."+methodName+"();\n" +
                    "            System.out.println(\"after\");";
            return context;
        }
    }


    static class LogHandler implements MyHandler {

        MyInterface myInterface;

        public LogHandler(MyInterface myInterface) {
            this.myInterface = myInterface;
        }

        @Override
        public String functionBody(String methodName) {
            String context = new LogAOP().before(methodName);
            return context;
        }

        @Override
        public void setProxy(MyInterface proxy) {
            Class<? extends MyInterface> clazz = proxy.getClass();
            try {
                Field field = clazz.getDeclaredField("myInterface");
                field.setAccessible(true);
                field.set(proxy,myInterface);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }


    }
}
