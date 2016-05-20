package com.finebi.cube.structure;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.data.db.BICubeFieldSource;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Cube表的只读接口
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeTableEntityGetterService extends Release {

    int getTableVersion();

    /**
     * 获得CubeTable对应的数据源表的字段信息
     *
     * @return 按照顺序的字段信息
     */
    List<BICubeFieldSource> getFieldInfo();

    /**
     * Cube中保存的字段信息。
     * 包括子类型的处理
     *
     * @return cube的字段信息。
     */
    Set<BIColumnKey> getCubeColumnInfo();

    int getRowCount();

    BICubeFieldSource getSpecificColumn(String fieldName) throws BICubeColumnAbsentException;

    Date getCubeLastTime();

    /**
     * 获取列的接口
     *
     * @param key 列
     * @return 获取列的接口
     */
    ICubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException;

    /**
     * 获取列的接口
     *
     * @param columnName 列名
     * @return 获取列的接口
     */
    ICubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException;

    /**
     * 获取关联数据的接口
     *
     * @param field 列
     * @param path  关联的路径
     * @return 获取关联数据的接口
     */
    ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path)
            throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException;

    boolean tableDataAvailable();

    boolean isRowCountAvailable();
}
