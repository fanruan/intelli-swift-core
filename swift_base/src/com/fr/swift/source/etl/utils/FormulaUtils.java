package com.fr.swift.source.etl.utils;

import com.fr.script.Calculator;
import com.fr.stable.Primitive;
import com.fr.stable.UtilEvalError;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fr.swift.cube.io.IOConstant.*;

/**
 * Created by Handsome on 2018/2/1 0001 15:27
 */
public class FormulaUtils {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(FormulaUtils.class);


    public static Object getCalculatorValue(Calculator c, String formula, Segment segment, Map<String, ColumnKey> columnIndexMap, int row) {
        Iterator<Map.Entry<String, ColumnKey>> iter = columnIndexMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, ColumnKey> entry = iter.next();
            String columnName = entry.getKey();
            ColumnKey columnKey = entry.getValue();
            if(columnKey != null) {
                DictionaryEncodedColumn getter = segment.getColumn(columnKey).getDictionaryEncodedColumn();
                Object value = getter.getValue(getter.getIndexByRow(row));
                int columnType = 0;
                try {
                    columnType = segment.getMetaData().getColumn(columnKey.getName()).getType();
                } catch(SwiftMetaDataException e) {
                    throw new RuntimeException();
                }
                if(!isNullValue(value)) {
                    if (columnType == ColumnTypeUtils.columnTypeToSqlType(ColumnType.DATE)) {
                        value = new Date((Long)value);
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
            LOGGER.error("incorrect formula");
            LOGGER.error("The formula:" + formula);
            return null;
        }
    }

    private static <V> boolean isNullValue(V val) {
        return val.equals(NULL_INT) ||
                val.equals(NULL_LONG) ||
                val.equals(NULL_DOUBLE) ||
                val.equals(NULL_STRING);
    }

    public static Map<String, ColumnKey> createColumnIndexMap(String formular, Segment segment) {
        Map<String, ColumnKey> columnIndexMap = new HashMap<String, ColumnKey>();
        String[] parameters = getRelatedParaNames(formular);
        for (int i = 0; i < parameters.length; i++) {
            String columnName;
            if (parameters[i].contains(ETLConstant.FIELD_ID.HEAD)) {
                // TODO  通过工具类截取部分字段
                columnName = parameters[i].substring(ETLConstant.FIELD_ID.HEAD.length(), parameters[i].length()).substring(16);
            } else {
                columnName = parameters[i];
            }
            Column column = segment.getColumn(new ColumnKey(columnName));
            if (column != null) {
                columnIndexMap.put(toParameterFormat(String.valueOf(i)), new ColumnKey(columnName));
            } else {
                LOGGER.error(columnName + ": not found");
            }
        }
        return columnIndexMap;
    }


    public static String[] getRelatedParaNames(String formular) {

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

    private static String toParameterFormat(String name) {

        return "$" + name;
    }
}
