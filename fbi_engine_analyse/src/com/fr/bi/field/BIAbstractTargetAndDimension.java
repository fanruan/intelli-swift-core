package com.fr.bi.field;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.BIID;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.style.ChartSetting;
import com.fr.bi.conf.report.widget.field.BITargetAndDimension;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.BIFormulaUtils;
import com.fr.general.ComparatorUtils;
import com.fr.js.NameJavaScriptGroup;
import com.fr.js.WebHyperlink;
import com.fr.json.JSONObject;
import com.fr.stable.ParameterProvider;
import com.fr.stable.StringUtils;

import java.net.URLEncoder;

public abstract class BIAbstractTargetAndDimension extends BIID implements BITargetAndDimension {

    /**
     *
     */
    private static final long serialVersionUID = -6531968195020108676L;
    @BICoreField
    protected BusinessField column;
    private String hyperLinkExpression = StringUtils.EMPTY;
    private boolean useHyperLink = false;
    private boolean used = true;
    private ChartSetting chartSetting;

    @Override
    public BusinessField getStatisticElement() {
        return column;
    }

    @Override
    public BusinessTable createTableKey() {
        return column == null ? null : column.getTableBelongTo();
    }

    @Override
    public BusinessField createColumnKey() {
        return column;
    }

    @Override
    public BIKey createKey(BusinessField column) {
        return new IndexKey(column.getFieldName());
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
        boolean expressEmpty = StringUtils.isEmpty(hyperLinkExpression);
        return !expressEmpty && useHyperLink;
    }

    private String getHyperLink(Object v) {
        String link = hyperLinkExpression;
        if (hyperLinkExpression != null && !hyperLinkExpression.isEmpty()) {
            try {
                String[] relatedParaNames = BIFormulaUtils.getRelatedParaNames(hyperLinkExpression);
                for (String relatedParaName : relatedParaNames) {
                    link = link.replace("${" + relatedParaNames[0] + "}", URLEncoder.encode(v.toString()));
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
            }
        }
        return link;
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
            if (fieldJo.has("fieldId")) {
                //获取分析用的字段,恶心的螺旋分析处理
                column = BIModuleUtils.getAnalysisBusinessFieldById(new BIFieldID(fieldJo.getString("fieldId")));
            }
        }
        chartSetting = new ChartSetting();
        chartSetting.parseJSON(jo);
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
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }

    @Override
    public boolean isUsed() {
        return used;
    }

    @Override
    //FIXME 这里的结构太诡异了，保存了MD5，还有刷新方法，现在如果中间表删除，则不影响螺旋分析的生成，不知道到底好不好
    public void refreshColumn() {
        if (column != null) {
            BusinessField c = BIModuleUtils.getAnalysisBusinessFieldById(column.getFieldID());
            if (c != null) {
                column = c;
            }
        }
    }

    public ChartSetting getChartSetting() {
        return chartSetting;
    }

    @Override
    public String getId() {
        return super.getValue();
    }

    @Override
    public String getText() {
        return super.getText();
    }
}