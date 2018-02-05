package com.fr.swift.mapreduce.impl;

import com.fr.swift.mapreduce.Collector;
import com.fr.swift.mapreduce.InCollector;
import com.fr.swift.mapreduce.KeyValue;
import com.fr.swift.mapreduce.Mapper;
import com.fr.swift.mapreduce.Reduce;
import com.fr.swift.mapreduce.Workflow;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lyon on 18-1-1.
 */
public class ParallelWorkflow<K_IN, V_IN, K_OUT, V_OUT> implements Workflow<K_OUT, V_OUT> {

    private static int workers = 10;

    private Mapper<K_IN, V_IN, K_OUT, V_OUT> mapper;
    private Reduce<K_IN, V_IN, K_OUT, V_OUT> reducer;
    private InCollector<K_IN, V_IN> mapIn;
    private InCollector<K_IN, Iterable<V_IN>> reducerIn;
    private ExecutorService executor = Executors.newFixedThreadPool(workers);
    private Collector<K_OUT, V_OUT> collector;

    private boolean isMapperTask = false;
    private boolean isReducerTask = false;

    public ParallelWorkflow(Mapper<K_IN, V_IN, K_OUT, V_OUT> mapper,
                            InCollector<K_IN, V_IN> in, Collector<K_OUT, V_OUT> collector) {
        this.mapper = mapper;
        this.mapIn = in;
        this.collector = collector;
        this.isMapperTask = true;
    }

    public ParallelWorkflow(Reduce<K_IN, V_IN, K_OUT, V_OUT> reducer,
                            InCollector<K_IN, Iterable<V_IN>> in, Collector<K_OUT, V_OUT> collector) {
        this.reducer = reducer;
        this.reducerIn = in;
        this.collector = collector;
        this.isReducerTask = true;
    }

    private void runMapperTask() {
        final Iterator<KeyValue<K_IN, V_IN>> iterator = mapIn.iterator();
        while (iterator.hasNext()) {
            final KeyValue<K_IN, V_IN> keyValue = iterator.next();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    mapper.map(keyValue, collector);
                }
            });
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ig) {

        }
    }

    private void runReducerTask() {
        final Iterator<KeyValue<K_IN, Iterable<V_IN>>> iterator = reducerIn.iterator();
        while (iterator.hasNext()) {
            final KeyValue<K_IN, Iterable<V_IN>> keyValue = iterator.next();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    reducer.reduce(keyValue.getKey(), keyValue.getValue().iterator(), collector);
                }
            });
        }
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ig) {

        }
    }

    @Override
    public InCollector<K_OUT, V_OUT> run() {
        if (isMapperTask) {
            runMapperTask();
        }
        if (isReducerTask) {
            runReducerTask();
        }
        return collector;
    }
}
