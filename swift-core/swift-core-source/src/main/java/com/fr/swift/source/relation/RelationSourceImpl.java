package com.fr.swift.source.relation;

import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.util.Util;

import java.io.Serializable;
import java.util.List;

/**
 * This class created on 2017-12-21 14:21:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RelationSourceImpl implements RelationSource, Serializable {
    private static final long serialVersionUID = -440451044374432042L;
    protected SourceKey key;
    @CoreField
    private SourceKey primarySource;
    @CoreField
    private SourceKey foreignSource;
    @CoreField
    private List<String> primaryFields;
    @CoreField
    private List<String> foreignFields;
    private transient Core core;

    public RelationSourceImpl(SourceKey primarySource, SourceKey foreignSource, List<String> primaryFields, List<String> foreignFields) {
        this.primarySource = primarySource;
        this.foreignSource = foreignSource;
        this.primaryFields = primaryFields;
        this.foreignFields = foreignFields;
    }

    public RelationSourceImpl() {
    }

    @Override
    public SourceKey getPrimarySource() {
        return primarySource;
    }

    public void setPrimarySource(SourceKey primarySource) {
        this.primarySource = primarySource;
    }

    @Override
    public SourceKey getForeignSource() {
        return foreignSource;
    }

    public void setForeignSource(SourceKey foreignSource) {
        this.foreignSource = foreignSource;
    }

    @Override
    public List<String> getPrimaryFields() {
        return primaryFields;
    }

    public void setPrimaryFields(List<String> primaryFields) {
        this.primaryFields = primaryFields;
    }

    @Override
    public List<String> getForeignFields() {
        return foreignFields;
    }

    public void setForeignFields(List<String> foreignFields) {
        this.foreignFields = foreignFields;
    }

    @Override
    public RelationSourceType getRelationType() {
        return RelationSourceType.RELATION;
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
        return "{" + primarySource + "." + primaryFields +
                " -> " + foreignSource + "." + foreignFields + "}";
    }
}
