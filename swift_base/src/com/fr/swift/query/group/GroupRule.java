package com.fr.swift.query.group;

import com.fr.swift.structure.array.IntList;

/**
 * @author anchore
 * @date 2018/1/29
 */
public interface GroupRule {
    /**
     * @param index 新分组号
     * @return 新分组名
     */
    String getGroupName(int index);

    /**
     * 新值序号 -> 多个旧值序号
     *
     * @param index 新分组号
     * @return 对应的旧分组号
     */
    IntList map(int index);

    /**
     * @return 新分组大小
     */
    int newSize();
}
