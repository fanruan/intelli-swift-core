package com.fr.bi.conf.report;

import com.fr.bi.conf.session.BISessionProvider;
import com.fr.bi.base.provider.ParseJSONWithUID;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.json.JSONObject;
import com.fr.main.impl.WorkBook;

import java.util.List;


/**
 * 根据控件生成BI报表内容
 *
 * @author Daniel-pc
 */
public interface BIReport extends ParseJSONWithUID {

    /**
     * 生成BI所展示的报表定义
     *
     * @param index 序号
     * @return 报表定义
     */
    public WorkBook createWorkBook(int index, BISessionProvider session);

    /**
     * @param index 序号
     * @return name 名字
     */
    public String getWidgetName(int index);

    /**
     * 返回Widget名字所对应的序列号
     *
     * @param name 名字
     * @return 序号
     */
    public int getWidgetIndexByName(String name);

    /**
     * 返回Widget名字所对应的widget
     *
     * @param name 名字
     * @return widget对象
     */
    public BIWidget getWidgetByName(String name);

    /**
     * 更改Widget名字
     *
     * @param newName 新的名字
     * @param oldName 旧的名字
     */

    public void renameWidgetName(String oldName, String newName);

    /**
     * 设置Widget,如果index不在范围,则增加Widget
     *
     * @param index  序号
     * @param widget 控件
     */
    public void setWidget(int index, BIWidget widget);

    public List<Table> getUsedTableDefine();

    public List<BIField> getUsedFieldDefine();

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception
     */
    public void parseJSON(JSONObject jo, long userId, ICubeDataLoader loader) throws Exception;

    public List<BIField> getControlColumns();

    /**
     * 删除控件
     *
     * @param widgetName 名字
     */
    public void removeWidget(String widgetName);
}