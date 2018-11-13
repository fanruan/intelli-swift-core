package com.fr.swift.adaptor.widget.target;

import com.fr.swift.query.aggregator.AggregatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/5/21.
 */
public class AggFormulaUtils {
    protected static final String AGG_REG = "_AGG";

    public static boolean isAggFormula(String formula) {
        return formula.contains(AGG_REG);
    }

    /**
     * split之后变成这样的数组["MIN_","(${0013DEMO_CONTRACT购买数量} ) + MAX_","(${0013DEMO_CONTRACT购买数量} +1) "]
     * 所以有n个聚合函数，就有n+1个长度的数组，对于第i个聚合函数，类型由i-决定，内容由i决定
     * @param formula
     * @return
     */
    public static List<AggUnit> getBaseParas(String formula) {
        List<AggUnit> aggUnitList = new ArrayList<AggUnit>();
        String[] units = formula.split(AGG_REG);
        for (int index = 1; index < units.length; index++) {
            aggUnitList.add(new AggUnit(getAggType(units[index - 1]), getFormula(units[index])));
        }
        return aggUnitList;
    }

    private static String getFormula(String current) {
        int start = current.indexOf("(") + 1;
        int end = current.lastIndexOf(")");
        return current.substring(start, end);
    }

    /**
     * \
     * nice job foundation! 只丢了string过来，只能先瞎搞搞了，截取最后三位先判断
     *
     * @param last
     * @return
     */
    private static AggregatorType getAggType(String last) {
        String type = last.substring(last.length() - 3, last.length());
        if (type.equals("MIN")) {
            return AggregatorType.MIN;
        } else if (type.equals("UNT")) {
            return AggregatorType.COUNT;
        } else if (type.equals("MAX")) {
            return AggregatorType.MAX;
        } else if (type.equals("IAN")) {
            return AggregatorType.MEDIAN;
        } else if (type.equals("NTD")) {
            return AggregatorType.DISTINCT;
        } else if (type.equals("VAR")) {
            return AggregatorType.VARIANCE;
        } else if (type.equals("AVG")) {
            return AggregatorType.AVERAGE;
        } else if (type.equals("DEV")) {
            return AggregatorType.STANDARD_DEVIATION;
        } else {
            return AggregatorType.SUM;
        }
    }
}
