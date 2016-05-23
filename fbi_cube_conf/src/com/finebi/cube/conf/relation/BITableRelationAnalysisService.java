package com.finebi.cube.conf.relation;

import com.finebi.cube.conf.relation.relation.BIRelationContainer;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.IBusinessTable;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.finebi.cube.relation.BITableRelation;

import java.util.List;
import java.util.Map;

/**
 * 当前用户的表的关联关系管理
 * Created by Connery on 2016/1/12.
 */
public interface BITableRelationAnalysisService {

    void addRelation(BITableRelation relation) throws BIRelationDuplicateException;

    void removeRelation(BITableRelation relation) throws BIRelationAbsentException, BITableAbsentException;

    boolean contain(BITableRelation relation);

    IRelationContainer getPrimaryRelation(IBusinessTable table) throws BITableAbsentException;

    IRelationContainer getForeignRelation(IBusinessTable table) throws BITableAbsentException;

    boolean containTablePrimaryRelation(IBusinessTable table);

    boolean containTableForeignRelation(IBusinessTable table);

    void clear();

    Boolean isChanged(BIRelationContainer targetRelationContainer);

    BIRelationContainer getRelationContainer();

    List<BITableRelation> getRelation(IBusinessTable primaryTable, IBusinessTable foreignTable) throws BITableAbsentException;

    Map<IBusinessTable, IRelationContainer> getAllTable2PrimaryRelation();

    Map<IBusinessTable, IRelationContainer> getAllTable2ForeignRelation();

}