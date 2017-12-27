package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlItem;
import com.finebi.base.data.xml.item.XmlMapItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew_asa on 2017/10/11.
 */
public class DesiginSetting {

    @XmlItem
    long initTime;

    @XmlItem
    long updateTime;

    @XmlItem
    String name;


    @XmlMapItem(keyClass = BIWidget.class,valueFactory = WidgetFactory.class)
    Map<String, BIWidget> widgetMap = new HashMap<String, BIWidget>();

    public DesiginSetting() {

    }

    public void setInitTime(long initTime) {

        this.initTime = initTime;
    }

    public void setUpdateTime(long updateTime) {

        this.updateTime = updateTime;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setWidgetMap(Map<String, BIWidget> widgetMap) {

        this.widgetMap = widgetMap;
    }

    public long getInitTime() {

        return initTime;
    }

    public long getUpdateTime() {

        return updateTime;
    }

    public String getName() {

        return name;
    }

    public Map<String, BIWidget> getWidgetMap() {

        return widgetMap;
    }

    public void addWidgetMap(String key, BIWidget widget) {

        widgetMap.put(key, widget);
    }
}
