package com.fr.bi.cal.generate;

import com.fr.bi.cal.generate.relation.RelationsGetter;
import com.fr.bi.cal.generate.relation.firstlinkindex.LinkFirstIndexEntry;
import com.fr.bi.stable.index.CubeGenerator;
import com.fr.bi.stable.relation.BITableSourceRelation;

import java.util.Set;

/**
 * Created by Connery on 2015/4/2.
 */
public class CubeBuildFirstIndexOperation implements CubeBuildOperation {
    private long userId;
    private RelationsGetter relationsGetter;
    private CubeGenerator linkBasicRelationIndexEntry;

    public CubeBuildFirstIndexOperation(String basePath, String tmpPath, long userId) {
    }

    public void setRelationsGetter(RelationsGetter relationsGetterm) {
        this.relationsGetter = relationsGetterm;
    }

    @Override
    public Object getData() {
        return relationsGetter.getGenerateRelations();
    }

    @Override
    public Object process(Object data) {
        setLinkBasicRelationIndexEntry((Set<BITableSourceRelation>) data);
        linkBasicRelationIndexEntry.generateCube();
        return new Object();
    }

    public void setLinkBasicRelationIndexEntry(Set<BITableSourceRelation> data) {
        this.linkBasicRelationIndexEntry = new LinkFirstIndexEntry(data, userId);
    }

}