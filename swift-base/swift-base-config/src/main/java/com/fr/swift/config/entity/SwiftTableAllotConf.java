package com.fr.swift.config.entity;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.config.TableAllotConf;
import com.fr.swift.config.entity.key.TableId;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.AllotRule;

import java.io.Serializable;


/**
 * @author anchore
 * @date 2018/7/2
 */
//@Entity
//@Table(name = "fine_swift_tab_idx_conf")
@Deprecated
public class SwiftTableAllotConf implements TableAllotConf, Serializable {
    @Id
    private TableId tableId;

    @Column(name = "allotRule")
//    @Convert(converter = AllotRuleConverter.class)
    private AllotRule allotRule;

    public SwiftTableAllotConf() {
    }

    public SwiftTableAllotConf(SourceKey tableKey, AllotRule allotRule) {
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