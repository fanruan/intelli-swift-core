package com.fr.swift.source.relation;

import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;
import com.fr.swift.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/4/3
 */
public class RelationPathSourceImpl implements SourcePath, Serializable {
    private static final long serialVersionUID = 1640228855301769583L;
    protected SourceKey key;
    private List<RelationSource> relations;
    @CoreField
    private SourceKey primarySource;
    @CoreField
    private SourceKey foreignSource;
    @CoreField
    private List<String> primaryFields;
    @CoreField
    private List<String> foreignFields;
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

    @Override
    public List<RelationSource> getRelations() {
        return relations;
    }

    @Override
    public SourcePath clone() {
        return new RelationPathSourceImpl(new ArrayList<RelationSource>(relations));
    }

    @Override
    public SourcePath addRelationAtHead(RelationSource source) {
        if (relations.isEmpty()) {
            relations.add(source);
        } else {
            relations.add(0, source);
        }
        RelationSource firstRelation = relations.get(0);
        this.primarySource = firstRelation.getPrimarySource();
        this.primaryFields = firstRelation.getPrimaryFields();
        return this;
    }

    @Override
    public SourcePath addRelationAtTail(RelationSource source) {
        relations.add(source);
        RelationSource lastRelation = relations.get(relations.size() - 1);
        this.foreignSource = lastRelation.getForeignSource();
        this.foreignFields = lastRelation.getForeignFields();
        initSourceKey();
        return this;
    }

    @Override
    public SourcePath removeFirstRelation() {
        relations.remove(0);
        RelationSource firstRelation = relations.get(0);
        this.primarySource = firstRelation.getPrimarySource();
        this.primaryFields = firstRelation.getPrimaryFields();
        return this;
    }

    @Override
    public SourcePath removeLastRelation() {
        relations.remove(relations.size() - 1);
        RelationSource lastRelation = relations.get(relations.size() - 1);
        this.foreignSource = lastRelation.getForeignSource();
        this.foreignFields = lastRelation.getForeignFields();
        initSourceKey();
        return this;
    }

    @Override
    public String toString() {
        return "{" + primarySource + "." + primaryFields +
                " -> " + foreignSource + "." + foreignFields + "}";
    }
}