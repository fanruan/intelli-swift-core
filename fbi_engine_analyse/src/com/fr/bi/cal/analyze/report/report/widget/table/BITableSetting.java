package com.fr.bi.cal.analyze.report.report.widget.table;

import com.fr.bi.common.BICoreService;
import com.fr.json.JSONParser;

/**
 * Created by GUY on 2015/4/9.
 */
public interface BITableSetting extends JSONParser, BICoreService {

    /**
     * 获取行内容
     *
     * @return
     */
    String[] getRow();

    /**
     * 获取列内容
     *
     * @return
     */
    String[] getColumn();

    /**
     * 显示的指标
     *
     * @return
     */
    String[] getSummary();

    boolean useRealData();

    int isOrder();

}