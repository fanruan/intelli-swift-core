package com.fr.swift.source.etl.datamining.rcompile;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.internalimp.analysis.table.FineAnalysisTableImpl;
import com.finebi.conf.internalimp.service.rlink.FineGetRCodeServiceImpl;
import com.finebi.conf.provider.SwiftTableManager;
import com.finebi.conf.service.table.FineTableService;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

/**
 * Created by Handsome on 2018/4/12 0012 15:07
 */
public class RCodeFactory {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RCodeFactory.class);

    public void getRCode(String tableName) {
        try {
            SwiftTableManager manager = new SwiftTableManager();
            FineAnalysisTableImpl analysisTable = (FineAnalysisTableImpl) manager.getSingleTable(tableName);
            com.finebi.conf.internalimp.analysis.operator.rcompile.RCompileOperator operator =
                    (com.finebi.conf.internalimp.analysis.operator.rcompile.RCompileOperator) analysisTable.getOperator();
            FineGetRCodeServiceImpl service = StableManager.getContext().getObject("fineGetRCodeServiceImpl");
            service.setRCode(operator.getCommands());
        } catch(Exception e) {
            LOGGER.error("failed to get R code", e);
        }
    }
}
