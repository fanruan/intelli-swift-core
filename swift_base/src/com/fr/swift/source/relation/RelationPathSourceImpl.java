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
 * @author yee
 * @date 2018/4/3
 */
public class RelationPathSourceImpl implements RelationSource {
    private List<RelationSource> relations;
    @CoreField
    private SourceKey primarySource;
    @CoreField
    private SourceKey foreignSource;
    @CoreField
    private List<String> primaryFields;
    @CoreField
    private List<String> foreignFields;

    protected SourceKey key;
    private transient Core core;

    public RelationPathSourceImpl(List<RelationSource> relations) {
        this.relations = relations;
        RelationSource firstRelation = relations.get(0);
        RelationSource lastRelation = relations.get(relations.size() - 1);
        this.primarySource = firstRelation.getPrimarySource();
        this.foreignSource = lastRelation.getForeignSource();
        this.primaryFields = firstRelation.getPrimaryFields();
        this.foreignFields = lastRelation.getForeignFields();
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
        return RelationSourceType.RELATION_PATH;
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

    public List<RelationSource> getRelations() {
        return relations;
    }
}
