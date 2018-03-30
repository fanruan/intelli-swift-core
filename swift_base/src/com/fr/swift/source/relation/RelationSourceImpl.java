package com.fr.swift.source.relation;

import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.util.Util;

import java.util.List;

/**
 * This class created on 2017-12-21 14:21:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RelationSourceImpl implements RelationSource {
    @CoreField
    private SourceKey primarySource;
    @CoreField
    private SourceKey foreignSource;
    @CoreField
    private List<String> primaryFields;
    @CoreField
    private List<String> foreignFields;

    private RelationSourceType type;

    protected SourceKey key;
    private transient Core core;

    public RelationSourceImpl(SourceKey primarySource, SourceKey foreignSource, List<String> primaryFields, List<String> foreignFields, RelationSourceType type) {
        this.primarySource = primarySource;
        this.foreignSource = foreignSource;
        this.primaryFields = primaryFields;
        this.foreignFields = foreignFields;
        this.type = type;
    }

    @Override
    public SourceKey getPrimarySource() {
        return primarySource;
    }

    @Override
    public SourceKey getForeignSource() {
        return foreignSource;
    }

    @Override
    public List<String> getPrimaryFields() {
        return primaryFields;
    }

    @Override
    public List<String> getForeignFields() {
        return foreignFields;
    }

    @Override
    public RelationSourceType getRelationType() {
        return type;
    }

    @Override
    public SourceKey getSourceKey() {
        if (key == null) {
            initSourceKey();
        }
        Util.requireNonNull(key);
        return key;
    }

    protected void initSourceKey() {
        String id = fetchObjectCore().getValue();
        key = new SourceKey(id);
    }

    @Override
    public Core fetchObjectCore() {
        if (core == null) {
            core = new CoreGenerator(this).fetchObjectCore();
        }
        return core;
    }

    @Override
    public String toString() {
        return "RelationSource{" +
                "primarySource=" + primarySource +
                ", primaryFields=" + primaryFields +
                ", foreignSource=" + foreignSource +
                ", foreignFields=" + foreignFields +
                '}';
    }
}
