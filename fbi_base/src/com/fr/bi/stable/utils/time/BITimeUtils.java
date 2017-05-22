package com.fr.bi.stable.utils.time;

import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.constant.DateConstant;

import java.util.Calendar;

/**
 *
 * Created by Connery on 2015/12/4.
 */
public class BITimeUtils {


    @SuppressWarnings("unchecked")


    public static int getFieldFromTime(Long t, int field) {
        if (t != null && t != DateConstant.DATEMAP.NULLDATE) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(t);
            return c.get(field);
        } else {
            return CubeConstant.NULLINDEX;
        }
    }

    /**
     * 加一个传Calendar的方法，Calendar.getInstance()会耗费大量的内存，传一个过来保证线程安全
     * @param t
     * @param field
     * @param calendar
     * @return
     */
    public static int getFieldFromTime(Long t, int field, Calendar calendar) {
        if (t != null && t != DateConstant.DATEMAP.NULLDATE) {
            calendar.setTimeInMillis(t);
            return calendar.get(field);
        } else {
            return CubeConstant.NULLINDEX;
        }
    }
}