package com.fr.bi.cal.analyze.report;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.report.report.BIWidgetFactory;
import com.fr.bi.conf.report.BIReport;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.conf.session.BISessionProvider;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.main.impl.WorkBook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 根据控件生成BI报表内容
 *
 * @author Daniel-pc
 */
public class BIReportor implements BIReport {
    private List<BIWidget> widgets = new ArrayList<BIWidget>();

    private List<BusinessField> controlColumns = new ArrayList<BusinessField>();

    public int getWidgetsCount() {
        return widgets.size();
    }

    public BIWidget getWidget(int index) {
        return widgets.get(index);
    }

    /**
     * 生成BI所展示的报表定义
     *
     * @param index 序号
     * @return 报表定义
     */
    @Override
    public WorkBook createWorkBook(int index, BISessionProvider session) {
        BIWidget widget = widgets.get(index);
        return widget.createWorkBook(session);
    }

    /**
     * @param index 序号
     * @return name 名字
     */
    @Override
    public String getWidgetName(int index) {
        return widgets.get(index).getWidgetName();
    }

    /**
     * 返回Widget名字所对应的序列号
     *
     * @param name 名字
     * @return 序号
     */
    @Override
    public int getWidgetIndexByName(String name) {
        for (int i = 0, len = getWidgetsCount(); i < len; i++) {
            if (ComparatorUtils.equals(widgets.get(i).getWidgetName(), name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 返回Widget名字所对应的widget
     *
     * @param name 名字
     * @return widget对象
     */
    @Override
    public BIWidget getWidgetByName(String name) {
        for (int i = 0, len = getWidgetsCount(); i < len; i++) {
            if (ComparatorUtils.equals(widgets.get(i).getWidgetName(), name)) {
                return widgets.get(i);
            }
        }
        return null;
    }

    /**
     * 更改Widget名字
     *
     * @param newName 新的名字
     * @param oldName 旧的名字
     */

    @Override
    public void renameWidgetName(String oldName, String newName) {
        if (ComparatorUtils.equals(oldName, newName)) {
            return;
        }

        for (int i = 0; i < getWidgetsCount(); i++) {

            BIWidget biWidget = widgets.get(i);
            if (ComparatorUtils.equals(biWidget.getWidgetName(), oldName)) {
                biWidget.setWidgetName(newName);
            }
        }
    }

    /**
     * 设置Widget,如果index不在范围,则增加Widget
     *
     * @param index  序号
     * @param widget 控件
     */
    @Override
    public void setWidget(int index, BIWidget widget) {
        synchronized (this) {
            if (index >= 0 && index < getWidgetsCount()) {
                widgets.set(index, widget);
            } else {
                widgets.add(widget);
            }
        }
    }

    @Override
    public List<BusinessTable> getUsedTableDefine() {
        List<BusinessTable> result = new ArrayList<BusinessTable>();
        for (BIWidget widget : widgets) {
            result.addAll(widget.getUsedTableDefine());
        }
        if (controlColumns != null) {

            Iterator<BusinessField> it = controlColumns.iterator();
            while (it.hasNext()) {
                result.add(it.next().getTableBelongTo());
            }
        }
        return result;
    }

    @Override
    public List<BusinessField> getUsedFieldDefine() {
        List<BusinessField> result = new ArrayList<BusinessField>();
        for (BIWidget widget : widgets) {
            result.addAll(widget.getUsedFieldDefine());
        }
        if (controlColumns != null) {
            result.addAll(controlColumns);
        }
        return result;
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        this.parseJSON(jo, userId, BICubeManager.getInstance().fetchCubeLoader(userId));
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo, long userId, ICubeDataLoader loader) throws Exception {
        widgets.clear();
        if (jo.has("widget")) {
            JSONArray ja = jo.getJSONArray("widget");
            for (int i = 0, len = ja.length(); i < len; i++) {
                BIWidget widget = BIWidgetFactory.parseWidget(ja.getJSONObject(i), userId, loader);
                widgets.add(widget);
            }
        }
        if (jo.has("detail")) {
            JSONArray ja = jo.getJSONArray("detail");
            for (int i = 0, len = ja.length(); i < len; i++) {
                BIWidget widget = BIWidgetFactory.parseWidget(ja.getJSONObject(i), userId, loader);
                widgets.add(widget);
            }
        }
        controlColumns.clear();
        if (jo.has("control")) {
            JSONArray ja = jo.getJSONArray("control");
            for (int i = 0, len = ja.length(); i < len; i++) {
                JSONObject cj = ja.getJSONObject(i);
                if (cj.has("config")) {
                    JSONArray columnJa = cj.getJSONArray("config");
                    for (int j = 0; j < columnJa.length(); j++) {
                        BusinessField c = new BIBusinessField();
                        c.parseJSON(columnJa.getJSONObject(j));
                        controlColumns.add(c);
                    }
                }
            }
        }
    }

    @Override
    public List<BusinessField> getControlColumns() {
        return controlColumns;
    }

    /**
     * 删除控件
     *
     * @param widgetName 名字
     */
    @Override
    public void removeWidget(String widgetName) {
        synchronized (this) {
            List<BIWidget> widgetList = new ArrayList<BIWidget>();
            for (int i = 0, len = getWidgetsCount(); i < len; i++) {
                BIWidget widget = widgets.get(i);
                if (!ComparatorUtils.equals(widget.getWidgetName(), widgetName)) {
                    widgetList.add(widget);
                }
            }
            this.widgets = widgetList;
        }
    }
}