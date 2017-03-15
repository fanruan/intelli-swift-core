package com.fr.bi.conf.fs.tablechartstyle;

/**
 * Created by windy on 2016/10/12.
 */
public class BITitleBackgroundAttr extends BIAbstractBackgroundAttr {

    public BITitleBackgroundAttr() {
    }

    public BITitleBackgroundAttr(String value, int type) {
        super(value, type);
    }

    @Override
    public String getTag() {
        return "TitleBackground";
    }
}
