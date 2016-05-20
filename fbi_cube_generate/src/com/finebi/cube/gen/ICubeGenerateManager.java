package com.finebi.cube.gen;

import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BITableSourceRelationPath;

import java.util.Set;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeGenerateManager {
    void setTables(Set<ICubeTableSource> tables);

    void setRelation(Set<BITableSourceRelation> relationSet);

    void setTableRelationPath(Set<BITableSourceRelationPath> tablePathSet);

    void setFieldRelationPath(Set<BITableSourceRelationPath> fieldPathSet);


}
