package com.finebi.cube.structure.column;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.fr.bi.common.inter.Release;

import java.util.Set;

/**
 * 管理CUBE中一张表中的列
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeTableColumnManagerService extends Release {

    /**
     * 依据列名获得当前表的某一列
     *
     * @param columnName 列名
     * @return 某一列
     */
    ICubeColumnEntityService getColumn(BIColumnKey columnKey) throws BICubeColumnAbsentException;

    Set<BIColumnKey> getCubeColumnInfo();

}
