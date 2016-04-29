package com.fr.bi.cal.generate;

import com.fr.bi.cal.generate.relation.RelationGenerator;
import com.fr.bi.cal.generate.relation.basiclinkindex.LinkBasicIndexEntry;

/**
 * Created by Connery on 2015/4/3.
 */
public class CubeBuildBasicIndexOperation implements CubeBuildOperation {
    private long userId;
    private String basePath;
    private String tmpPath;
    private RelationGenerator relationGenerator;
    private LinkBasicIndexEntry linkBasicIndexEntry;

    public CubeBuildBasicIndexOperation(String basePath, String tmpPath, long userId) {
        this.userId = userId;
        this.basePath = basePath;
        this.tmpPath = tmpPath;
    }

    @Override
    public Object getData() {
        return relationGenerator.fetchTableKeys();
    }

    @Override
    public Object process(Object tableSet) {
        linkBasicIndexEntry.generateCube();
        return new Object();
    }
}