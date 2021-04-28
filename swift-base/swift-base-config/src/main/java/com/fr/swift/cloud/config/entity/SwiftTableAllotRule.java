package com.fr.swift.cloud.config.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fr.swift.cloud.config.entity.convert.AllotRuleConvert;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.alloter.AllotRule;
import com.fr.swift.cloud.source.alloter.impl.BaseAllotRule;
import com.fr.swift.cloud.source.alloter.impl.line.LineAllotRule;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    @Convert(converter = AllotRuleConvert.class)
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