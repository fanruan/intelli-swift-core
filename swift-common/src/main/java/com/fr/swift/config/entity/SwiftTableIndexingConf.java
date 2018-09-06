package com.fr.swift.config.entity;

import com.fr.swift.config.convert.hibernate.AllotRuleConverter;
import com.fr.swift.config.entity.key.TableId;
import com.fr.swift.config.indexing.TableIndexingConf;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Convert;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;

/**
 * @author anchore
 * @date 2018/7/2
 */
@Entity
@Table(name = "fine_swift_tab_idx_conf")
public class SwiftTableIndexingConf implements TableIndexingConf {
    @Id
    private TableId tableId;

    @Column(name = "allotRule")
    @Convert(converter = AllotRuleConverter.class)
    private AllotRule allotRule;

    public SwiftTableIndexingConf() {
    }

    public SwiftTableIndexingConf(SourceKey tableKey, AllotRule allotRule) {
        this.tableId = new TableId(tableKey);
        this.allotRule = allotRule;
    }

    @Override
    public SourceKey getTable() {
        return tableId.getTableKey();
    }

    @Override
    public AllotRule getAllotRule() {
        return allotRule;
    }
}