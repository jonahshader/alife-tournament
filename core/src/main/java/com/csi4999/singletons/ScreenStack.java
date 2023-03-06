package com.csi4999.singletons;

import com.badlogic.gdx.Screen;
import com.csi4999.ALifeApp;

import java.util.Stack;

public class ScreenStack {
    private ScreenStack() {}

    private static final Stack<Screen> stack = new Stack<>();

    public static ALifeApp app;

    public static void push(Screen screen) {
        stack.push(screen);
        app.setScreen(screen);
    }

    public static void pop() {
        stack.pop().dispose();
        app.setScreen(stack.peek());
    }

    public static void switchTo(Screen screen) {
        stack.pop().dispose();
        stack.push(screen);
        app.setScreen(screen);
    }
}
