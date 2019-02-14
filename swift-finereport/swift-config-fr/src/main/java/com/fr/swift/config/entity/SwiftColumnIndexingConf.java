package com.fr.swift.config.entity;

import com.fr.swift.config.ColumnIndexingConf;
import com.fr.swift.config.bean.SwiftColumnIdxConfBean;
import com.fr.swift.config.entity.key.ColumnId;
import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Assert;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;


/**
 * @author anchore
 * @date 2018/7/2
 */
@Entity
@Table(name = "fine_swift_col_idx_conf")
public class SwiftColumnIndexingConf implements ColumnIndexingConf, ObjectConverter<SwiftColumnIdxConfBean> {
    @Id
    private ColumnId columnId;

    @Column(name = "requireIndex")
    private boolean requireIndex;

    @Column(name = "requireGlobalDict")
    private boolean requireGlobalDict;

    public SwiftColumnIndexingConf() {
    }

    public SwiftColumnIndexingConf(SourceKey tableKey, String columnName, boolean requireIndex, boolean requireGlobalDict) {
        Assert.isFalse(!requireIndex && requireGlobalDict, "global dict is not allowed to generate without index");

        this.columnId = new ColumnId(tableKey, columnName);
        this.requireIndex = requireIndex;
        this.requireGlobalDict = requireGlobalDict;
    }

    public SwiftColumnIndexingConf(SwiftColumnIdxConfBean value) {
        this(new SourceKey(value.getTableKey()), value.getColumnName(), value.isRequireIndex(), value.isRequireGlobalDict());
    }

    @Override
    public SourceKey getTable() {
        return columnId.getTableKey();
    }

    @Override
    public String getColumn() {
        return columnId.getColumnName();
    }

    @Override
    public boolean requireIndex() {
        return requireIndex;
    }

    @Override
    public boolean requireGlobalDict() {
        return requireGlobalDict;
    }

    @Override
    public SwiftColumnIdxConfBean convert() {
        return new SwiftColumnIdxConfBean(columnId.getTableKey().getId(), columnId.getColumnName(), this.requireIndex, this.requireGlobalDict);
    }
}