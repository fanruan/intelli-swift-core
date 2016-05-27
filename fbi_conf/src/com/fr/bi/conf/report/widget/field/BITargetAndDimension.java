package com.fr.bi.conf.report.widget.field;


import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.base.provider.NameProvider;
import com.fr.bi.base.provider.ParseJSONWithUID;
import com.fr.bi.common.BICoreService;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.js.NameJavaScriptGroup;

public interface BITargetAndDimension extends ParseJSONWithUID, NameProvider, BICoreService {

    boolean useHyperLink();

    NameJavaScriptGroup createHyperLinkNameJavaScriptGroup(Object v);

    /**
     * 获取statisticElement
     *
     * @return
     */
    BusinessField getStatisticElement();

    /**
     * 索引键值
     *
     * @param column
     * @return
     */
    BIKey createKey(BusinessField column);

    /**
     * 所在表
     *
     * @return
     */
    BusinessTable createTableKey();

    /**
     * 所在字段
     *
     * @return
     */
    CubeFieldSource createColumnKey();

    boolean isUsed();
}