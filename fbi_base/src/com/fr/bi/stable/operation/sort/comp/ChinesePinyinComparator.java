package com.fr.bi.stable.operation.sort.comp;

import com.fr.stable.pinyin.PinyinFormat;
import com.fr.stable.pinyin.PinyinHelper;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GUY on 2015/2/12.
 */
public class ChinesePinyinComparator extends AbstractComparator<String> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5930600992594460524L;

    private static transient ConcurrentHashMap<Integer, String> stringMap = new ConcurrentHashMap<Integer, String>();
    /**
     * 拼音的空值肯定是字母啦
     */
    private static String NULL = "我4空值";

    /**
     * 字符的拼音，多音字就得到第一个拼音。不是汉字，就return null。
     */
    private static String pinyin(int c) {
        String res = stringMap.get(c);
        if (res == null) {
            String[] pinyins = PinyinHelper.convertToPinyinArray((char) c, PinyinFormat.WITH_TONE_NUMBER);
            if (pinyins != null) {
                res = pinyins[0];
            }
            if (res != null) {
                stringMap.put(c, res);
            } else {
                stringMap.put(c, NULL);
            }
        } else if (NULL.equals(res)) {
            res = null;
        }
        return res;
    }

    @Override
    public int compare(String o1, String o2) {
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1 == o2) {
            return 0;
        }
        int o1Len = o1.length(), o2Len = o2.length();
        for (int i = 0; i < o1Len && i < o2Len; i++) {
            char codePoint1 = o1.charAt(i);
            char codePoint2 = o2.charAt(i);
            if (codePoint1 != codePoint2) {
                String pinyin1 = pinyin(codePoint1);
                String pinyin2 = pinyin(codePoint2);
                boolean isChinese1 = pinyin1 != null;
                boolean isChinese2 = pinyin2 != null;
                if (isChinese1 && isChinese2) { // 两个字符都是汉字
                    if (!pinyin1.equals(pinyin2)) {
                        return pinyin1.compareTo(pinyin2);
                    }
                } else if (isChinese1 ^ isChinese2) {
                    return isChinese1 ? 1 : -1;
                }
                return codePoint1 - codePoint2;
            }
        }
        return o1Len - o2Len;
    }

}