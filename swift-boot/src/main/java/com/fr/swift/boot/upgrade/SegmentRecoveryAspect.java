package com.fr.swift.boot.upgrade;

import com.fr.decision.log.ExecuteMessage;
import com.fr.decision.log.WriteMessage;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.JpaAdaptor;
import com.fr.third.aspectj.lang.annotation.Aspect;
import com.fr.third.aspectj.lang.annotation.Before;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Qualifier;
import com.fr.third.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/10/18
 */
@Aspect
@Service
public class SegmentRecoveryAspect {

    @Autowired
    @Qualifier("swiftMetaDataService")
    private SwiftMetaDataService metaService;

    private List<SourceKey> tableKeys;

    /**
     * BI-34065 兼容
     *
     * @param tableKey 表
     */
    @Before(value = "execution(* com.fr.swift.segment.recover.SegmentRecovery.recover(com.fr.swift.source.SourceKey)) && args(tableKey)")
    public void beforeRecoverTable(SourceKey tableKey) {
        if (tableKeys.contains(tableKey) && metaService.containsMeta(tableKey)) {
            try {
                SwiftMetaData meta = metaService.getMetaDataByKey(tableKey.getId());
                List<SwiftMetaDataColumn> columnMetas = new ArrayList<SwiftMetaDataColumn>(meta.getColumnCount());
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    SwiftMetaDataColumn columnMeta = meta.getColumn(i + 1);
                    columnMetas.add(new MetaDataColumnBean(
                            columnMeta.getName(), columnMeta.getRemark(),
                            columnMeta.getType(), columnMeta.getPrecision(),
                            columnMeta.getScale(), columnMeta.getColumnId()));
                }
                SwiftMetaData newMeta = new SwiftMetaDataBean(meta.getId(), SwiftDatabase.DECISION_LOG, meta.getSchemaName(), meta.getTableName(), meta.getRemark(), columnMetas);
                metaService.updateMetaData(tableKey.getId(), newMeta);
            } catch (SwiftMetaDataException e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    @PostConstruct
    private void init() {
        Class<?>[] classes = {ExecuteMessage.class, WriteMessage.class};
        tableKeys = new ArrayList<SourceKey>(classes.length);
        for (Class<?> aClass : classes) {
            tableKeys.add(new SourceKey(JpaAdaptor.getTableName(aClass)));
        }
    }
}