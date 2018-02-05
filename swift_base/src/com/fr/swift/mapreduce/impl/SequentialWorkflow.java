package com.fr.swift.mapreduce.impl;

import com.fr.swift.mapreduce.Collector;
import com.fr.swift.mapreduce.InCollector;
import com.fr.swift.mapreduce.KeyValue;
import com.fr.swift.mapreduce.Mapper;
import com.fr.swift.mapreduce.Reduce;
import com.fr.swift.mapreduce.Workflow;

import java.util.Iterator;

/**
 * Created by Lyon on 18-1-1.
 */
public class SequentialWorkflow<K_IN, V_IN, K_OUT, V_OUT> implements Workflow<K_OUT, V_OUT> {

    private Mapper<K_IN, V_IN, K_OUT, V_OUT> mapper;
    private Reduce<K_IN, V_IN, K_OUT, V_OUT> reducer;
    private InCollector<K_IN, V_IN> mapIn;
    private InCollector<K_IN, Iterable<V_IN>> reducerIn;
    private Collector<K_OUT, V_OUT> collector;

    private boolean isMapperTask = false;
    private boolean isReduceTask = false;

    public SequentialWorkflow(Mapper<K_IN, V_IN, K_OUT, V_OUT> mapper,
                              InCollector<K_IN, V_IN> mapIn, Collector<K_OUT, V_OUT> collector) {
        this.mapper = mapper;
        this.mapIn = mapIn;
        this.collector = collector;
        this.isMapperTask = true;
    }

    public SequentialWorkflow(Reduce<K_IN, V_IN, K_OUT, V_OUT> reducer,
                              InCollector<K_IN, Iterable<V_IN>> reducerIn, Collector<K_OUT, V_OUT> collector) {
        this.reducer = reducer;
        this.reducerIn = reducerIn;
        this.collector = collector;
        this.isReduceTask = true;
    }

    @Override
    public InCollector<K_OUT, V_OUT> run() {
        if (isMapperTask) {
            Iterator<KeyValue<K_IN, V_IN>> iterator = mapIn.iterator();
            while (iterator.hasNext()) {
                KeyValue<K_IN, V_IN> keyValue = iterator.next();
                mapper.map(keyValue, collector);
            }
        }
        if (isReduceTask) {
            Iterator<KeyValue<K_IN, Iterable<V_IN>>> iterator = reducerIn.iterator();
            while (iterator.hasNext()) {
                KeyValue<K_IN, Iterable<V_IN>> keyValue = iterator.next();
                reducer.reduce(keyValue.getKey(), keyValue.getValue().iterator(), collector);
            }
        }
        return collector;
    }
}
