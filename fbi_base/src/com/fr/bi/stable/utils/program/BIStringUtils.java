package com.fr.bi.stable.utils.program;

import com.fr.stable.StringUtils;

import java.util.Set;

/**
 * 字符串的操作
 * Created by Connery on 2015/12/4.
 */
public class BIStringUtils {


    public static String generateOrderSuffixString(String prefixOfName, Set<String> names) {
        int index = 1;
        while (true) {
            String newName = prefixOfName + index;
            if (!names.contains(newName)) {
                return newName;
            }
            index++;
        }
    }

    public static String appendWithSpace(String... strings) {
        return StringUtils.join(" ", strings);
    }
    public static String append(String... strings) {
        return StringUtils.join("", strings);
    }

    /**
     * 指定字符串末尾字符，并且是唯一的。
     * 例如："/a/b////",处理后应该是"/a/b/"
     * 例如："/a/b",处理后应该是"/a/b/"
     *
     * @param target  目标字符串
     * @param endChar 结尾字符串
     * @return 结尾处理后的字符串
     */
    public static String specificEndSingleChar(String target, String endChar) {
        return StringUtils.perfectEnd(cutEndChar(target, endChar), endChar);
    }

    /**
     * 指定字符开始，并且是唯一字符开始。
     *
     * @param target    目标字符串
     * @param startChar 以相应字符开始
     * @return 处理起始后的字符串
     */
    public static String specificStartSingleChar(String target, String startChar) {
        return StringUtils.perfectStart(cutStartChar(target, startChar), startChar);
    }

    /**
     * 去掉字符串头部的斜线
     *
     * @param location 目标字符串
     * @return 没有头部斜线的字符串。
     */
    public static String cutStartSlash(String location) {
        return cutStartChar(location, "/");
    }

    /**
     * 递归去除目标字符串尾部字符。
     * "////a/b/",去除头部"/"的话，结果为"a/b/"
     *
     * @param target  目标字符串；
     * @param cutChar 需要切除的头部
     * @return 处理后的字符
     */
    public static String cutStartChar(String target, String cutChar) {
        if (target != null) {
            if (target.startsWith(cutChar)) {
                return cutStartChar(target.substring(cutChar.length()), cutChar);
            } else {
                return target;
            }
        } else {
            return target;
        }
    }

    /**
     * 去掉字符串尾部的斜线
     *
     * @param location 目标字符串
     * @return 没有尾部斜线的字符串。
     */
    public static String cutEndSlash(String location) {
        return cutEndChar(location, "/");
    }

    /**
     * 递归去除目标字符串尾部字符。
     * "/a/b////",去除尾部"/"的话，结果为"/a/b"
     *
     * @param target  目标字符串；
     * @param cutChar 需要切除的尾部
     * @return 处理后的字符
     */
    public static String cutEndChar(String target, String cutChar) {
        if (target != null) {
            if (target.endsWith(cutChar)) {
                return cutEndChar(target.substring(0, target.length() - cutChar.length()), cutChar);
            } else {
                return target;
            }
        } else {
            return target;
        }
    }

    public static String emptyString() {
        return "__FINE_BI_EMPTY__";
    }

}