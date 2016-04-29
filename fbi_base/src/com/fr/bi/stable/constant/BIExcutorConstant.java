package com.fr.bi.stable.constant;

import com.fr.base.Formula;
import com.fr.base.Parameter;
import com.fr.base.TemplateUtils;
import com.fr.chart.base.ChartCustomRendererType;
import com.fr.data.util.function.DataFunction;
import com.fr.data.util.function.NoneFunction;
import com.fr.js.JavaScriptImpl;
import com.fr.js.NameJavaScriptGroup;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/10/27.
 */
public class BIExcutorConstant {
    /**
     * 分页类型
     */
    public static final class PAGINGTYPE {

        public static final int NONE = -1;//不分页

        public static final int GROUP20 = 0;//默认分页类型

        public static final int GROUP5 = 1;

        public static final int GROUP100 = 2;//明细表用的分页类型
    }

    /**
     * 图表
     */
    public static final class CHART {

        public static final int BAR = 0x0;

        public static final int ACCUMULATED_BAR = 0x1;

        public static final int LINE = 0x2;

        public static final int SQUARE = 0x3;

        public static final int PIE = 0x4;

        public static final int TIAO = 0x5;

        public static final int ACCUMULATED_TIAO = 0x6;

        public static final int DASHBOARD = 0x7;

        public static final int MAP = 0x8;

        public static final int DONUT = 0xf;

        public static final int RADAR = 0x9;

        public static DataFunction getChartDataFunction() {
            return new NoneFunction();
        }

        public static ChartCustomRendererType getCombChartType(int chartType) {
            switch (chartType) {
                case CHART.BAR:
                    return ChartCustomRendererType.BAR_RENDERER;
                case CHART.LINE:
                    return ChartCustomRendererType.LINE_RENDERER;
                case CHART.ACCUMULATED_BAR:
                    return ChartCustomRendererType.BAR_STACK;
                case CHART.SQUARE:
                    return ChartCustomRendererType.AREA_STACK;
                case CHART.PIE:
                    return ChartCustomRendererType.BAR_RENDERER;
                case CHART.TIAO:
                    return ChartCustomRendererType.BAR_RENDERER;
                case CHART.ACCUMULATED_TIAO:
                    return ChartCustomRendererType.BAR_STACK;
                case CHART.DASHBOARD:
                    return ChartCustomRendererType.BAR_RENDERER;
                default:
                    return ChartCustomRendererType.BAR_RENDERER;
            }
        }

        /**
         * 创建图表的超链接
         *
         * @param row        行
         * @param column     列
         * @param widgetName 控件名
         * @return 图表的超链接
         */
        public static NameJavaScriptGroup createChartHyperLink(String row,
                                                               String column, String widgetName) {
            JavaScriptImpl js = new JavaScriptImpl();
            js.setParameters(new Parameter[]{
                    new Parameter("cate", new Formula("Category")),
                    new Parameter("series", new Formula("Series")),
                    new Parameter("value", new Formula("Value")),
            });
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("widgetName", widgetName);
            JSONObject jo = new JSONObject();
            try {
                if (row != null) {
                    jo.put("cateName", row);
                }
                if (column != null) {
                    jo.put("seriesName", column);
                }
            } catch (JSONException ignore) {

            }
            map.put("config", jo.toString());
            String content = StringUtils.EMPTY;
            try {
                content = TemplateUtils.renderTemplate("/com/fr/bi/stable/tpl/hyperlink4chart.js", map);
            } catch (IOException ignore) {

            }
            js.setContent(content);
            return new NameJavaScriptGroup(js);
        }

        /**
         * 创建地图的超链接
         *
         * @param widgetName 控件名
         * @return 地图的超链接
         */
        public static NameJavaScriptGroup createChartMapHyperLink(String widgetName) {
            JavaScriptImpl js = new JavaScriptImpl();
            js.setParameters(new Parameter[]{
                    new Parameter("cate", new Formula("Area_Name")),
                    new Parameter("series", new Formula("Series")),
                    new Parameter("value", new Formula("Value")),
            });
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("widgetName", widgetName);
            String content = StringUtils.EMPTY;
            try {
                content = TemplateUtils.renderTemplate("/com/fr/bi/stable/tpl/hyperlink4map.js", map);
            } catch (IOException ignore) {

            }
            js.setContent(content);
            return new NameJavaScriptGroup(js);
        }

        public final static class WARINGLINE {

            public final static int AVG = 0x0;

            public final static int CUSTOM = 0x1;

            public final static int TOTAL = 0x2;
        }
    }

    public static final class CHARTSTYLE {
        public static final int SERICEPOSITION_DOWN = 0;
        public static final int SERICEPOSITION_RIGHT = 1;

        public static final int CLASSIFYDIRECTION_HORIZONTAL = 0;
        public static final int CLASSIFYDIRECTION_VERTICAL = 1;

        public static final int HIDE_DATA_SHEET = 0;
        public static final int SHOW_DATA_SHEET = 1;

        public static final int VALUE_UNIT_NONE = 0;
        public static final int VALUE_UNIT_TEN_THOUSAND = 1;
        public static final int VALUE_UNIT_MILLION = 2;
        public static final int VALUE_UNIT_HUNDRED_MILLION = 3;
    }
}