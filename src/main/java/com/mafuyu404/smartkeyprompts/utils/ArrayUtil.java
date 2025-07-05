package com.mafuyu404.smartkeyprompts.utils;

import java.util.Arrays;

public class ArrayUtil {

    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static String toString(Object obj) {
        switch (obj) {
            case null -> {
                return null;
            }
            case long[] longs -> {
                return Arrays.toString(longs);
            }
            case int[] ints -> {
                return Arrays.toString(ints);
            }
            case short[] shorts -> {
                return Arrays.toString(shorts);
            }
            case char[] chars -> {
                return Arrays.toString(chars);
            }
            case byte[] bytes -> {
                return Arrays.toString(bytes);
            }
            case boolean[] booleans -> {
                return Arrays.toString(booleans);
            }
            case float[] floats -> {
                return Arrays.toString(floats);
            }
            case double[] doubles -> {
                return Arrays.toString(doubles);
            }
            default -> {
                if (isArray(obj)) {
                    try {
                        return Arrays.deepToString((Object[]) obj);
                    } catch (Exception var2) {
                    }
                }

                return obj.toString();
            }
        }
    }
}
