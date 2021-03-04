package com.fr.swift.cloud.query.aggregator;


import java.io.Serializable;

/**
 * @author Xiaolei.liu
 */
public class StandardAggregatorValue extends VarianceAggregatorValue implements Serializable {
    private static final long serialVersionUID = 3145787090081586765L;
    private VarianceAggregatorValue variance;

    public void setVariance(VarianceAggregatorValue variance) {
        this.variance = variance;
    }

    public VarianceAggregatorValue getCalVariance() {
        return variance;
    }

    @Override
    public double calculate() {
        return (variance.getCount() == 0) ? null : Math.sqrt(variance.calculateValue().doubleValue());
    }

    @Override
    public Number calculateValue() {
        return (variance.getCount() == 0) ? null : Math.sqrt(variance.calculateValue().doubleValue());
    }

    @Override
    public Object clone() {
        StandardAggregatorValue value = new StandardAggregatorValue();
        value.variance = (VarianceAggregatorValue) this.variance.clone();
        return value;
    }
}
