package com.fr.swift.cloud.query.aggregator;

import com.fr.swift.cloud.query.aggregator.hll.CardinalityMergeException;
import com.fr.swift.cloud.query.aggregator.hll.HyperLogLog;
import com.fr.swift.cloud.query.aggregator.hll.MurmurHash;
import com.fr.swift.cloud.util.Crasher;

import java.io.Serializable;

/**
 * @author Lyon
 * @date 2018/7/13
 */
public class HLLAggregatorValue implements AggregatorValue<Double>, Serializable {

    private static final long serialVersionUID = 2390681614784335256L;
    private HyperLogLog hyperLogLog;

    public HLLAggregatorValue() {
        // TODO: 2018/7/13 基础位的个数可以改成可配置
        this.hyperLogLog = new HyperLogLog(15);
    }

    public void offer(int value) {
        hyperLogLog.offerHashed(MurmurHash.hashLong(value));
    }

    public void offer(long value) {
        hyperLogLog.offerHashed(MurmurHash.hashLong(value));
    }

    public void offer(double value) {
        hyperLogLog.offerHashed(MurmurHash.hash(Double.doubleToRawLongBits(value)));
    }

    public void offer(String value) {
        hyperLogLog.offerHashed(value == null ? 0 : MurmurHash.hash(value.getBytes()));
    }

    public void addAll(HLLAggregatorValue other) {
        try {
            hyperLogLog.addAll(other.hyperLogLog);
        } catch (CardinalityMergeException e) {
            Crasher.crash(e);
        }
    }

    @Override
    public double calculate() {
        return hyperLogLog.cardinality();
    }

    @Override
    public Double calculateValue() {
        return Double.valueOf(hyperLogLog.cardinality());
    }

    @Override
    public Object clone() {
        try {
            AggregatorValue value = new HLLAggregatorValue();
            ((HLLAggregatorValue) value).hyperLogLog = (HyperLogLog) hyperLogLog.merge(null);
            return value;
        } catch (CardinalityMergeException e) {
        }
        return null;
    }
}
