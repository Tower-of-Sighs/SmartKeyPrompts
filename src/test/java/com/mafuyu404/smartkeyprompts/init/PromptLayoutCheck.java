package com.mafuyu404.smartkeyprompts.init;

import java.lang.reflect.Method;

/** Exercise the renderer's row coordinates without starting the Minecraft client. */
public final class PromptLayoutCheck {
    private static int checks;

    private static void check(boolean passed, String message) {
        if (!passed) throw new AssertionError(message);
        checks++;
    }

    private static int y(Method layout, int position, int height, int count, int index,
                         float scale, boolean crosshair) throws Exception {
        return (int) layout.invoke(null, position, height, count, index, scale, crosshair);
    }

    public static void main(String[] args) throws Exception {
        Method layout = HUD.class.getDeclaredMethod("calculatePromptY", int.class, int.class,
                int.class, int.class, float.class, boolean.class);
        layout.setAccessible(true);

        for (float scale : new float[]{0, 0.1f, 0.625f, 0.8f, 0.875f, 1, 1.25f, 2, 10}) {
            for (int height : new int[]{241, 360, 720}) {
                for (int position : new int[]{5, 7}) {
                    int footer = y(layout, position, height, 2, 1, scale, false);
                    int category = y(layout, position, height, 2, 0, scale, false);
                    for (int count : new int[]{2, 6, 3, 12, 7, 2}) {
                        check(y(layout, position, height, count, count - 1, scale, false) == footer,
                                "last row moved when category size changed");
                        check(y(layout, position, height, count, count - 2, scale, false) == category,
                                "penultimate row moved when category size changed");
                    }
                    check(y(layout, position, height + 100, 6, 5, scale, false) == footer + 100,
                            "bottom anchor did not follow window height");
                }
                for (int position : new int[]{1, 3}) {
                    check(y(layout, position, height, 6, 0, scale, false) == 5,
                            "top margin changed");
                    check(y(layout, position, height, 6, 4, scale, false)
                                    == 5 + (int) (16.0 * scale * 4), "top row spacing changed");
                }
                for (int position : new int[]{4, 8}) {
                    for (int count : new int[]{2, 3, 6, 12}) {
                        double center = (y(layout, position, height, count, 0, scale, false)
                                + y(layout, position, height, count, count - 1, scale, false)
                                + 16.0 * scale) / 2;
                        check(Math.abs(center - height / 2) <= 1,
                                "middle layout used an unscaled block height");
                    }
                }
                for (int position = 1; position <= 8; position++) {
                    check(y(layout, position, height, 6, 4, scale, true)
                                    == height / 2 + 7 + (int) (16.0 * scale * 4),
                            "crosshair coordinates changed");
                }
            }
        }
        check(y(layout, 5, 360, 6, 5, 0.8f, false) == 343, "reported 0.8-scale footer regression");
        check(y(layout, 5, 360, 2, 0, 0.8f, false) == 330, "reported 0.8-scale category regression");
        System.out.println(checks + " native prompt-layout checks passed");
    }
}
