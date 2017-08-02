package com.fr.bi.cal.analyze.report.report.widget.util;

import com.fr.bi.cal.analyze.report.report.widget.DetailWidget;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.conf.report.SclCalculator;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.json.JSONObject;

import java.util.Arrays;

import static com.fr.bi.stable.constant.BIReportConstant.WIDGET.*;

/**
 * widget 数据样式分离 这个工具类通过传过来的biWidgetConf和data处理各种类型的图表，生成view层需要的数据
 * Created by astronaut007 on 2017/7/15.
 * edit by kary on 2017/7/21 将计算逻辑提一个接口出来进行处理
 */
public class BIWidgetUtils {

    public static JSONObject generateResult4View(BIWidgetConf widgetConf, JSONObject data) throws Exception {
        return getWidget(widgetConf).calculateSCData(widgetConf, data);
    }

    private static SclCalculator getWidget(BIWidgetConf widgetConf) throws Exception {
        if (isTableWidget(widgetConf.getType())) {
            return getTableWidget(widgetConf);
        } else {
            return getVanWidget(widgetConf);
        }
    }

    private static boolean isTableWidget(int type) {
        return Arrays.asList(TABLE, CROSS_TABLE, COMPLEX_TABLE, DETAIL).contains(type);
    }

    private static SclCalculator getVanWidget(BIWidgetConf widgetConf) throws Exception {
        return BIWidgetFactory.createVanWidgetByType(WidgetType.parse(widgetConf.getType()));
    }

    private static SclCalculator getTableWidget(BIWidgetConf widgetConf) throws Exception {
        SclCalculator res = null;
        switch (WidgetType.parse(widgetConf.getType())) {
            case TABLE:
            case CROSS_TABLE:
            case COMPLEX_TABLE:
                res = new TableWidget();
                break;
            case DETAIL:
                res = new DetailWidget();
                break;
            default:
                res = new TableWidget();
        }
        return res;
    }
}
