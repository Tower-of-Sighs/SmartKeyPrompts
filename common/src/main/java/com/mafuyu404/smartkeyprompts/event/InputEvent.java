package com.mafuyu404.smartkeyprompts.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface InputEvent {


    Event<MouseButtonPre> MOUSE_BUTTON_PRE = EventFactory.createArrayBacked(MouseButtonPre.class,
            callbacks -> (button, action, modifiers) -> {
                for (MouseButtonPre cb : callbacks) {
                    if (cb.onMouseButtonPre(button, action, modifiers)) {
                        return true; // 任何一个返回 true 视为取消
                    }
                }
                return false;
            });
    Event<MouseButtonPost> MOUSE_BUTTON_POST = EventFactory.createArrayBacked(MouseButtonPost.class,
            callbacks -> (button, action, modifiers) -> {
                for (MouseButtonPost cb : callbacks) {
                    cb.onMouseButtonPost(button, action, modifiers);
                }
            });
    Event<MouseScroll> MOUSE_SCROLL = EventFactory.createArrayBacked(MouseScroll.class,
            callbacks -> (scrollDeltaX, scrollDeltaY, mouseX, mouseY, leftDown, middleDown, rightDown) -> {
                for (MouseScroll cb : callbacks) {
                    if (cb.onMouseScroll(scrollDeltaX, scrollDeltaY, mouseX, mouseY, leftDown, middleDown, rightDown)) {
                        return true;
                    }
                }
                return false;
            });
    Event<Key> KEY = EventFactory.createArrayBacked(Key.class,
            callbacks -> (key, scancode, action, modifiers) -> {
                for (Key cb : callbacks) {
                    cb.onKey(key, scancode, action, modifiers);
                }
            });


    @FunctionalInterface
    interface MouseButtonPre {
        /**
         * @return true 表示取消
         */
        boolean onMouseButtonPre(int button, int action, int modifiers);
    }

    @FunctionalInterface
    interface MouseButtonPost {
        void onMouseButtonPost(int button, int action, int modifiers);
    }


    @FunctionalInterface
    interface MouseScroll {
        /**
         * @return true 表示取消
         */
        boolean onMouseScroll(double scrollDeltaX, double scrollDeltaY, double mouseX, double mouseY,
                              boolean leftDown, boolean middleDown, boolean rightDown);
    }

    @FunctionalInterface
    interface Key {
        void onKey(int key, int scancode, int action, int modifiers);
    }
}