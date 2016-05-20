package com.fr.bi.cal.analyze.report.report.widget.detail;

import com.fr.bi.common.BICoreService;
import com.fr.json.JSONParser;

/**
 * Created by GUY on 2015/4/9.
 */
public interface BIDetailSetting extends JSONParser,BICoreService {

    /**
     * 获取行内容
     *
     * @return
     */
    String[] getView();

    boolean isFreeze();

    int isOrder();
}