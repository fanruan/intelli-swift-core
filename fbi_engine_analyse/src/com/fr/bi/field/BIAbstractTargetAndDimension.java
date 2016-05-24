package com.fr.bi.field;

import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIID;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.BIDataColumn;
import com.fr.bi.conf.report.widget.BIDataColumnFactory;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.js.NameJavaScriptGroup;
import com.fr.js.WebHyperlink;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.ParameterProvider;
import com.fr.stable.StringUtils;

import java.net.URLEncoder;

public abstract class BIAbstractTargetAndDimension extends BIID implements BITargetAndDimension {

    /**
     *
     */
    private static final long serialVersionUID = -6531968195020108676L;
    protected BIDataColumn column;
    private String hyperLinkExpression = StringUtils.EMPTY;
    private boolean useHyperLink = false;
    private boolean used = true;

    @Override
    public BusinessField getStatisticElement() {
        return column;
    }

    @Override
    public BusinessTable createTableKey() {
        return column.getTableBelongTo();
    }

    @Override
    public BusinessField createColumnKey() {
        return column;
    }

    @Override
    public BIKey createKey(BusinessField column) {
        return new IndexKey(column.getFieldName());
    }

    private String getHyperLink(Object v) {
        String link = StringUtils.EMPTY;
        try {
            link = link.replace("${" + Calculator.relatedParameters(hyperLinkExpression)[0] + "}", URLEncoder.encode(v.toString()));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return link;
    }

    @Override
    public NameJavaScriptGroup createHyperLinkNameJavaScriptGroup(Object v) {
        if (!useHyperLink() || v == null) {
            return null;
        }

        WebHyperlink wl = new WebHyperlink(getHyperLink(v));
        ParameterProvider[] parameters = new ParameterProvider[]{};
        wl.setParameters(parameters);
        wl.setExtendParameters(false);
        return new NameJavaScriptGroup(wl);
    }

    @Override
    public boolean useHyperLink() {
        return !StringUtils.isEmpty(hyperLinkExpression) && useHyperLink;
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo);
        if (jo.has("hyperlink")) {
            JSONObject hyperlink = jo.getJSONObject("hyperlink");
            this.hyperLinkExpression = hyperlink.optString("expression", StringUtils.EMPTY);
            this.useHyperLink = hyperlink.getBoolean("used");
        }
        if (jo.has("used")) {
            this.used = jo.getBoolean("used");
        }
        if (jo.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
            JSONObject fieldJo = jo.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
            if (fieldJo.has("field_id")) {
                column = BIDataColumnFactory.createBIDataColumnByFieldID(fieldJo.getString("field_id"), new BIUser(userId));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BIAbstractTargetAndDimension)) {
            return false;
        }
        BIAbstractTargetAndDimension that = (BIAbstractTargetAndDimension) o;
        if (useHyperLink != that.useHyperLink) {
            return false;
        }
        if (!ComparatorUtils.equals(hyperLinkExpression, that.hyperLinkExpression)) {
            return false;
        }
        if (!ComparatorUtils.equals(id, that.id)) {
            return false;
        }
        if (!ComparatorUtils.equals(column, that.column)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = id != null ? id.hashCode() : 0;
        result = prime * result + (hyperLinkExpression != null ? hyperLinkExpression.hashCode() : 0);
        result = prime * result + (useHyperLink ? 1 : 0);

        result = prime * result + (column != null ? column.hashCode() : 0);
        return result;
    }

    @Override
    public boolean isUsed() {
        return used;
    }

}