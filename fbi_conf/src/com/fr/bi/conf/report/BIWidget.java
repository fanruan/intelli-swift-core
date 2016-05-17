package com.fr.bi.conf.report;

import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.base.provider.ParseJSONWithUID;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.fr.json.JSONObject;
import com.fr.main.impl.WorkBook;
import com.fr.stable.FCloneable;

import java.util.List;

/**
 * BI的控件接口
 * 处理控件各项属性，生成聚合报表块
 *
 * @author Daniel-pc
 */
public interface BIWidget extends ParseJSONWithUID, FCloneable {

    /**
     * 返回Widget的ID
     *
     * @return 注释
     */
    String getWidgetName();

    void setWidgetName(String newName);


    <T extends BITargetAndDimension> T[] getDimensions();

    <T extends BITargetAndDimension> T[] getViewDimensions();

    <T extends BITargetAndDimension> T[] getTargets();

    <T extends BITargetAndDimension> T[] getViewTargets();

    /**
     * @return 所有用到的表
     */
    List<Table> getUsedTableDefine();

    /**
     * @return 所有用到的字段
     */
    List<BIField> getUsedFieldDefine();

    int isOrder();

    /**
     * @return 计算结果Node，页码信息
     */
    JSONObject createDataJSON(BISessionProvider session) throws Exception;

    /**
     * 生成控件对应的定义模板用于计算
     *
     * @return 注释
     */
    WorkBook createWorkBook(BISessionProvider session);

    int getType();


}