package com.fr.bi.conf.base.relation;

import com.fr.bi.conf.base.relation.relation.IRelationContainer;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BIRelationDuplicateException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.conf.base.relation.relation.BIRelationContainer;
import com.fr.bi.stable.relation.BITableRelation;

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

    IRelationContainer getPrimaryRelation(Table table) throws BITableAbsentException;

    IRelationContainer getForeignRelation(Table table) throws BITableAbsentException;

    boolean containTablePrimaryRelation(Table table);

    boolean containTableForeignRelation(Table table);

    void clear();

    Boolean isChanged(BIRelationContainer targetRelationContainer);

    BIRelationContainer getRelationContainer();

    List<BITableRelation> getRelation(Table primaryTable, Table foreignTable) throws BITableAbsentException;

    Map<Table, IRelationContainer> getAllTable2PrimaryRelation();

    Map<Table, IRelationContainer> getAllTable2ForeignRelation();

}