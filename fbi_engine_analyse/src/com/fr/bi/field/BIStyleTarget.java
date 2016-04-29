package com.fr.bi.field;

import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

public abstract class BIStyleTarget extends BIAbstractTargetAndDimension {

    /**
     *
     */
    private static final long serialVersionUID = -125512176576208782L;
    private TargetStyle style;

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        if (jo.has("style_conditions")) {
            style = new TargetStyle();
            style.parseJSON(jo.getJSONObject("style_conditions"));
        }
    }

    public TargetStyle getStyle() {
        return style;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIStyleTarget)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        BIStyleTarget that = (BIStyleTarget) o;

        if (ComparatorUtils.equals(style, that.style)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (style != null ? style.hashCode() : 0);
        return result;
    }
}