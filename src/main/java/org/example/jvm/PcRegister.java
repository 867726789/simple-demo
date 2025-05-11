package org.example.jvm;

import org.jetbrains.annotations.NotNull;
import tech.medivh.classpy.classfile.bytecode.Instruction;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PcRegister implements Iterable<Instruction> {

    private final JVMStack jvmStack;

    public PcRegister(JVMStack jvmStack) {
        this.jvmStack = jvmStack;
    }

    @NotNull
    @Override
    public Iterator<Instruction> iterator() {
        return new PcRegisterIterator();
    }

    class PcRegisterIterator implements Iterator<Instruction> {

        @Override
        public boolean hasNext() {
            return !jvmStack.isEmpty();
        }

        @Override
        public Instruction next() {
            StackFrame topFrame = jvmStack.peek();
            return topFrame.getNextInstruction();
        }
    }
}
