package org.example.jvm;


import org.jetbrains.annotations.NotNull;
import tech.medivh.classpy.classfile.ClassFile;
import tech.medivh.classpy.classfile.ClassFileParser;
import tech.medivh.classpy.classfile.MethodInfo;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Hotspot {

    private String mainClass;

    BootstrapClassLoader classLoader;

    public Hotspot(String mainClass, @NotNull String classPathString) {
        this.mainClass = mainClass;
        this.classLoader =  new BootstrapClassLoader(Arrays.asList(classPathString.split(File.pathSeparator)));
    }

    void start() throws Exception {
        ClassFile mainClassFile = this.classLoader.loadClass(this.mainClass);
        MethodInfo mainMethod = mainClassFile.getMainMethod();
        StackFrame mainFrame = new StackFrame(mainMethod,mainClassFile.getConstantPool());
        Thread mainThread = new Thread("mainThread",mainFrame,classLoader);
        mainThread.start();
    }


}
