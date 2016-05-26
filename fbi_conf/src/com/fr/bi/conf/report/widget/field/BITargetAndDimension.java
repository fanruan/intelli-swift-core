package com.fr.bi.conf.report.widget.field;


import com.fr.bi.common.BICoreService;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.base.provider.NameProvider;
import com.fr.bi.base.provider.ParseJSONWithUID;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.BITable;
import com.fr.js.NameJavaScriptGroup;

public interface BITargetAndDimension extends ParseJSONWithUID, NameProvider, BICoreService {

    boolean useHyperLink();

    NameJavaScriptGroup createHyperLinkNameJavaScriptGroup(Object v);

    /**
     * 获取statisticElement
     *
     * @return
     */
    BIDataColumn getStatisticElement();

    /**
     * 索引键值
     *
     * @return
     * @param column
     */
    BIKey createKey(BIField column);

    /**
     * 所在表
     *
     * @return
     */
    BITable createTableKey();

    /**
     * 所在字段
     *
     * @return
     */
    BIField createColumnKey();

    boolean isUsed();
}