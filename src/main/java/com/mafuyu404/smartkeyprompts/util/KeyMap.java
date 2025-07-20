package com.mafuyu404.smartkeyprompts.util;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class KeyMap {
    private static final Map<String, Integer> KEY_MAP = new HashMap<>();

    static {
        KEY_MAP.put("key.keyboard.a", GLFW.GLFW_KEY_A);
        KEY_MAP.put("key.keyboard.b", GLFW.GLFW_KEY_B);
        KEY_MAP.put("key.keyboard.c", GLFW.GLFW_KEY_C);
        KEY_MAP.put("key.keyboard.d", GLFW.GLFW_KEY_D);
        KEY_MAP.put("key.keyboard.e", GLFW.GLFW_KEY_E);
        KEY_MAP.put("key.keyboard.f", GLFW.GLFW_KEY_F);
        KEY_MAP.put("key.keyboard.g", GLFW.GLFW_KEY_G);
        KEY_MAP.put("key.keyboard.h", GLFW.GLFW_KEY_H);
        KEY_MAP.put("key.keyboard.i", GLFW.GLFW_KEY_I);
        KEY_MAP.put("key.keyboard.j", GLFW.GLFW_KEY_J);
        KEY_MAP.put("key.keyboard.k", GLFW.GLFW_KEY_K);
        KEY_MAP.put("key.keyboard.l", GLFW.GLFW_KEY_L);
        KEY_MAP.put("key.keyboard.m", GLFW.GLFW_KEY_M);
        KEY_MAP.put("key.keyboard.n", GLFW.GLFW_KEY_N);
        KEY_MAP.put("key.keyboard.o", GLFW.GLFW_KEY_O);
        KEY_MAP.put("key.keyboard.p", GLFW.GLFW_KEY_P);
        KEY_MAP.put("key.keyboard.q", GLFW.GLFW_KEY_Q);
        KEY_MAP.put("key.keyboard.r", GLFW.GLFW_KEY_R);
        KEY_MAP.put("key.keyboard.s", GLFW.GLFW_KEY_S);
        KEY_MAP.put("key.keyboard.t", GLFW.GLFW_KEY_T);
        KEY_MAP.put("key.keyboard.u", GLFW.GLFW_KEY_U);
        KEY_MAP.put("key.keyboard.v", GLFW.GLFW_KEY_V);
        KEY_MAP.put("key.keyboard.w", GLFW.GLFW_KEY_W);
        KEY_MAP.put("key.keyboard.x", GLFW.GLFW_KEY_X);
        KEY_MAP.put("key.keyboard.y", GLFW.GLFW_KEY_Y);
        KEY_MAP.put("key.keyboard.z", GLFW.GLFW_KEY_Z);

        // 数字键
        KEY_MAP.put("key.keyboard.0", GLFW.GLFW_KEY_0);
        KEY_MAP.put("key.keyboard.1", GLFW.GLFW_KEY_1);
        KEY_MAP.put("key.keyboard.2", GLFW.GLFW_KEY_2);
        KEY_MAP.put("key.keyboard.3", GLFW.GLFW_KEY_3);
        KEY_MAP.put("key.keyboard.4", GLFW.GLFW_KEY_4);
        KEY_MAP.put("key.keyboard.5", GLFW.GLFW_KEY_5);
        KEY_MAP.put("key.keyboard.6", GLFW.GLFW_KEY_6);
        KEY_MAP.put("key.keyboard.7", GLFW.GLFW_KEY_7);
        KEY_MAP.put("key.keyboard.8", GLFW.GLFW_KEY_8);
        KEY_MAP.put("key.keyboard.9", GLFW.GLFW_KEY_9);

        // 符号键
        KEY_MAP.put("key.keyboard.space", GLFW.GLFW_KEY_SPACE);
        KEY_MAP.put("key.keyboard.apostrophe", GLFW.GLFW_KEY_APOSTROPHE);
        KEY_MAP.put("key.keyboard.comma", GLFW.GLFW_KEY_COMMA);
        KEY_MAP.put("key.keyboard.minus", GLFW.GLFW_KEY_MINUS);
        KEY_MAP.put("key.keyboard.period", GLFW.GLFW_KEY_PERIOD);
        KEY_MAP.put("key.keyboard.slash", GLFW.GLFW_KEY_SLASH);
        KEY_MAP.put("key.keyboard.semicolon", GLFW.GLFW_KEY_SEMICOLON);
        KEY_MAP.put("key.keyboard.equal", GLFW.GLFW_KEY_EQUAL);
        KEY_MAP.put("key.keyboard.left.bracket", GLFW.GLFW_KEY_LEFT_BRACKET);
        KEY_MAP.put("key.keyboard.backslash", GLFW.GLFW_KEY_BACKSLASH);
        KEY_MAP.put("key.keyboard.right.bracket", GLFW.GLFW_KEY_RIGHT_BRACKET);
        KEY_MAP.put("key.keyboard.grave.accent", GLFW.GLFW_KEY_GRAVE_ACCENT);
        KEY_MAP.put("key.keyboard.world.1", GLFW.GLFW_KEY_WORLD_1);
        KEY_MAP.put("key.keyboard.world.2", GLFW.GLFW_KEY_WORLD_2);

        // 功能键
        KEY_MAP.put("key.keyboard.escape", GLFW.GLFW_KEY_ESCAPE);
        KEY_MAP.put("key.keyboard.enter", GLFW.GLFW_KEY_ENTER);
        KEY_MAP.put("key.keyboard.tab", GLFW.GLFW_KEY_TAB);
        KEY_MAP.put("key.keyboard.backspace", GLFW.GLFW_KEY_BACKSPACE);
        KEY_MAP.put("key.keyboard.insert", GLFW.GLFW_KEY_INSERT);
        KEY_MAP.put("key.keyboard.delete", GLFW.GLFW_KEY_DELETE);
        KEY_MAP.put("key.keyboard.right", GLFW.GLFW_KEY_RIGHT);
        KEY_MAP.put("key.keyboard.left", GLFW.GLFW_KEY_LEFT);
        KEY_MAP.put("key.keyboard.down", GLFW.GLFW_KEY_DOWN);
        KEY_MAP.put("key.keyboard.up", GLFW.GLFW_KEY_UP);
        KEY_MAP.put("key.keyboard.page.up", GLFW.GLFW_KEY_PAGE_UP);
        KEY_MAP.put("key.keyboard.page.down", GLFW.GLFW_KEY_PAGE_DOWN);
        KEY_MAP.put("key.keyboard.home", GLFW.GLFW_KEY_HOME);
        KEY_MAP.put("key.keyboard.end", GLFW.GLFW_KEY_END);
        KEY_MAP.put("key.keyboard.caps.lock", GLFW.GLFW_KEY_CAPS_LOCK);
        KEY_MAP.put("key.keyboard.scroll.lock", GLFW.GLFW_KEY_SCROLL_LOCK);
        KEY_MAP.put("key.keyboard.num.lock", GLFW.GLFW_KEY_NUM_LOCK);
        KEY_MAP.put("key.keyboard.print.screen", GLFW.GLFW_KEY_PRINT_SCREEN);
        KEY_MAP.put("key.keyboard.pause", GLFW.GLFW_KEY_PAUSE);

        // F键系列
        KEY_MAP.put("key.keyboard.f1", GLFW.GLFW_KEY_F1);
        KEY_MAP.put("key.keyboard.f2", GLFW.GLFW_KEY_F2);
        KEY_MAP.put("key.keyboard.f3", GLFW.GLFW_KEY_F3);
        KEY_MAP.put("key.keyboard.f4", GLFW.GLFW_KEY_F4);
        KEY_MAP.put("key.keyboard.f5", GLFW.GLFW_KEY_F5);
        KEY_MAP.put("key.keyboard.f6", GLFW.GLFW_KEY_F6);
        KEY_MAP.put("key.keyboard.f7", GLFW.GLFW_KEY_F7);
        KEY_MAP.put("key.keyboard.f8", GLFW.GLFW_KEY_F8);
        KEY_MAP.put("key.keyboard.f9", GLFW.GLFW_KEY_F9);
        KEY_MAP.put("key.keyboard.f10", GLFW.GLFW_KEY_F10);
        KEY_MAP.put("key.keyboard.f11", GLFW.GLFW_KEY_F11);
        KEY_MAP.put("key.keyboard.f12", GLFW.GLFW_KEY_F12);
        KEY_MAP.put("key.keyboard.f13", GLFW.GLFW_KEY_F13);
        KEY_MAP.put("key.keyboard.f14", GLFW.GLFW_KEY_F14);
        KEY_MAP.put("key.keyboard.f15", GLFW.GLFW_KEY_F15);
        KEY_MAP.put("key.keyboard.f16", GLFW.GLFW_KEY_F16);
        KEY_MAP.put("key.keyboard.f17", GLFW.GLFW_KEY_F17);
        KEY_MAP.put("key.keyboard.f18", GLFW.GLFW_KEY_F18);
        KEY_MAP.put("key.keyboard.f19", GLFW.GLFW_KEY_F19);
        KEY_MAP.put("key.keyboard.f20", GLFW.GLFW_KEY_F20);
        KEY_MAP.put("key.keyboard.f21", GLFW.GLFW_KEY_F21);
        KEY_MAP.put("key.keyboard.f22", GLFW.GLFW_KEY_F22);
        KEY_MAP.put("key.keyboard.f23", GLFW.GLFW_KEY_F23);
        KEY_MAP.put("key.keyboard.f24", GLFW.GLFW_KEY_F24);
        KEY_MAP.put("key.keyboard.f25", GLFW.GLFW_KEY_F25);

        // 小键盘键
        KEY_MAP.put("key.keyboard.keypad.0", GLFW.GLFW_KEY_KP_0);
        KEY_MAP.put("key.keyboard.keypad.1", GLFW.GLFW_KEY_KP_1);
        KEY_MAP.put("key.keyboard.keypad.2", GLFW.GLFW_KEY_KP_2);
        KEY_MAP.put("key.keyboard.keypad.3", GLFW.GLFW_KEY_KP_3);
        KEY_MAP.put("key.keyboard.keypad.4", GLFW.GLFW_KEY_KP_4);
        KEY_MAP.put("key.keyboard.keypad.5", GLFW.GLFW_KEY_KP_5);
        KEY_MAP.put("key.keyboard.keypad.6", GLFW.GLFW_KEY_KP_6);
        KEY_MAP.put("key.keyboard.keypad.7", GLFW.GLFW_KEY_KP_7);
        KEY_MAP.put("key.keyboard.keypad.8", GLFW.GLFW_KEY_KP_8);
        KEY_MAP.put("key.keyboard.keypad.9", GLFW.GLFW_KEY_KP_9);
        KEY_MAP.put("key.keyboard.keypad.decimal", GLFW.GLFW_KEY_KP_DECIMAL);
        KEY_MAP.put("key.keyboard.keypad.divide", GLFW.GLFW_KEY_KP_DIVIDE);
        KEY_MAP.put("key.keyboard.keypad.multiply", GLFW.GLFW_KEY_KP_MULTIPLY);
        KEY_MAP.put("key.keyboard.keypad.subtract", GLFW.GLFW_KEY_KP_SUBTRACT);
        KEY_MAP.put("key.keyboard.keypad.add", GLFW.GLFW_KEY_KP_ADD);
        KEY_MAP.put("key.keyboard.keypad.enter", GLFW.GLFW_KEY_KP_ENTER);
        KEY_MAP.put("key.keyboard.keypad.equal", GLFW.GLFW_KEY_KP_EQUAL);

        // 修饰键
        KEY_MAP.put("key.keyboard.left.shift", GLFW.GLFW_KEY_LEFT_SHIFT);
        KEY_MAP.put("key.keyboard.left.control", GLFW.GLFW_KEY_LEFT_CONTROL);
        KEY_MAP.put("key.keyboard.left.alt", GLFW.GLFW_KEY_LEFT_ALT);
        KEY_MAP.put("key.keyboard.left.super", GLFW.GLFW_KEY_LEFT_SUPER);
        KEY_MAP.put("key.keyboard.right.shift", GLFW.GLFW_KEY_RIGHT_SHIFT);
        KEY_MAP.put("key.keyboard.right.control", GLFW.GLFW_KEY_RIGHT_CONTROL);
        KEY_MAP.put("key.keyboard.right.alt", GLFW.GLFW_KEY_RIGHT_ALT);
        KEY_MAP.put("key.keyboard.right.super", GLFW.GLFW_KEY_RIGHT_SUPER);
        KEY_MAP.put("key.keyboard.menu", GLFW.GLFW_KEY_MENU);

        // 鼠标
        for (int i = 1; i <= 8; i++) {
            KEY_MAP.put("key.mouse.button." + i, i - 1);
        }
        KEY_MAP.put("key.mouse.left", GLFW.GLFW_MOUSE_BUTTON_1);
        KEY_MAP.put("key.mouse.right", GLFW.GLFW_MOUSE_BUTTON_2);
        KEY_MAP.put("key.mouse.wheel", GLFW.GLFW_MOUSE_BUTTON_3);
        KEY_MAP.put("key.mouse.middle", GLFW.GLFW_MOUSE_BUTTON_3);
    }

    public static Integer getGLFWKey(String keyName) {
        return KEY_MAP.get(keyName);
    }

    public static boolean containsKey(String keyName) {
        return KEY_MAP.containsKey(keyName);
    }
}
