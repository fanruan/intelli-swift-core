package com.finebi.base.data;


import com.finebi.base.data.xml.item.XmlItem;

/**
 * Created by andrew_asa on 2017/10/11.
 */
public class AbstractBiWidget implements BIWidget {

    @XmlItem
    String widgetId;

    @XmlItem
    long initTime;

    @XmlItem
    String widgetName;

    public AbstractBiWidget() {

    }

    public void setWidgetId(String widgetId) {

        this.widgetId = widgetId;
    }

    public void setInitTime(long initTime) {

        this.initTime = initTime;
    }

    public void setWidgetName(String widgetName) {

        this.widgetName = widgetName;
    }

    public String getWidgetId() {

        return widgetId;
    }

    public long getInitTime() {

        return initTime;
    }

    public String getWidgetName() {

        return widgetName;
    }
}
