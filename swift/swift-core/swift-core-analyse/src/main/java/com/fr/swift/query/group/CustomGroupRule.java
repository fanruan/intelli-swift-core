package com.fr.swift.query.group;

import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;

/**
 * @author anchore
 * @date 2018/4/2
 */
public interface CustomGroupRule<Base, Derive> extends GroupRule {
    /**
     * 序号拿值
     *
     * @param index 新分组号
     * @return 新分组值
     */
    Derive getValue(int index);

    /**
     * 根据值拿序号
     *
     * @param val 新分组值
     * @return 字典序号
     */
    int getIndex(Object val);

    /**
     * 新值序号 -> 多个旧值序号
     *
     * @param index 新分组号
     * @return 对应的旧分组号
     */
    IntList map(int index);

    /**
     * 反向映射
     * 旧值序号 -> 新值序号
     *
     * @param originIndex 旧值序号
     * @return 新值序号
     */
    IntList reverseMap(int originIndex);

    /**
     * @return 新分组大小
     */
    int newSize();

    int getGlobalIndexByIndex(int index);

    /**
     * 设置原始分组
     *
     * @param dict 原始分组
     */
    void setOriginDict(DictionaryEncodedColumn<Base> dict);
}