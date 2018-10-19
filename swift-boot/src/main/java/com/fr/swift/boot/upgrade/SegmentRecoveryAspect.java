package com.fr.swift.boot.upgrade;

import com.fr.decision.log.ExecuteMessage;
import com.fr.decision.log.WriteMessage;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
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
            SwiftMetaData meta = metaService.getMetaDataByKey(tableKey.getId());
            if (meta.getSwiftDatabase() != SwiftDatabase.DECISION_LOG) {
                ((SwiftMetaDataBean) meta).setSwiftDatabase(SwiftDatabase.DECISION_LOG);
                metaService.updateMetaData(tableKey.getId(), meta);
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