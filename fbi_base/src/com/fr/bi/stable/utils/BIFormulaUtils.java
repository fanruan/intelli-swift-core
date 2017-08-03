package com.fr.bi.stable.utils;

import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.script.Calculator;
import com.fr.stable.Primitive;
import com.fr.stable.UtilEvalError;
import com.fr.third.antlr.ANTLRException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 小灰灰 on 2015/9/24.
 */
public class BIFormulaUtils {

    public static Object getCalculatorValue(Calculator c, String formula, ICubeTableService ti, Map<String, BIKey> columnIndexMap, int row) {

        Iterator<Map.Entry<String, BIKey>> iter = columnIndexMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, BIKey> entry = iter.next();
            String columnName = entry.getKey();
            BIKey columnIndex = entry.getValue();
            if (columnIndex != null) {
                Object value = ti.getColumnDetailReader(columnIndex).getValue(row);
                int fieldType = ti.getColumns().get(columnIndex).getFieldType();
                if (BICollectionUtils.isNotCubeNullKey(value)) {
                    if (fieldType == DBConstant.COLUMN.DATE) {
                        value = new Date((Long) value);
                    }
                    c.set(columnName, value);
                } else {
                    // BI-6107 null + number(非空数值) = null  直接返回null 以前的做法是直接返回.
                    // 但是因为这边是调用的是fr的公式处理,如果这样处理就会引发if(isNull)/if(a=null)(BI-5447 BI-5893 BI-5992 BI-6281)这样的的公式有问题
                    // 又因为fr中null+2 = 2
                    // 这种两难的情况
                    // .....所以对于这种情况,产品宁可选择接受null+2=2这种方法(同时和fr那边保持一致),所以还是改回来......
                    //
                    c.remove(columnName);
                }
            }
        }

        try {
            Object ob = c.eval(formula);
            return ob == Primitive.NULL ? null : ob;
        } catch (UtilEvalError e) {
            BILoggerFactory.getLogger(BIFormulaUtils.class).error("incorrect formula");
            BILoggerFactory.getLogger(BIFormulaUtils.class).error(BIStringUtils.append("The formula:", formula));
            return null;
        }
    }

    public static Map<String, BIKey> createColumnIndexMap(String formular, ICubeTableService ti) {

        Map<String, BIKey> columnIndexMap = new HashMap<String, BIKey>();
        try {
            String[] parameters = getRelatedParaNames(formular);
            for (int j = 0; j < parameters.length; j++) {
                String columnName;
                if (parameters[j].contains(BIReportConstant.FIELD_ID.HEAD)) {
                    columnName = BIIDUtils.getFieldNameFromFieldID(parameters[j].substring(BIReportConstant.FIELD_ID.HEAD.length(), parameters[j].length()));
                } else {
                    columnName = parameters[j];
                }

                BIKey columnIndex = ti.getColumnIndex(columnName);
                if (columnIndex == null) {
                    BILoggerFactory.getLogger().error(columnName + ": not found");
                } else {
                    columnIndexMap.put(toParameterFormat(String.valueOf(j)), columnIndex);
                }
            }
        } catch (ANTLRException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return columnIndexMap;
    }

    public static String[] getRelatedParaNames(String formular) throws ANTLRException {

        ArrayList<String> nameList = new ArrayList<String>();
        Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
        Matcher matcher = pat.matcher(formular);
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            nameList.add(matchStr.substring(2, matchStr.length() - 1));
        }
        String[] names = new String[nameList.size()];
        for (int i = 0; i < nameList.size(); i++) {
            names[i] = nameList.get(i);
        }
        return names;
    }

    public static Map<String, String> createColumnIndexMap(String formula) {

        Map<String, String> columnIndexMap = new HashMap<String, String>();
        try {
            String[] parameters = getRelatedParaNames(formula);

            for (int j = 0; j < parameters.length; j++) {
                String columnName = parameters[j];
                columnIndexMap.put(toParameterFormat(String.valueOf(j)), columnName);
            }
        } catch (ANTLRException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return columnIndexMap;
    }

    private static String toParameterFormat(String name) {

        return "$" + name;
    }

    public static Object getCalculatorValue(Calculator c, String formula, Map values) {

        Iterator<Map.Entry<String, String>> iter = createColumnIndexMap(formula).entrySet().iterator();
        String formulaStr = getIncrementParaFormula(formula);
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String columnName = entry.getKey();
            Object value = values.get(entry.getValue());
            if (BICollectionUtils.isNotCubeNullKey(value)) {
                c.set(columnName, value);
            } else {
                c.remove(columnName);
            }
        }
        try {
            Object ob = c.eval(formulaStr);
            return ob == Primitive.NULL ? null : ob;
        } catch (UtilEvalError e) {
            return null;
        }
    }

    public static String getIncrementParaFormula(String expression) {

        String formula = new String(expression);
        Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
        Matcher matcher = pat.matcher(expression);
        int parameterCount = 0;
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            formula = formula.replace(matchStr, "$" + String.valueOf(parameterCount));
            parameterCount++;
        }
        return formula;
    }

    public static Object getCalculatorValue(Calculator c, String formula, Map<String, TargetGettingKey> paraTargetMap, Number[] values) {

        Iterator<Map.Entry<String, TargetGettingKey>> iter = paraTargetMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, TargetGettingKey> entry = iter.next();
            String columnName = entry.getKey();
            if (values == null || values.length - 1 < entry.getValue().getTargetIndex()) {
                c.remove(columnName);
            } else {
                Object v = values[entry.getValue().getTargetIndex()];
                if(BICollectionUtils.isCubeNullKey(v)){
                   c.remove(columnName);
                }else {
                    c.set(columnName, v);
                }
            }
        }
        try {
            Object ob = c.eval(formula);
            return ob == Primitive.NULL ? null : ob;
        } catch (UtilEvalError e) {
            return null;
        }
    }

}