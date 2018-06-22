package com.fr.swift.query.info.bean.factory;

import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.element.metric.FormulaMetric;
import com.fr.swift.query.info.element.metric.Metric;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/22
 */
public class MetricBeanFactory implements BeanFactory<List<Metric>, List<MetricBean>> {

    public static final SingleMetricBeanFactory SINGLE_METRIC_BEAN_FACTORY = new SingleMetricBeanFactory();

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

    public static class SingleMetricBeanFactory implements BeanFactory<Metric, MetricBean> {

        @Override
        public MetricBean create(Metric source) {
            if (null != source) {
                MetricBean bean = new MetricBean();
                bean.setFilterInfoBean(FilterInfoBeanFactory.SINGLE_FILTER_INFO_BEAN_FACTORY.create(source.getFilter()));
                bean.setType(source.getAggregator().getAggregatorType());
                bean.setMetricType(source.getMetricType());
                bean.setSourceKey(source.getSourceKey());
                bean.setColumnKey(source.getColumnKey());
                if (source.getMetricType() == Metric.MetricType.FORMULA) {
                    bean.setFormula(((FormulaMetric) source).getFormula());
                }
                return bean;
            }
            return null;
        }
    }
}
