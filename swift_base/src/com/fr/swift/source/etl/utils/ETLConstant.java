package com.fr.swift.source.etl.utils;

/**
 * Created by Handsome on 2018/1/18 0018 09:30
 * @deprecated
 */
public class ETLConstant {

    public static final class CONF {

        public static final class FIELD_ID {

            public static final String SYSTEM_TIME = "";
        }


        public static final class ADD_COLUMN {
            public static final class TIME_GAP {
                public static final int TYPE = 0x2;
                public static final String SYSTEM_TIME = "";
                public static final class UNITS {
                    public static final int YEAR = 0x1;
                    public static final int QUARTER = 0x2;
                    public static final int MONTH = 0x3;
                    public static final int WEEK = 0x4;
                    public static final int DAY = 0x5;
                    public static final int HOUR = 0x6;
                    public static final int MINUTE = 0x7;
                    public static final int SECOND = 0x8;
                }
            }
            public static final class RANKING {
                public static final int TYPE = 0x6;
                public static final int ASC = 0xa;
                public static final int DSC = 0xb;
                public static final int ASC_IN_GROUP = 0xc;
                public static final int DSC_IN_GROUP = 0xd;
            }
        }

        public static final class JOIN {
            public static final int OUTER = 0x1;
            public static final int INNER = 0x2;
            public static final int LEFT = 0x3;
            public static final int RIGHT = 0x4;
        }
    }
}