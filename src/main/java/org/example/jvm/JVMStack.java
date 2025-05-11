package org.example.jvm;

import java.util.ArrayDeque;
import java.util.Deque;

public class JVMStack {

    private Deque<StackFrame> stack = new ArrayDeque<>();

    public boolean isEmpty(){
        return stack.isEmpty();
    }

    public StackFrame peek() {
        return stack.peek();
    }

    public StackFrame pop() {
        return stack.pop();
    }

    public void push(StackFrame frame) {
        stack.push(frame);
    }
}
