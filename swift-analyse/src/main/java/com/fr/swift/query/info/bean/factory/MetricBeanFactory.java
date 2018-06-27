package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.element.metric.FormulaMetric;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.segment.column.ColumnKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class MetricBeanFactory implements BeanFactory<List<Metric>, List<MetricBean>> {

    public static final BeanFactory<Metric, MetricBean> SINGLE_METRIC_BEAN_FACTORY = new BeanFactory<Metric, MetricBean>() {
        @Override
        public MetricBean create(Metric source) {
            if (null != source) {
                MetricBean bean = new MetricBean();
                bean.setFilterInfoBean(FilterInfoBeanFactory.SINGLE_FILTER_INFO_BEAN_FACTORY.create(source.getFilter()));
                bean.setType(source.getAggregator().getAggregatorType());
                bean.setMetricType(source.getMetricType());
                bean.setTable(source.getSourceKey().getId());
                ColumnKey columnKey = source.getColumnKey();
                if (null != columnKey) {
                    bean.setColumn(columnKey.getName());
                    bean.setRelation(RelationSourceBeanFactory.SINGLE_RELATION_SOURCE_BEAN_FACTORY.create(columnKey.getRelation()));
                }
                if (source.getMetricType() == Metric.MetricType.FORMULA) {
                    bean.setFormula(((FormulaMetric) source).getFormula());
                }
                return bean;
            }
            return null;
        }
    };

    @Override
    public List<MetricBean> create(List<Metric> source) {
        List<MetricBean> result = new ArrayList<MetricBean>();
        if (null != source) {
            for (Metric metric : source) {
                result.add(SINGLE_METRIC_BEAN_FACTORY.create(metric));
            }
        }
        return result;
    }
}
