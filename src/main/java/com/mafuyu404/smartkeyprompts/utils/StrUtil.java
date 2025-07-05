package com.mafuyu404.smartkeyprompts.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StrUtil {
    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(CharSequence str) {
        final int length;
        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (!isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return str.toString();
        }

        final String str2 = str.toString();
        if (str2.startsWith(prefix.toString())) {
            return str2.substring(prefix.length()); // 截取后半段
        }
        return str2;
    }

    public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
        if (isEmpty(string) || separator == null) {
            return null == string ? null : string.toString();
        }

        final String str = string.toString();
        final String sep = separator.toString();
        if (sep.isEmpty()) {
            return EMPTY;
        }
        final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
        if (INDEX_NOT_FOUND == pos) {
            return str;
        }
        if (0 == pos) {
            return EMPTY;
        }
        return str.substring(0, pos);
    }

    public static String addSuffixIfNot(CharSequence str, CharSequence suffix) {
        return appendIfMissing(str, suffix, suffix);
    }

    public static String appendIfMissing(CharSequence str, CharSequence suffix, CharSequence... suffixes) {
        return appendIfMissing(str, suffix, false, suffixes);
    }

    public static String appendIfMissing(CharSequence str, CharSequence suffix, boolean ignoreCase, CharSequence... testSuffixes) {
        if (str == null || isEmpty(suffix) || endWith(str, suffix, ignoreCase, false)) {
            return str(str);
        }
        if (testSuffixes != null) {
            for (final CharSequence testSuffix : testSuffixes) {
                if (endWith(str, testSuffix, ignoreCase, false)) {
                    return str.toString();
                }
            }
        }
        return str.toString().concat(suffix.toString());
    }

    public static boolean endWith(CharSequence str, CharSequence suffix, boolean ignoreCase, boolean ignoreEquals) {
        if (null == str || null == suffix) {
            if (ignoreEquals) {
                return false;
            }
            return null == str && null == suffix;
        }

        final int strOffset = str.length() - suffix.length();
        boolean isEndWith = str.toString()
                .regionMatches(ignoreCase, strOffset, suffix.toString(), 0, suffix.length());

        if (isEndWith) {
            return (!ignoreEquals) || (!equals(str, suffix, ignoreCase));
        }
        return false;
    }

    public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
        if (null == str1) {
            // 只有两个都为null才判断相等
            return str2 == null;
        }
        if (null == str2) {
            // 字符串2空，字符串1非空，直接false
            return false;
        }

        if (ignoreCase) {
            return str1.toString().equalsIgnoreCase(str2.toString());
        } else {
            return str1.toString().contentEquals(str2);
        }
    }

    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c)
                || Character.isSpaceChar(c)
                || c == '\ufeff'
                || c == '\u202a'
                || c == '\u0000'
                // issue#I5UGSQ，Hangul Filler
                || c == '\u3164'
                // Braille Pattern Blank
                || c == '\u2800'
                // Zero Width Non-Joiner, ZWNJ
                || c == '\u200c'
                // MONGOLIAN VOWEL SEPARATOR
                || c == '\u180e';
    }

    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        } else if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return str(obj, charset);
        } else if (obj instanceof Byte[]) {
            return str(obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str(obj, charset);
        } else {
            return ArrayUtil.isArray(obj) ? ArrayUtil.toString(obj) : obj.toString();
        }
    }

    public static String utf8Str(Object obj) {
        return str(obj, StandardCharsets.UTF_8);
    }

    public static String format(CharSequence template, Object... params) {
        if (null == template) {
            return "null";
        } else {
            return !ArrayUtil.isEmpty(params) && !isBlank(template) ? StrFormatter.format(template.toString(), params) : template.toString();
        }
    }

    public static String replace(String str, String searchStr, String replacement) {
        return replace(str, 0, searchStr, replacement, false);
    }

    public static String replace(String str, int fromIndex, String searchStr, String replacement, boolean ignoreCase) {
        if (!isEmpty(str) && !isEmpty(searchStr)) {
            if (null == replacement) {
                replacement = "";
            }

            int strLength = str.length();
            int searchStrLength = searchStr.length();
            if (strLength < searchStrLength) {
                return str(str);
            } else if (fromIndex > strLength) {
                return str(str);
            } else {
                if (fromIndex < 0) {
                    fromIndex = 0;
                }

                StringBuilder result = new StringBuilder(strLength - searchStrLength + ((CharSequence) replacement).length());
                if (0 != fromIndex) {
                    result.append(str.subSequence(0, fromIndex));
                }

                int preIndex;
                int index;
                for (preIndex = fromIndex; (index = indexOf(str, searchStr, preIndex, ignoreCase)) > -1; preIndex = index + searchStrLength) {
                    result.append(str.subSequence(preIndex, index));
                    result.append(replacement);
                }

                if (preIndex < strLength) {
                    result.append(str.subSequence(preIndex, strLength));
                }

                return result.toString();
            }
        } else {
            return str(str);
        }
    }

    public static int indexOf(String str, String searchStr, int preIndex, boolean ignoreCase) {
        // 如果目标字符串或搜索字符串为空，或者 preIndex 超出范围，返回 -1
        if (str == null || searchStr == null || searchStr.isEmpty() || preIndex < 0 || preIndex >= str.length()) {
            return -1;
        }

        // 如果忽略大小写，将两个字符串都转为小写
        if (ignoreCase) {
            str = str.toLowerCase();
            searchStr = searchStr.toLowerCase();
        }

        // 从 preIndex 位置开始遍历字符串 str，查找 searchStr
        for (int i = preIndex; i <= str.length() - searchStr.length(); i++) {
            // 检查从当前位置开始，子字符串是否匹配
            if (str.startsWith(searchStr, i)) {
                return i;  // 找到匹配，返回当前索引
            }
        }

        // 没有找到匹配，返回 -1
        return -1;
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String maxLength(String str, int length) {
        if (str == null || length <= 0) {
            return null;
        } else {
            return str.length() <= length ? str : sub(str, 0, length) + "...";
        }
    }

    public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
        if (isEmpty(str)) {
            return str(str);
        } else {
            int len = str.length();
            if (fromIndexInclude < 0) {
                fromIndexInclude += len;
                if (fromIndexInclude < 0) {
                    fromIndexInclude = 0;
                }
            } else if (fromIndexInclude > len) {
                fromIndexInclude = len;
            }

            if (toIndexExclude < 0) {
                toIndexExclude += len;
                if (toIndexExclude < 0) {
                    toIndexExclude = len;
                }
            } else if (toIndexExclude > len) {
                toIndexExclude = len;
            }

            if (toIndexExclude < fromIndexInclude) {
                int tmp = fromIndexInclude;
                fromIndexInclude = toIndexExclude;
                toIndexExclude = tmp;
            }

            return fromIndexInclude == toIndexExclude ? "" : str.toString().substring(fromIndexInclude, toIndexExclude);
        }
    }
}
