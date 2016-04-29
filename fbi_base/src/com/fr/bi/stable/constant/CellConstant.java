package com.fr.bi.stable.constant;

/**
 * Created by 小灰灰 on 2015/10/26.
 */
@Deprecated
public class CellConstant {
    public static final class CBCELL {
        public final static int NORMAL = 0x0;

        public final static int SUMARYNAME = 0x1;

        public final static int ROWFIELD = 0x2;

        public final static int SUMARYFIELD = 0x3;

        public final static int DIMENSIONTITLE_X = 0x7;

        public final static int DIMENSIONTITLE_Y = 0x8;

        public final static int TARGETTITLE_X = 0x9;

        public final static int TARGETTITLE_Y = 0x10;

        public static final class SORT {

            public final static int INCREASEFIELD = BIReportConstant.SORT.ASC;

            public final static int DECREASESORTFIELD = BIReportConstant.SORT.DESC;

            public final static int CUSTOMSORTFIELD = BIReportConstant.SORT.CUSTOM;

            public final static int UNSORTFIELD = 0x4;
        }
    }
}