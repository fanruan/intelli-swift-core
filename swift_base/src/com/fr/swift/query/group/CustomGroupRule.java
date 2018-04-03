package com.fr.swift.query.group;

import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;

/**
 * @author anchore
 * @date 2018/4/2
 */
public interface CustomGroupRule<Base, Derive> extends GroupRule {
    /**
     * @param index 新分组号
     * @return 新分组值
     */
    Derive getValue(int index);

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
    int reverseMap(int originIndex);

    /**
     * @return 新分组大小
     */
    int newSize();

    /**
     * 获取功能传来的分组数
     *
     * @return 功能传来的分组数
     */
    int originalSize();

    boolean hasOtherGroup();

    /**
     * @param dict 原始分组
     */
    void setOriginDict(DictionaryEncodedColumn<Base> dict);
}