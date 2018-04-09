package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.session.CommonConfigManager;
import com.finebi.common.service.engine.table.AbstractEngineTableManager;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.fieldinfo.FieldInfo;
import com.finebi.common.structure.config.relation.Relation;
import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.internalimp.analysis.table.FineAnalysisTableImpl;
import com.finebi.conf.internalimp.response.bean.FineTableResponed;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.conf.business.field.FieldInfoHelper;
import com.fr.swift.conf.business.relation.TableRelationReader;
import com.fr.swift.conf.business.table2source.dao.TableToSourceConfigDao;
import com.fr.swift.conf.business.table2source.dao.TableToSourceConfigDaoImpl;
import com.fr.swift.driver.SwiftDriverRegister;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/04/08
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftTableManager extends AbstractEngineTableManager {
    private TableToSourceConfigDao tableToSourceConfigDao;
    private SwiftLogger logger = SwiftLoggers.getLogger(SwiftTableManager.class);

    public SwiftTableManager() {
        SwiftDriverRegister.register();
        tableToSourceConfigDao = new TableToSourceConfigDaoImpl();
    }

    @Override
    protected FieldInfo createFieldInfo(EntryInfo entryInfo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FineBusinessTable> getBelongAnalysisTables(String tableName) {
        List<FineBusinessTable> result = new ArrayList<FineBusinessTable>();
        List<FineBusinessTable> allTable = getAllTable();
        for (FineBusinessTable table : allTable) {
            if (ComparatorUtils.equals(BICommonConstants.TABLE.ANALYSIS, table.getType())) {
                FineBusinessTable baseTable = ((FineAnalysisTableImpl) table).getBaseTable();
                if (ComparatorUtils.equals(baseTable.getName(), tableName)) {
                    result.add(table);
                }
            }
        }
        return result;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    @Override
    protected void saveEntryInfo(Map.Entry<String, List<FineBusinessTable>> entry, FineTableResponed responed) {
        Iterator iterator = ((List) entry.getValue()).iterator();

        try {
            while (iterator.hasNext()) {
                FineBusinessTable table = (FineBusinessTable) iterator.next();
                tableToSourceConfigDao.addConfig(table.getId(), DataSourceFactory.getDataSource(table).getSourceKey().getId());
                EntryInfo entryInfo = this.createEntryInfo(table);
                this.addEntryInfo(entryInfo, entry.getKey());
                this.saveFieldInfo(FieldInfoHelper.createFieldInfo(entryInfo, table));
                List<Relation> relationList = this.developDatabaseRelations(entryInfo, entry.getKey());
                this.updateFineTableResponed(responed, entryInfo, relationList);
            }
        } catch (Exception e) {
            logger.error("add table error: ", e);
            Crasher.crash(e);
        }
    }

    @Override
    protected List<Relation> developDatabaseRelations(EntryInfo entryInfo, String packageId) {
        List<Relation> dbTableRelations  = TableRelationReader.create(packageId, entryInfo).build();
        CommonConfigManager.getRelationConfigurationSession(this.getEngineType()).updateRelation(entryInfo.getID(), dbTableRelations);
        return dbTableRelations;
    }
}
