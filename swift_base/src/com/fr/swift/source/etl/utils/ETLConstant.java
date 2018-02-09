package com.fr.swift.source.etl.utils;

/**
 * Created by Handsome on 2018/1/18 0018 09:30
 */
public class ETLConstant {

    public static final class JOINTYPE {

        public static final int OUTER = 0x1;

        public static final int INNER = 0x2;

        public static final int LEFT = 0x3;

        public static final int RIGHT = 0x4;
    }

    public static final class SUMMARY_TYPE {

        public final static int SUM = 0x0;

        public final static int MAX = 0x1;

        public final static int MIN = 0x2;

        public final static int AVG = 0x3;

        public final static int COUNT = 0x4;

        public final static int APPEND = 0x5;

        public final static int RECORD_COUNT = 0x6;
    }

    public static final class FIELD_ID {
        public static final String HEAD = "81c48028-1401-11e6-a148-3e1d05defe78";
    }
}
