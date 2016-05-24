package com.finebi.cube.structure;

import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.data.db.ICubeFieldSource;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 表的基本属性。
 * 出错无法恢复，抛错runtime异常，终止运行。
 * 由于全部数据都是从数据源获取，如果异常应该在于数据源获取问题。
 * 从另外的角度来说，如果数据源获取的数据保证了的准确性，
 * 此处不应该抛出自身造成的异常（不包括系统异常）。数据来源要强制
 * 进行检查。
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeTablePropertyService extends Release {
    void recordTableStructure(List<ICubeFieldSource> fields);

    void recordTableGenerateVersion(int version);

    void recordRowCount(long rowCount);

    void recordLastTime();

    void recordParentsTable(List<ITableKey> parentTables);

    List<ITableKey> getParentsTable();

    int getRowCount();

    Date getCubeLastTime();

    int getTableVersion();

    List<ICubeFieldSource> getFieldInfo();

    Boolean isPropertyExist();

    Boolean isRowCountAvailable();

    void recordFieldNamesFromParent(Set<String> fieldNames);

    Set<String> getFieldNamesFromParent();

}
