package com.fr.bi.conf.data.source.operator;

import com.fr.bi.conf.data.source.operator.add.FieldFormulaOperator;
import com.fr.bi.conf.data.source.operator.add.FieldGroupOperator;
import com.fr.bi.conf.data.source.operator.add.selfrelation.OneFieldIsometricUnionRelationOperator;
import com.fr.bi.conf.data.source.operator.add.selfrelation.OneFieldUnionRelationOperator;
import com.fr.bi.conf.data.source.operator.add.selfrelation.TwoFieldUnionRelationOperator;
import com.fr.bi.conf.data.source.operator.create.*;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GUY on 2015/3/5.
 */
public class OperatorFactory {

    public static IETLOperator createOperatorByXMLTagName(String tag) {
        if (ComparatorUtils.equals(tag, FieldFormulaOperator.XML_TAG)) {
            return new FieldFormulaOperator();
        } else if (ComparatorUtils.equals(tag, FieldGroupOperator.XML_TAG)) {
            return new FieldGroupOperator();
        } else if (ComparatorUtils.equals(tag, OneFieldUnionRelationOperator.XML_TAG)) {
            return new OneFieldUnionRelationOperator();
        } else if (ComparatorUtils.equals(tag, OneFieldIsometricUnionRelationOperator.XML_TAG)) {
            return new OneFieldIsometricUnionRelationOperator();
        } else if (ComparatorUtils.equals(tag, TwoFieldUnionRelationOperator.XML_TAG)) {
            return new TwoFieldUnionRelationOperator();
        } else if (ComparatorUtils.equals(tag, TableColumnFilterOperator.XML_TAG)) {
            return new TableColumnFilterOperator();
        } else if (ComparatorUtils.equals(tag, TableColumnRowTransOperator.XML_TAG)) {
            return new TableColumnRowTransOperator();
        } else if (ComparatorUtils.equals(tag, TableFilterOperator.XML_TAG)) {
            return new TableFilterOperator();
        } else if (ComparatorUtils.equals(tag, TableSumByGroupOperator.XML_TAG)) {
            return new TableSumByGroupOperator();
        } else if (ComparatorUtils.equals(tag, TableUnionOperator.XML_TAG)) {
            return new TableUnionOperator();
        } else if (ComparatorUtils.equals(tag, TableJoinOperator.XML_TAG)) {
            return new TableJoinOperator();
        }
        return null;
    }

    /**
     * 创建etloprator
     *
     * @param jo json对象
     * @return etloprator对象
     */
    public static List<IETLOperator> createOperatorsByJSON(JSONObject jo, long userId) {
        List<IETLOperator> operators = new ArrayList<IETLOperator>();
        try {
            String type = jo.getString(BIJSONConstant.JSON_KEYS.ETL_TYPE);
            JSONObject op = jo.getJSONObject(BIJSONConstant.JSON_KEYS.ETL_VALUE);
            if (ComparatorUtils.equals(type, "formula")) {
                JSONArray ja = op.getJSONArray("formulas");
                for (int i = 0; i < ja.length(); i++) {
                    IETLOperator opr = new FieldFormulaOperator(userId);
                    opr.parseJSON(ja.getJSONObject(i));
                    operators.add(opr);
                }
            } else if (ComparatorUtils.equals(type, "new_group")) {
                JSONArray ja = op.getJSONArray("new_groups");
                for (int i = 0; i < ja.length(); i++) {
                    IETLOperator opr = new FieldGroupOperator(userId);
                    opr.parseJSON(ja.getJSONObject(i));
                    operators.add(opr);
                }
            } else if (ComparatorUtils.equals(type, "circle")) {
                IETLOperator opr = null;
                if (op.has("parentid_field_name") && StringUtils.isNotEmpty(op.optString("parentid_field_name"))) {
                    opr = new TwoFieldUnionRelationOperator(userId);
                } else {
                    if (op.has("field_length") && StringUtils.isNotEmpty(op.optString("field_length"))) {
                        opr = new OneFieldIsometricUnionRelationOperator(userId);
                    } else {
                        opr = new OneFieldUnionRelationOperator(userId);
                    }
                }
                opr.parseJSON(op);
                operators.add(opr);
            } else {
                IETLOperator operator = createSingleOperatorByType(type, userId);
                if (operator != null) {
                    operator.parseJSON(op);
                    operators.add(operator);
                }
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return operators;
    }

    private static IETLOperator createSingleOperatorByType(String type, long userId) {
        IETLOperator operator = null;
        if (ComparatorUtils.equals(type, "join")) {
            operator = new TableJoinOperator(userId);
        } else if (ComparatorUtils.equals(type, "union")) {
            operator = new TableUnionOperator(userId);
        } else if (ComparatorUtils.equals(type, "convert")) {
            operator = new TableColumnRowTransOperator(userId);
        } else if (ComparatorUtils.equals(type, "partial")) {
            operator = new TableFilterOperator(userId);
        } else if (ComparatorUtils.equals(type, "filter")) {
            operator = new TableColumnFilterOperator(userId);
        } else if (ComparatorUtils.equals(type, "group")) {
            operator = new TableSumByGroupOperator(userId);
        }
        return operator;
    }

    /**
     * 根据oprators创建json
     *
     * @param jo       json对象
     * @param oprators etloprator对象
     */
    public static void createJSONByOperators(JSONObject jo, List<IETLOperator> oprators) {
        String op = oprators.get(0).xmlTag();
        try {
            if (ComparatorUtils.equals(op, TableJoinOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "join");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, TableUnionOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "union");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, TableColumnRowTransOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "convert");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, TableFilterOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "partial");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, TableColumnFilterOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "filter");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, TableSumByGroupOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "group");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, TwoFieldUnionRelationOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "circle");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, OneFieldIsometricUnionRelationOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "circle");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, OneFieldUnionRelationOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "circle");
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, oprators.get(0).createJSON());
            } else if (ComparatorUtils.equals(op, FieldFormulaOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "formula");
                JSONArray ja = new JSONArray();
                for (int i = 0; i < oprators.size(); i++) {
                    ja.put(oprators.get(i).createJSON());
                }
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, new JSONObject().put("formulas", ja));
            } else if (ComparatorUtils.equals(op, FieldGroupOperator.XML_TAG)) {
                jo.put(BIJSONConstant.JSON_KEYS.ETL_TYPE, "new_group");
                JSONArray ja = new JSONArray();
                for (int i = 0; i < oprators.size(); i++) {
                    ja.put(oprators.get(i).createJSON());
                }
                jo.put(BIJSONConstant.JSON_KEYS.ETL_VALUE, new JSONObject().put("new_groups", ja));
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}