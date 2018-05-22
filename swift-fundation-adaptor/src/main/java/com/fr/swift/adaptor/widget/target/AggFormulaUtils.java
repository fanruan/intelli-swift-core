package com.fr.swift.adaptor.widget.target;

/**
 * Created by pony on 2018/5/21.
 */
public class AggFormulaUtils {
    private static final String AGG_REG = "AGG";
    private static final String MIN_AGG = "MIN_AGG";
    private static final String COUNT_AGG = "COUNT_AGG";
    private static final String MAX_AGG = "MAX_AGG";
    private static final String MEDIAN_AGG = "MEDIAN_AGG";
    private static final String COUNTD_AGG = "COUNTD_AGG";
    private static final String VAR_AGG = "VAR_AGG";
    private static final String AVG_AGG = "AVG_AGG";
    private static final String STADEV_AGG = "STADEV_AGG";
    private static final String SUM_AGG = "SUM_AGG";

    public static boolean isAggFormula(String formula){
        return formula.contains(AGG_REG);
    }

    public static String[] getBaseParas(String formula) {
        return formula.split(AGG_REG);
    }
}
