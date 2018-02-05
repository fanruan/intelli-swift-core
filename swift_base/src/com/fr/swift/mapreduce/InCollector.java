package com.fr.swift.mapreduce;

/**
 * Created by Lyon on 18-1-1.
 */
public interface InCollector<K, V> extends Iterable<KeyValue<K, V>> {

    /**
     * 如果不是分页之类的要统计个数，作为流式迭代器好像不需要增加其他接口了
     */
}
