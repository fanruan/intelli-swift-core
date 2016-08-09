package com.finebi.cube.structure.group;

import com.finebi.cube.exception.BIResourceInvalidException;
import com.fr.bi.common.inter.Release;

import java.util.Comparator;

/**
 * 分组信息
 * This class created on 2016/3/6.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeGroupDataService<T> extends Release {
    /**
     * 在指定位置记录数值
     *
     * @param positionInGroup 位置
     * @param groupValue      分组值
     */
    void addGroupDataValue(int positionInGroup, T groupValue);

    Comparator<T> getGroupComparator();

    void setGroupComparator(Comparator groupComparator);

    /**
     * 获得分组值所在位置
     *
     * @param groupValue 分组值
     */
    int getPositionOfGroupValue(T groupValue) throws BIResourceInvalidException;


    T getGroupObjectValueByPosition(int position);

    int sizeOfGroup();

    void writeSizeOfGroup(int size);
}
