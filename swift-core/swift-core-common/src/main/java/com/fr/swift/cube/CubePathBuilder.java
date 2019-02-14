package com.fr.swift.cube;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Assert;
import com.fr.swift.util.Strings;

/**
 * @author anchore
 * @date 12/7/2018
 */
public class CubePathBuilder {

    private static final SwiftCubePathService PATH_SVC = SwiftContext.get().getBean(SwiftCubePathService.class);
    private boolean absolute = false;
    private SwiftDatabase schema = null;
    private Integer tempDir = null;
    private boolean backup = false;
    private SourceKey tableKey = null;
    private Integer segOrder = null;
    private String columnId = null;

    public CubePathBuilder() {
    }

    public CubePathBuilder(SegmentKey segKey) {
        setSwiftSchema(segKey.getSwiftSchema())
                .setTableKey(segKey.getTable())
                .setSegOrder(segKey.getOrder());
    }

    public CubePathBuilder asAbsolute() {
        this.absolute = true;
        return this;
    }

    public CubePathBuilder setSwiftSchema(SwiftDatabase schema) {
        Assert.notNull(schema);
        this.schema = schema;
        return this;
    }

    public CubePathBuilder setTempDir(int tempDir) {
        this.tempDir = tempDir;
        return this;
    }

    public CubePathBuilder asBackup() {
        this.backup = true;
        return this;
    }

    public CubePathBuilder setTableKey(SourceKey tableKey) {
        Assert.notNull(tableKey);
        this.tableKey = tableKey;
        return this;
    }

    public CubePathBuilder setSegOrder(int segOrder) {
        Assert.isTrue(segOrder >= 0);
        this.segOrder = segOrder;
        return this;
    }

    public CubePathBuilder setColumnId(String columnId) {
        Assert.isTrue(Strings.isNotEmpty(columnId));
        this.columnId = columnId;
        return this;
    }

    public String build() {
        Assert.notNull(schema);
        Assert.isFalse(tempDir != null && backup, "tempDir is not allowed when backup is present, vice versa");

        StringBuilder path = new StringBuilder();
        if (absolute) {
            path.append(PATH_SVC.getSwiftPath()).append('/');
        }
        path.append(backup ? schema.getBackupDir() : schema.getDir());
        if (tempDir != null) {
            path.append('/').append(tempDir);
        }
        if (tableKey != null) {
            path.append('/').append(tableKey.getId());
        }
        if (segOrder != null) {
            path.append("/seg").append(segOrder);
        }
        if (columnId != null) {
            path.append('/').append(columnId);
        }
        return path.toString();
    }

    @Override
    public String toString() {
        return build();
    }
}