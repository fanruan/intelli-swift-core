package com.fr.bi.conf.data.source.operator.add;

import com.fr.base.Utils;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GUY on 2015/3/5.
 */
public class FieldFormulaOperator extends AbstractAddColumnOperator {

    public static final String XML_TAG = "FieldFormulaOperator";
    private static final long serialVersionUID = -1675716963282566541L;

    @BICoreField
    protected String expression = StringUtils.EMPTY;

    public FieldFormulaOperator(long userId) {
        super(userId);
    }

    public FieldFormulaOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        jo.put("expression", expression);
        return jo;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jsonObject json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jsonObject) throws Exception {
        super.parseJSON(jsonObject);
        expression = jsonObject.optString("expression", StringUtils.EMPTY);
    }


    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        int rowCount = ti.getRowCount();
        Map<String, BIKey> columnIndexMap = BIFormularUtils.createColumnIndexMap(expression, ti);
        Calculator cal = Calculator.createCalculator();
        String expressionStr = expression;
        Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
        Matcher matcher = pat.matcher(expressionStr);
        int parameterCount = 0;
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            expressionStr = expressionStr.replace(matchStr, "$" + String.valueOf(parameterCount));
            parameterCount++;
        }
        String formular = "=" + expressionStr;
        for (int row = 0; row < rowCount; row++) {
            try {
                Object value = BIFormularUtils.getCalculatorValue(cal, formular, ti, columnIndexMap, row);
                travel.actionPerformed(new BIDataValue(row, startCol, getValueByColumnType(value)));
            } catch (Exception e) {
                BILogger.getLogger().error("incorrect formular");
                travel.actionPerformed(new BIDataValue(row, startCol, null));
            }
        }
        return rowCount;
    }

    private Object getValueByColumnType(Object value) {
        switch (columnType){
            case DBConstant.COLUMN.DATE :
                return ((Date) value).getTime();
            case DBConstant.COLUMN.NUMBER :
                return  ((Number) value).doubleValue();
            default:
                return  Utils.objectToString(value);
        }
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isAttr()) {
            try {
                this.expression = reader.getAttrAsString("expression", StringUtils.EMPTY);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        try {
            writer.attr("expression", expression);
        } catch (Exception e) {
        }
        writer.end();

    }
}