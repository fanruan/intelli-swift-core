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

    public static final class TARGET_TYPE {

        public static final int STRING = 0x1;

        public static final int NUMBER = 0x2;

        public static final int DATE = 0x3;

        public static final int COUNTER = 0x4;

        public static final int FORMULA = 0x5;
        public static final int YEAR_ON_YEAR_RATE = 0x6;
        public static final int MONTH_ON_MONTH_RATE = 0x7;
        public static final int YEAR_ON_YEAR_VALUE = 0x8;
        public static final int MONTH_ON_MONTH_VALUE = 0x9;
        public static final int SUM_OF_ABOVE = 0xa;
        public static final int SUM_OF_ABOVE_IN_GROUP = 0xb;
        public static final int SUM_OF_ALL = 0xc;
        public static final int SUM_OF_ALL_IN_GROUP = 0xd;
        public static final int RANK = 0xe;
        public static final int RANK_IN_GROUP = 0xf;


//        public static final int CALCULATOR = 0x5;

        public static final class CAL {

            public static final int FORMULA = 0x0;

            public static final int CONFIGURATION = 0x1;

        }

        public static final class CAL_VALUE {

            public static final int SUM_OF_ALL = 0x0;

            public static final int PERIOD = 0x1;

            public static final int SUM_OF_ABOVE = 0x2;

            public static final int RANK = 0x3;

            public static final class RANK_TPYE {

                public static final int ASC = 0x0;

                public static final int DESC = 0x1;
            }

            public static final class SUMMARY_TYPE {
                public final static int SUM = 0x0;

                public final static int MAX = 0x1;

                public final static int MIN = 0x2;

                public final static int AVG = 0x3;
            }

            public static final class PERIOD_TYPE {
                public final static int VALUE = 0x0;

                public final static int RATE = 0x1;
            }


        }


        public static final class CAL_POSITION {

            public static final int ALL = 0x0;

            public static final int INGROUP = 0x1;
        }
    }
}