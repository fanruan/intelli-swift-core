package com.fr.bi.conf.fs.tablechartstyle;

/**
 * Created by windy on 2016/10/12.
 */
public class BIWidgetBackgroundAttr extends BIAbstractBackgroundAttr {

    public BIWidgetBackgroundAttr() {
    }

    public BIWidgetBackgroundAttr(String value, int type) {
        super(value, type);
    }

    @Override
    public String getTag() {
        return "WidgetBackground";
    }
}
