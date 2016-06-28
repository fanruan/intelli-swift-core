package com.fr.bi.stable.utils;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.base.key.BIKey;
import com.finebi.cube.api.ICubeTableService;
import com.fr.script.Calculator;
import com.fr.stable.Primitive;
import com.fr.stable.UtilEvalError;
import com.fr.third.antlr.ANTLRException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 小灰灰 on 2015/9/24.
 */
public class BIFormularUtils {

    public static Object getCalculatorValue(Calculator c, String formula, ICubeTableService ti, Map<String, BIKey> columnIndexMap, int row) {
        Iterator<Map.Entry<String, BIKey>> iter = columnIndexMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, BIKey> entry = iter.next();
            String columnName = entry.getKey();
            BIKey columnIndex = entry.getValue();
            if (columnIndex != null) {
                Object value = ti.getColumnDetailReader(columnIndex).getValue(row);
                if (value != null) {
                    c.set(columnName, value);
                } else {
                    c.remove(columnName);
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
                    BILogger.getLogger().error(columnName + ": not found");
                } else {
                    columnIndexMap.put(toParameterFormat(String.valueOf(j)), columnIndex);
                }
            }
        } catch (ANTLRException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return columnIndexMap;
    }

    private static String[] getRelatedParaNames(String formular) throws ANTLRException {
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

    public static Map<String, String> createColumnIndexMap(String formular) {
        Map<String, String> columnIndexMap = new HashMap<String, String>();
        try {
            String[] parameters = getRelatedParaNames(formular);

            for (int j = 0; j < parameters.length; j++) {
                String columnName = parameters[j];
                columnIndexMap.put(toParameterFormat(String.valueOf(j)), columnName);
            }
        } catch (ANTLRException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return columnIndexMap;
    }

    private static String toParameterFormat(String name) {
        return "$" + name;
    }

    public static Object getCalculatorValue(Calculator c, String formula, Map values) {
        Iterator<Map.Entry<String, String>> iter = createColumnIndexMap(formula).entrySet().iterator();
        String formulaStr = formula;
        Pattern pat = Pattern.compile("\\$[\\{][^\\}]*[\\}]");
        Matcher matcher = pat.matcher(formulaStr);
        int parameterCount = 0;
        while (matcher.find()) {
            String matchStr = matcher.group(0);
            formulaStr = formulaStr.replace(matchStr, "$" + String.valueOf(parameterCount));
            parameterCount++;
        }
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String columnName = entry.getKey();
            Object value = values.get(entry.getValue());
            if (value != null) {
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

}