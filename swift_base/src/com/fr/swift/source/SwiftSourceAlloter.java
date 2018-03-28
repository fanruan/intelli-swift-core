package com.fr.swift.source;

/**
 * Created by pony on 2017/10/24.
 * 数据源生成cube的接口,同一数据源可以有多种生成方式，或者分配规则，直接按行拆分，按某些列的hash拆分等等
 */
public interface SwiftSourceAlloter {
    /**
     * 分片逻辑
     *
     * @param row       行号
     * @param keyColumn 分片参照列
     * @param data      参照列的值
     * @return 返回根据逻辑返回的segment序号
     */
    int allot(long row, String keyColumn, Object data);

    int getAllotStep();
}
