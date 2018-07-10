package com.fr.swift.generate.conf;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.swift.source.alloter.line.LineAllotRule;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import com.fr.third.javax.persistence.AttributeConverter;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

import java.io.IOException;

/**
 * @author anchore
 * @date 2018/7/2
 */
@Entity
@Table(name = "fine_swift_table_index_conf")
public class SwiftTableIndexingConf extends BaseIndexingConf implements TableIndexingConf {
    @Column(name = "allotRule")
    @Convert(converter = AllotRuleConverter.class)
    private AllotRule allotRule;

    public SwiftTableIndexingConf(SourceKey table, AllotRule allotRule) {
        super(table);
        this.allotRule = allotRule;
    }

    @Override
    public AllotRule getAllotRule() {
        return allotRule;
    }

    @Override
    public SourceKey getTable() {
        return new SourceKey(tableKey);
    }
}

class AllotRuleConverter implements AttributeConverter<AllotRule, String> {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AllotRule allotRule) {
        try {
            return mapper.writeValueAsString(allotRule);
        } catch (JsonProcessingException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    @Override
    public AllotRule convertToEntityAttribute(String s) {
        try {
            return mapper.readValue(s, LineAllotRule.class);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
            return null;
        }
    }

    public static void main(String[] args) {
        AllotRuleConverter converter = new AllotRuleConverter();
        converter.convertToEntityAttribute(converter.convertToDatabaseColumn(new LineAllotRule()));
    }
}