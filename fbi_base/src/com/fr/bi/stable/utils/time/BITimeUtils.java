package com.fr.bi.stable.utils.time;

import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.constant.DateConstant;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * Created by Connery on 2015/12/4.
 */
public class BITimeUtils {


    @SuppressWarnings("unchecked")


    public static int getFieldFromTime(Long t, int field) {
        if (t != null && t != DateConstant.DATEMAP.NULLDATE) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(t));
            return c.get(field);
        } else {
            return CubeConstant.NULLINDEX;
        }
    }
}