/**
 *
 */
package com.fr.bi.field.target.filter.formula;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.field.target.filter.AbstractTargetFilter;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FormulaFilter extends AbstractTargetFilter {
    /**
     *
     */
    private static final long serialVersionUID = 5615839692061360681L;
    private static final String XML_TAG = "FormulaFilter";
    private String expression = StringUtils.EMPTY;


    /**
     * 解析json
     *
     * @param jo     json对象
     * @param userid 用户id
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userid) throws Exception {
        if (jo.has(BIJSONConstant.JSON_KEYS.FILTER_VALUE)) {
            expression = jo.getString(BIJSONConstant.JSON_KEYS.FILTER_VALUE);
        }
    }

    /**
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();

        if (!StringUtils.isEmpty(expression)) {
            jo.put(BIJSONConstant.JSON_KEYS.FILTER_VALUE, expression);
            jo.put(BIJSONConstant.JSON_KEYS.FILTER_TYPE, BIReportConstant.FILTER_TYPE.FORMULA);
        }else{
            jo.put(BIJSONConstant.JSON_KEYS.FILTER_TYPE, BIReportConstant.FILTER_TYPE.EMPTY_FORMULA);
        }

        return jo;
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String node = reader.getTagName();
            if (XML_TAG.equals(node)) {
                expression = reader.getAttrAsString("filter_value", StringUtils.EMPTY);
            }
        }
    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        if (StringUtils.isNotEmpty(expression)) {
            writer.attr("filter_value", expression);
        }
        writer.end();
    }


    /**
     * 创建过滤条件的index，用于指标过滤
     *
     *
     * @param target
     * @param loader loader对象
     * @return 分组索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        return createFormulaIndex(loader.getTableIndex(target.getTableSource()));
    }

    private GroupValueIndex createFormulaIndex(ICubeTableService ti) {
        String transExpression = replaceRelatedParaNames();
        Map<String, BIKey> columnMap = BIFormularUtils.createColumnIndexMap(transExpression, ti);
        String formular = "=" + transExpression;
        Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
        Matcher matcher = pat.matcher(formular);
        int parameterCount = 0;
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            formular = formular.replace(matchStr, "$" + String.valueOf(parameterCount));
            parameterCount++;
        }
        Calculator cal = Calculator.createCalculator();
        long rowCount = ti.getRowCount();
		GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();
        for (int row = 0; row < rowCount; row++) {
            boolean res = calculatorFormulaFilterValue(cal, formular, columnMap, row, ti);
            if (res) {
            	gvi.addValueByIndex(row);
            }
        }
        return gvi;
    }

    /**
     * 分析那边传过来的是fieldId, 要把它变成fieldName
     * @return 转换过后的expression副本
     */
    private String replaceRelatedParaNames() {
        String result = expression;
        Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
        Matcher matcher = pat.matcher(expression);
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            if(matchStr.contains(BIReportConstant.FIELD_ID.HEAD)){
                result = expression.replaceAll(matchStr.substring(2, matchStr.length() - 1), BIModuleUtils.getBusinessFieldById(new BIFieldID(matchStr.substring(BIReportConstant.FIELD_ID.HEAD.length() + 2, matchStr.length() - 1))).getFieldName());
            }
        }
        return result;
    }


    /**
     * bool 类型 其他类型 不为空
     *
     * @param c
     * @param formula
     * @param columnMap
     * @param row
     * @return
     */
    private boolean calculatorFormulaFilterValue(Calculator c, String formula, Map<String, BIKey> columnMap, int row, ICubeTableService ti) {
        Object value = BIFormularUtils.getCalculatorValue(c, formula, ti, columnMap, row);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return value != null;
    }

    /**
     * 指标上加的过滤
     *
     * @param target
     * @param loader
     * @param userID
     * @return
     */
    @Override
    public GroupValueIndex createFilterIndex(BusinessTable target, ICubeDataLoader loader, long userID) {
        return createFormulaIndex(loader.getTableIndex(target.getTableSource()));
    }

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + expression.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        FormulaFilter other = (FormulaFilter) obj;
        if (!ComparatorUtils.equals(expression, other.expression)) {
            return false;
        }

        return true;
    }

}