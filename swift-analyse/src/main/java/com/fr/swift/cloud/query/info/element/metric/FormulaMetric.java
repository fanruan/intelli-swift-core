//package com.fr.swift.query.info.element.metric;
//
//import Aggregator;
//import FilterInfo;
//import Formula;
//import MetricType;
//import Segment;
//import Column;
//import FormulaMetricColumn;
//
///**
// * Created by pony on 2018/5/10.
// */
//public class FormulaMetric extends GroupMetric {
//    private Formula formula;
//
//    public FormulaMetric(int queryIndex, FilterInfo filterInfo, Aggregator aggregator, Formula formula) {
//        super(queryIndex, null, filterInfo, aggregator);
//        this.formula = formula;
//    }
//
//    @Override
//    public Column getColumn(Segment segment) {
//        return new FormulaMetricColumn(formula, segment);
//    }
//
////    public String getFormula() {
////        return formula;
////    }
//
//    @Override
//    public MetricType getMetricType() {
//        return MetricType.FORMULA;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;
//
//        FormulaMetric that = (FormulaMetric) o;
//
//        return formula != null ? formula.equals(that.formula) : that.formula == null;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = super.hashCode();
//        result = 31 * result + (formula != null ? formula.hashCode() : 0);
//        return result;
//    }
//}
