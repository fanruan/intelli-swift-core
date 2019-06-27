package com.fr.swift.config.entity;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Convert;
import com.fr.swift.annotation.persistence.Entity;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.annotation.persistence.Table;
import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.config.convert.AllotRuleConverter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;

/**
 * @author lucifer
 * @date 2019-06-24
 * @description
 * @since advanced swift 1.0
 */
@Entity
@Table(name = "fine_swift_table_allot_rule")
public class SwiftTableAllotRule {

    @Id
    @Column(name = "sourceKey")
    @JsonProperty
    private String sourceKey;

    @Column(name = "allotType")
    @JsonProperty
    private String allotType;

    @Column(name = "allotRule")
    @Convert(converter = AllotRuleConverter.class)
    @JsonProperty
    private AllotRule allotRule;

    private SwiftTableAllotRule() {
    }

    public SwiftTableAllotRule(String sourceKey) {
        this(sourceKey, BaseAllotRule.AllotType.LINE.name(), new LineAllotRule());
    }

    public SwiftTableAllotRule(String sourceKey, String allotType, AllotRule allotRule) {
        this.sourceKey = sourceKey;
        this.allotType = allotType;
        this.allotRule = allotRule;
    }

    public SwiftTableAllotRule(SourceKey sourceKey) {
        this(sourceKey.getId(), BaseAllotRule.AllotType.LINE.name(), new LineAllotRule());
    }

    public SwiftTableAllotRule(SourceKey sourceKey, AllotRule.Type allotType, AllotRule allotRule) {
        this(sourceKey.getId(), allotType.name(), allotRule);
    }

    public AllotRule getAllotRule() {
        return allotRule;
    }

    @Override
    public String toString() {
        return "SwiftTableAllotRule{" +
                "sourceKey=" + sourceKey +
                ", allotType=" + allotType +
                ", allotRule=" + allotRule +
                '}';
    }
}