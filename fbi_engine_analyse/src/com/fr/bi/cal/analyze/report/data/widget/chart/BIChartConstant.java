package com.fr.bi.cal.analyze.report.data.widget.chart;

import com.fr.chart.base.ChartTypeValueCollection;

/**
 * Created by GUY on 2015/4/9.
 */
public class BIChartConstant {
    public static final class CHARTDEFINE {

        //3d
        public static final BIChartDefine BAR0 = new BIChartDefine(ChartTypeValueCollection.COLUMN, 3);
        public static final BIChartDefine ACCUMULATED_BAR0 = new BIChartDefine(ChartTypeValueCollection.COLUMN, 5);
        public static final BIChartDefine SQUARE0 = new BIChartDefine(ChartTypeValueCollection.AREA, 2);
        public static final BIChartDefine PIE0 = new BIChartDefine(ChartTypeValueCollection.PIE, 1);
        public static final BIChartDefine TIAO0 = new BIChartDefine(ChartTypeValueCollection.BAR, 3);
        public static final BIChartDefine ACCUMULATED_TIAO0 = new BIChartDefine(ChartTypeValueCollection.BAR, 5);
        public static final BIChartDefine DONUT0 = new BIChartDefine(ChartTypeValueCollection.DONUT, 1);

        //3d

        //2d
        public static final BIChartDefine BAR1 = new BIChartDefine(ChartTypeValueCollection.COLUMN, 0);
        public static final BIChartDefine ACCUMULATED_BAR1 = new BIChartDefine(ChartTypeValueCollection.COLUMN, 1);
        public static final BIChartDefine SQUARE1 = new BIChartDefine(ChartTypeValueCollection.AREA, 0);
        public static final BIChartDefine PIE1 = new BIChartDefine(ChartTypeValueCollection.PIE, 0);
        public static final BIChartDefine TIAO1 = new BIChartDefine(ChartTypeValueCollection.BAR, 0);
        public static final BIChartDefine ACCUMULATED_TIAO1 = new BIChartDefine(ChartTypeValueCollection.BAR, 1);
        public static final BIChartDefine DONUT1 = new BIChartDefine(ChartTypeValueCollection.DONUT, 0);
        public static final BIChartDefine RADAR1 = new BIChartDefine(ChartTypeValueCollection.RADAR, 0);
        //2d

        public static final BIChartDefine LINE = new BIChartDefine(ChartTypeValueCollection.LINE, 0);
        public static final BIChartDefine DASHBOARD = new BIChartDefine(ChartTypeValueCollection.METER, 0);
        public static final BIChartDefine MAP = new BIChartDefine(ChartTypeValueCollection.MAP, 0);

    }
}