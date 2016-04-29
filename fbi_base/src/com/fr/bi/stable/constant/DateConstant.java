package com.fr.bi.stable.constant;

import com.fr.general.Inter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by GUY on 2015/3/12.
 */
public class DateConstant {
    public static String getWeekString(int m) {
        switch (m) {
            case Calendar.MONDAY:
                return Inter.getLocText("BI-Monday");
            case Calendar.TUESDAY:
                return Inter.getLocText("BI-Tuesday");
            case Calendar.WEDNESDAY:
                return Inter.getLocText("BI-Wednesday");
            case Calendar.THURSDAY:
                return Inter.getLocText("BI-Thursday");
            case Calendar.FRIDAY:
                return Inter.getLocText("BI-Friday");
            case Calendar.SATURDAY:
                return Inter.getLocText("BI-Saturday");
            case Calendar.SUNDAY:
                return Inter.getLocText("BI-Sunday");
        }
        return "SB";
    }

    /**
     * 时间分类
     *
     * @author frank
     */
    public static final class DATE {

        public static final String FIELDNAME = "__current_time_is_now__";

        public static final int YEAR = 0;

        public static final int SEASON = 1;

        public static final int MONTH = 2;

        public static final int WEEK = 3;

        public static final int DAY = 4;

        public static final int YMD = 5;
    }

    public static final class DATEDELTRA {

        public static final int DAY = 1000 * 60 * 60 * 24;

        public static final int WEEK = DAY * 7;
        
        public static final int MONTH_OF_YEAR = 12;

        public static final int MONTH_OF_SEASON = 3;
    }

    public static class DATEEDITOR {
        public static final int BEFORE = 0x0;

        public static final int AFTER = 0x1;

        public static final int START = 0x0;

        public static final int END = 0x1;
    }

    public static final class DATEMAP {
        public static final int MONTHINDEX = 0x0;
        public static final int WEEKINDEX = 0x1;

        public static final String TAG_DATE = "_date";

        public static final String DATEFORMATSTRING = "yyyy/MM/dd";

        public static final DateFormat DATEFORMAT = new SimpleDateFormat(DATEFORMATSTRING);

        //时间不会是这样一个负数的
        public static final long NULLDATE = Long.MIN_VALUE;

        public static final int STRINGLENGTH = DATEFORMATSTRING.length();
    }

    public static final class CALENDAR {

        public static class WEEK {

            public final static int SUNDAY = Calendar.SUNDAY;

            public final static int MONDAY = Calendar.MONDAY;

            public final static int TUESDAY = Calendar.TUESDAY;

            public final static int WEDNESDAY = Calendar.WEDNESDAY;

            public final static int THURSDAY = Calendar.THURSDAY;

            public final static int FRIDAY = Calendar.FRIDAY;

            public final static int SATURDAY = Calendar.SATURDAY;
        }

        public static final class MONTH {

            public final static int JANUARY = Calendar.JANUARY;

            public final static int FEBRUARY = Calendar.FEBRUARY;

            public final static int MARCH = Calendar.MARCH;

            public final static int APRIL = Calendar.APRIL;

            public final static int MAY = Calendar.MAY;

            public final static int JUNE = Calendar.JUNE;

            public final static int JULY = Calendar.JULY;

            public final static int AUGUST = Calendar.AUGUST;

            public final static int SEPTEMBER = Calendar.SEPTEMBER;

            public final static int OCTOBER = Calendar.OCTOBER;

            public final static int NOVEMBER = Calendar.NOVEMBER;

            public final static int DECEMBER = Calendar.DECEMBER;

        }

        /**
         * 这里不用春夏秋冬了，南半球是秋冬春夏
         *
         * @author Daniel
         */
        public static final class SEASON {

            public final static int Q1 = 0x1;

            public final static int Q2 = 0x2;

            public final static int Q3 = 0x3;

            public final static int Q4 = 0x4;

        }
    }
}