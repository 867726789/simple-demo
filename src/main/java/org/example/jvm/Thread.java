package org.example.jvm;

import tech.medivh.classpy.classfile.ClassFile;
import tech.medivh.classpy.classfile.MethodInfo;
import tech.medivh.classpy.classfile.bytecode.*;
import tech.medivh.classpy.classfile.constant.ConstantMethodrefInfo;
import tech.medivh.classpy.classfile.constant.ConstantPool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class Thread {

    private final String threadName;
    private final JVMStack jvmStack;
    private final PcRegister pcRegister;
    private final BootstrapClassLoader classLoader;

    public Thread(String threadName, StackFrame stackFrame, BootstrapClassLoader classLoader) {
        this.threadName = threadName;
        this.jvmStack = new JVMStack();
        this.jvmStack.push(stackFrame);
        this.pcRegister = new PcRegister(jvmStack);
        this.classLoader = classLoader;
    }

    public void start() throws Exception{
        for (Instruction instruction : pcRegister) {
            System.out.println(instruction);
            ConstantPool constantPool = jvmStack.peek().constantPool;
            switch (instruction.getOpcode()) {
                case getstatic -> { // 压入一个类的一个属性
                    GetStatic getStatic = (GetStatic) instruction;
                    String className = getStatic.getClassName(constantPool);
                    String filedName = getStatic.getFieldName(constantPool);
                    Object staticField;
                    if (className.contains("java")) { //类名加属性名获取属性对象并压入操作数栈
                        Class<?> aClass = Class.forName(className);
                        Field declaredField = aClass.getDeclaredField(filedName);
                        staticField = declaredField.get(null);
                        jvmStack.peek().pushObjectToOperandStack(staticField);
                    }
                }
                case iconst_1 -> {
                    jvmStack.peek().pushObjectToOperandStack(1);
                }
                case iconst_2 -> {
                    jvmStack.peek().pushObjectToOperandStack(2);
                }
                case iconst_4 -> {
                    jvmStack.peek().pushObjectToOperandStack(4);
                }
                case iload_0 -> {
                    jvmStack.peek().pushObjectToOperandStack(jvmStack.peek().localVariable[0]);
                }
                case iload_1 -> {
                    jvmStack.peek().pushObjectToOperandStack(jvmStack.peek().localVariable[1]);
                }
                case invokevirtual -> { //调用一个函数
                    InvokeVirtual invokeVirtual = (InvokeVirtual) instruction;
                    // 获取需要被调用的函数的信息
                    ConstantMethodrefInfo methodInfo = invokeVirtual.getMethodInfo(constantPool);

                    // 根据方法的对应类名来加载对应类，以获得对应的常量池和实际的函数
                    String className = methodInfo.className(constantPool);
                    String methodName = methodInfo.methodName(constantPool);
                    List<String> params = methodInfo.paramClassName(constantPool);

                    if (className.contains("java")) {
                        Class<?> aClass = Class.forName(className);
                        Method declaredMethod = aClass.getDeclaredMethod(methodName,params.stream().map(this::nameToClass).toArray(Class[]::new));
                        Object[] args = new Object[params.size()];
                        for (int index = args.length-1 ; index >= 0 ; index--) {
                            args[index] = jvmStack.peek().operandStack.pop();
                        }
                        Object result = declaredMethod.invoke(jvmStack.peek().operandStack.pop(),args);
                        if (!methodInfo.isVoid(constantPool)){
                            jvmStack.peek().pushObjectToOperandStack(result);
                        }
                        break;
                    }


                    ClassFile classFile = classLoader.loadClass(className);


                    MethodInfo finalMethodInfo = classFile.getMethods(methodName).get(0);
                    Object[] args = new Object[params.size()+1];
                    for (int index = args.length-1 ; index >= 0 ; index--) {
                        args[index] = jvmStack.peek().operandStack.pop();
                    }
                    // 压入新的栈帧
                    StackFrame stackFrame = new StackFrame(finalMethodInfo,classFile.getConstantPool(),args);
                    jvmStack.push(stackFrame);
                }
                case invokestatic -> { //调用一个函数
                    InvokeStatic invokeStatic = (InvokeStatic) instruction;
                    // 获取需要被调用的函数的信息
                    ConstantMethodrefInfo methodInfo = invokeStatic.getMethodInfo(constantPool);
                    // 根据方法的对应类名来加载对应类，以获得对应的常量池和实际的函数
                    String className = methodInfo.className(constantPool);
                    String methodName = methodInfo.methodName(constantPool);
                    List<String> params = methodInfo.paramClassName(constantPool);

                    if (className.contains("java")) {
                        Class<?> aClass = Class.forName(className);
                        Method declaredMethod = aClass.getDeclaredMethod(methodName,params.stream().map(this::nameToClass).toArray(Class[]::new));
                        Object[] args = new Object[params.size()];
                        for (int index = args.length-1 ; index >= 0 ; index--) {
                            args[index] = jvmStack.peek().operandStack.pop();
                        }
                        Object result = declaredMethod.invoke(null,args);
                        if (!methodInfo.isVoid(constantPool)){
                            jvmStack.peek().pushObjectToOperandStack(result);
                        }
                        break;
                    }


                    ClassFile classFile = classLoader.loadClass(className);


                    MethodInfo finalMethodInfo = classFile.getMethods(methodName).get(0);
                    Object[] args = new Object[params.size()];
                    for (int index = args.length-1 ; index >= 0 ; index--) {
                        args[index] = jvmStack.peek().operandStack.pop();
                    }
                    // 压入新的栈帧
                    StackFrame stackFrame = new StackFrame(finalMethodInfo,classFile.getConstantPool(),args);
                    jvmStack.push(stackFrame);
                }
                case _return -> {
                    this.jvmStack.pop();
                }
                case ireturn -> {
                    int result = (int)this.jvmStack.peek().operandStack.pop();
                    this.jvmStack.pop();
                    this.jvmStack.peek().pushObjectToOperandStack(result);
                }
                case if_icmple -> {
                    int value2 = (int)jvmStack.peek().operandStack.pop();
                    int value1 = (int)jvmStack.peek().operandStack.pop();
                    if (value1 <= value2) {
                        Branch branch = (Branch) instruction;
                        int jumpTo = branch.getJumpTo();
                        jvmStack.peek().jumpTo(jumpTo);
                    }
                }
                default -> {
                    throw new IllegalArgumentException("指令未实现"+instruction);
                }
            }
        }
    }

    private Class<?> nameToClass(String className) {
        if (className.equals("int")) {
            return int.class;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
