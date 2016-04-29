package com.fr.bi.conf.base.relation.relation;


import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.relation.BITableRelation;

import java.util.List;

/**
 * Created by Connery on 2016/1/12.
 */
public interface BIRelationContainerService {
    void addRelation(BITableRelation relation) throws BIRelationDuplicateException;

    void removeRelation(BITableRelation relation) throws BIRelationAbsentException;

    boolean contain(BITableRelation relation);

    BIRelationContainer getRelationContainer();

    void clear();

    Boolean isChanged(BIRelationContainer targetContainer);

    /**
     * 指定外键表，获得相应的关联
     *
     * @param foreignTable 外键表
     * @return 相应关联
     */
    List<BITableRelation> getRelationSpecificForeignTable(Table foreignTable);
    /**
     * 指定主键表，获得相应的关联
     *
     * @param foreignTable 主键表
     * @return 相应关联
     */
    List<BITableRelation> getRelationSpecificPrimaryTable(Table primaryTable);
}