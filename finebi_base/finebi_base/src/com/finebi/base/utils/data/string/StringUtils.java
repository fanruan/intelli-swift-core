package com.finebi.base.utils.data.string;


/**
 * Created by andrew_asa on 2017/9/29.
 */
public class StringUtils {

    public static String EMPTY = StringUtils.EMPTY;

    /**
     * 以前的所谓StringUtils都是用私有的构造方法但是这是不合理的，因为如果本utils方法里面不实现
     * 别人想继承怎么办
     */
    public StringUtils() {

    }

    public static String setIndex(String str, int index, String c) {

        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, index) + c + str.substring(index);
    }

    public static boolean isEmpty(String s) {

        return s != null && StringUtils.EMPTY.equals(s);
    }

}
