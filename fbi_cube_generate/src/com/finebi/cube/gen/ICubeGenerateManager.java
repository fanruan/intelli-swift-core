package com.finebi.cube.gen;

import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeGenerateManager {
    void setTables(Set<CubeTableSource> tables);

    void setRelation(Set<BITableSourceRelation> relationSet);

    void setTableRelationPath(Set<BITableSourceRelationPath> tablePathSet);

    void setFieldRelationPath(Set<BITableSourceRelationPath> fieldPathSet);


}
