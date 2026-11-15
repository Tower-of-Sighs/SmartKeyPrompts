package com.mafuyu404.smartkeyprompts.util;

import net.minecraft.client.Minecraft;

public class CommonUtils {
    /**
     * 检查游戏界面是否打开
     */
    public static boolean isScreenOpen() {
        return Minecraft.getInstance().screen != null;
    }

    /**
     * 将描述ID转换为路径字符串格式
     */
    public static String toPathString(String key) {
        String[] path = key.split("\\.");
        return path[1] + ":" + path[2];
    }
}
