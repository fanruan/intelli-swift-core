/**
 *
 */
package com.fr.bi.conf;

import com.fr.base.FRContext;
import com.fr.base.chart.ChartRegisterForBI;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.FUNC;
import com.fr.general.VT4FR;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.LicUtils;

/**
 * @author jerry
 */
public final class VT4FBI {

    public static final FUNC BI_BASIC = new BIFUNC(60) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_basic";
        }

    };
    public static final FUNC BI_MUTITABLE = new BIFUNC(61) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_multitable";
        }

    };
    public static final FUNC BI_INCREMENTUPDATE = new BIFUNC(62) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_increment_update";
        }

    };
    public static final FUNC BI_OLAP = new BIFUNC(63) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_OLAP";
        }

    };
    public static final FUNC BI_CALCULATETARGET = new BIFUNC(64) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_calculate_target";
        }

    };
    public static final FUNC BI_REPORTSHEAR = new BIFUNC(65) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_report_share";
        }

    };
    public static final FUNC BI_CONTROL = new BIFUNC(66) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_control";
        }

    };
    public static final FUNC BI_GENERALCONTROL = new BIFUNC(67) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_general_control";
        }

    };
    public static final FUNC BI_EXCELVIEW = new BIFUNC(68) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_excel_view";
        }

    };
    public static final FUNC BI_MOBILE = new BIFUNC(69) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_mobile";
        }

    };
    public static final FUNC BI_MUTIWIDGET = new BIFUNC(70) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_multi_widget";
        }

    };
    public static final FUNC BI_CHARTLINK = new BIFUNC(71) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_chart_link";
        }

    };
    public static final FUNC BI_MAP = new BIFUNC(72) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_map";
        }

    };
    public static final FUNC BI_FS = new BIFUNC(73) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_fs";
        }

    };
    public static final FUNC BI_MOBILE_FS = new BIFUNC(74) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_mobile_fs";
        }

    };
    public static final FUNC BI_BIG_DATA = new BIFUNC(75) {

        @Override
        public String getLocaleKey() {
            return "fbi_func_big_data";
        }

    };

    /**
     * 心人的代码检测\n
     *
     * @return JSON格式
     */
    public static JSONObject toJSONObject() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("hasLic", VT4FR.isLicAvailable());
            jo.put("supportBasic", supportBasicDesign());
            jo.put("supportDatabaseUnion", supportDatabaseUnion());
            jo.put("supportIncrementUpdate", supportIncrementUpdate());
            jo.put("supportOLAPTable", supportOLAPTable());
            jo.put("supportCalcuateTarget", supportCalcuateTarget());
            jo.put("supportReportShare", supportReportShare());
            jo.put("supportSimpleControl", supportSimpleControl());
            jo.put("supportGeneralControl", supportGeneralControl());
            jo.put("supportExcelView", supportExcelView());
            jo.put("supportMobileClient", supportMobileClient());
            jo.put("supportMultiStatisticsWidget", supportMultiStatisticsWidget());
            jo.put("supportBigData", supportBigData());
        } catch (JSONException e) {
            BILogger.getLogger().error(e.getMessage());
        }
        return jo;
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持大数据
     */
    public static final boolean supportBigData() {
        return BI_BIG_DATA.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持基本版功能
     */
    public static final boolean supportBasicDesign() {
        return BI_BASIC.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持多表
     */
    public static final boolean supportDatabaseUnion() {
        return BI_MUTITABLE.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持增量更新
     */
    public static final boolean supportIncrementUpdate() {
        return BI_INCREMENTUPDATE.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return OLAP表格
     */
    public static final boolean supportOLAPTable() {
        return BI_OLAP.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持计算指标
     */
    public static final boolean supportCalcuateTarget() {
        return BI_CALCULATETARGET.support();
    }

    /**
     * 恶心人的代码检测\n
     * 恶心人的代码检测
     *
     * @return 支持报表分享
     */
    public static final boolean supportReportShare() {
        return BI_REPORTSHEAR.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持简单控件
     */
    public static final boolean supportSimpleControl() {
        return BI_CONTROL.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持通用查询
     */
    public static final boolean supportGeneralControl() {
        return BI_GENERALCONTROL.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持ExcelView
     */
    public static final boolean supportExcelView() {
        return BI_EXCELVIEW.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持移动端
     */
    public static final boolean supportMobileClient() {
        return BI_MOBILE.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持多组件
     */
    public static final boolean supportMultiStatisticsWidget() {
        return BI_MUTIWIDGET.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持多组件
     */
    public static final boolean supportChartLink() {
        return BI_CHARTLINK.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持多组件
     */
    public static final boolean supportBIMap() {
        return BI_MAP.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持多组件
     */
    public static final boolean supportBIFS() {
        return BI_FS.support();
    }

    /**
     * 恶心人的代码检测\n
     *
     * @return 支持多组件
     */
    public static final boolean supportBIMOBILEFS() {
        return BI_MOBILE_FS.support();
    }

    /**
     * 处理L文件
     *
     * @return 空
     */
    public static void dealWithLic() {
        FRContext.getCurrentEnv().setLicName("FineBI.lic");
        LicUtils.resetBytes();
        LicUtils.retryLicLock();
        FUNC.refreshFuntions();
        //FSRegisterForBI.setSupportFS(supportBIFS());
        ChartRegisterForBI.setSupportDynamicChart(supportChartLink());
    }

    private static class BIFUNC extends FUNC {
        private int pos;

        private BIFUNC(int pos) {
            super();
            this.pos = pos;
        }

        /* (non-Javadoc)
         * @see com.fr.general.VT4FR.FUNC#marker()
         */
        @Override
        public int marker() {
            return pos;
        }

        /* (non-Javadoc)
         * @see com.fr.general.VT4FR.FUNC#enterpriseSupported()
         */
        @Override
        public boolean enterpriseSupported() {
            return false;
        }

        /* (non-Javadoc)
         * @see com.fr.general.VT4FR.FUNC#professionalSupported()
         */
        @Override
        public boolean professionalSupported() {
            return false;
        }

        /* (non-Javadoc)
         */
        @Override
        public boolean standardSupported() {
            return false;
        }

        /* (non-Javadoc)
         */
        @Override
        public boolean developmentSupported() {
            return false;
        }

        /* (non-Javadoc)
         * @see com.fr.general.VT4FR.FUNC#basicSupported()
         */
        @Override
        public boolean basicSupported() {
            return false;
        }

    }
}