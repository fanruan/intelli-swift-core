package com.finebi.cube.structure;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.structure.column.ICubeTableColumnManagerService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.relation.BITableSourceRelation;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Cube表的接口
 * <p/>
 * 表有两部分组成：column的manager和Relation的manager
 *
 * @author Connery
 * @see ICubeTableColumnManagerService
 * @see ICubeRelationManagerService
 * @since 4.0
 */
public interface ICubeTableEntityService extends ICubeTableEntityGetterService {

    void recordTableStructure(List<BICubeFieldSource> fields);

    void recordTableGenerateVersion(int version);

    void recordRowCount(long rowCount);

    void recordLastTime();

    void recordRemovedLine(TreeSet<Integer> removedLine);

    /**
     * 添加数据库表的原始数据
     *
     * @param originalDataValue
     */
    void addDataValue(BIDataValue originalDataValue) throws BICubeColumnAbsentException;

    boolean checkRelationVersion(List<BITableSourceRelation> relations,
                                 int relation_version);

    boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations,
                                 int relation_version);

    boolean checkCubeVersion();

    void copyDetailValue(ICubeTableEntityService cube, long rowCount);

    void recordParentsTable(List<ITableKey> parents);

    List<ITableKey> getParentsTable();

    void recordFieldNamesFromParent(Set<String> fieldNames);

    Set<String> getFieldNamesFromParent();
}
