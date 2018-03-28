package com.finebi.conf.provider;

import com.finebi.base.constant.BaseConstant;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.analysis.table.FineAnalysisTableImpl;
import com.finebi.conf.internalimp.response.bean.FineTableResponed;
import com.finebi.conf.service.engine.table.EngineTableManager;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.finebi.conf.structure.bean.table.AbstractFineTable;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.conf.business.ISwiftXmlWriter;
import com.fr.swift.conf.business.pack.PackXmlWriter;
import com.fr.swift.conf.business.pack.PackageParseXml;
import com.fr.swift.conf.business.pack.SwiftPackageDao;
import com.fr.swift.conf.business.table.SwiftTableDao;
import com.fr.swift.conf.business.table.TableParseXml;
import com.fr.swift.conf.business.table.TableXmlWriter;
import com.fr.swift.conf.business.table2source.dao.TableToSourceConfigDao;
import com.fr.swift.conf.business.table2source.dao.TableToSourceMemConfigDao;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-23 13:58:46
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftTableConfProvider implements EngineTableManager {

    private SwiftTableDao businessTableDAO;
    private SwiftPackageDao businessPackageDAO;
    private TableToSourceConfigDao tableToSourceConfigDao;
    private SwiftPackageConfProvider swiftPackageConfProvider;
    private String xmlFileName = "table.xml";
    private SwiftLogger logger = SwiftLoggers.getLogger(SwiftTableConfProvider.class);

    public SwiftTableConfProvider() {
        TableParseXml xmlHandler = new TableParseXml();
        ISwiftXmlWriter swiftXmlWriter = new TableXmlWriter();
        businessTableDAO = new SwiftTableDao(xmlHandler, xmlFileName, swiftXmlWriter);
        swiftPackageConfProvider = new SwiftPackageConfProvider();
        businessPackageDAO = new SwiftPackageDao(new PackageParseXml(), xmlFileName, new PackXmlWriter());
        tableToSourceConfigDao = new TableToSourceMemConfigDao();
    }

    @Override
    public List<FineBusinessTable> getAllTable() {
        return businessTableDAO.getAllConfig();
    }

    @Override
    public List<FineBusinessTable> getAllTableByPackId(String packId) {
        List<FineBusinessTable> resultBusinessTableList = new ArrayList<FineBusinessTable>();
        FineBusinessPackage fineBusinessPackage = swiftPackageConfProvider.getSinglePackage(packId);
        List<String> tableIdList = fineBusinessPackage.getTables();
        for (FineBusinessTable fineBusinessTable : businessTableDAO.getAllConfig()) {
            if (tableIdList.contains(fineBusinessTable.getId())) {
                resultBusinessTableList.add(fineBusinessTable);
            }
        }
        return resultBusinessTableList;
    }

    @Override
    public FineBusinessTable getSingleTable(String tableName) {
        for (FineBusinessTable fineBusinessTable : businessTableDAO.getAllConfig()) {
            if (ComparatorUtils.equals(tableName, fineBusinessTable.getName())) {
                return ((AbstractFineTable) fineBusinessTable).clone();
            }
        }
        return null;
    }

    @Override
    public FineBusinessField getField(String tableId, String fieldId) {
        FineBusinessTable fineBusinessTable = this.getSingleTable(tableId);
        if (fineBusinessTable != null) {
            return fineBusinessTable.getFieldByFieldId(fieldId);
        }
        return null;
    }

    @Override
    public FineTableResponed addTables(Map<String, List<FineBusinessTable>> tables) {
        List<FineBusinessPackage> needUpdatePackage = new ArrayList<FineBusinessPackage>();
        List<FineBusinessTable> newAddTables = new ArrayList<FineBusinessTable>();
        List<FineBusinessPackage> allPackage = businessPackageDAO.getAllConfig();
        for (FineBusinessPackage pack : allPackage) {
            if (tables.containsKey(pack.getId())) {
                List<FineBusinessTable> allTables = tables.get(pack.getId());
                for (FineBusinessTable table : allTables) {
                    if (!pack.getTables().contains(table.getName())) {
                        pack.addTable(table.getName());
                    }
                    try {
                        tableToSourceConfigDao.addConfig(table.getId(), IndexingDataSourceFactory.transformDataSource(table).getSourceKey().getId());
                    } catch (Exception e) {
                        logger.error("Cannot save tableId to sourceKey: ", e);
                    }
                }
                needUpdatePackage.add(pack);
                newAddTables.addAll(allTables);
            }
        }
        businessPackageDAO.updateConfigs(needUpdatePackage);
        businessTableDAO.updateConfigs(newAddTables);
        return new FineTableResponed();
    }

    private List<String> getTableNames(List<FineBusinessTable> tables) {
        List<String> result = new ArrayList<String>();
        for (FineBusinessTable table : tables) {
            result.add(table.getName());
        }
        return result;
    }

    @Override
    public boolean removeTable(List<String> tableIds) {
        List<FineBusinessTable> removeTableList = new ArrayList<FineBusinessTable>();
        List<FineBusinessTable> fineBusinessTableList = this.getAllTable();
        List<FineBusinessPackage> allPackage = businessPackageDAO.getAllConfig();
        List<FineBusinessPackage> needUpdatePackage = new ArrayList<FineBusinessPackage>();
        for (FineBusinessTable fineBusinessTable : fineBusinessTableList) {
            if (tableIds.contains(fineBusinessTable.getName())) {
                removeTableList.add(fineBusinessTable);
            }
        }
        try {
            for (FineBusinessPackage pack : allPackage) {
                for (FineBusinessTable table : removeTableList) {
                    if (pack.getTables().contains(table.getId())) {
                        pack.removeTable(table.getName());
                        if (!needUpdatePackage.contains(pack)) {
                            needUpdatePackage.add(pack);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("remove from package error", e);
        }
        return businessTableDAO.removeConfigs(removeTableList) && businessPackageDAO.updateConfigs(needUpdatePackage);
    }

    @Override
    public boolean updateTable(String tableId, String newName) {
        FineBusinessTable fineBusinessTable = this.getSingleTable(tableId);
        List<FineBusinessPackage> needUpdatePackage = new ArrayList<FineBusinessPackage>();
        fineBusinessTable.setName(newName);
        List<FineBusinessPackage> allPackage = businessPackageDAO.getAllConfig();
        try {
            for (FineBusinessPackage pack : allPackage) {
                if (pack.getTables().contains(tableId)) {
                    pack.removeTable(tableId);
                    pack.addTable(newName);
                    needUpdatePackage.add(pack);
                }
            }
        } catch (Exception e) {
            logger.error("remove from package error", e);
        }
        return businessTableDAO.updateConfig(fineBusinessTable) && businessPackageDAO.updateConfigs(needUpdatePackage);
    }

    @Override
    public boolean updateTables(List<FineBusinessTable> needUpdateTables) {
        return businessTableDAO.updateConfigs(needUpdateTables);
    }

    @Override
    public boolean updateField(String tableId, FineBusinessField field) {
        FineBusinessTable fineBusinessTable = this.getSingleTable(tableId);
        List<FineBusinessField> allFields = fineBusinessTable.getFields();
        for (FineBusinessField businessField : allFields) {
            if (ComparatorUtils.equals(businessField.getId(), field.getId())) {
                allFields.add(field);
            }
        }
        fineBusinessTable.setFields(allFields);
        return businessTableDAO.updateConfig(fineBusinessTable);
    }

    @Override
    public boolean isTableExist(String tableId) {
        FineBusinessTable fineBusinessTable = this.getSingleTable(tableId);
        return fineBusinessTable != null;
    }

    @Override
    public boolean isFieldExist(String tableId, String fieldId) {
        FineBusinessField fineBusinessField = this.getField(tableId, fieldId);
        return fineBusinessField != null;
    }

    @Override
    public List<FineBusinessTable> getBelongAnalysisTables(String tableName) {
        List<FineBusinessTable> result = new ArrayList<FineBusinessTable>();
        List<FineBusinessTable> allTable = businessTableDAO.getAllConfig();
        for (FineBusinessTable table : allTable) {
            if (ComparatorUtils.equals(BaseConstant.TABLETYPE.ANALYSIS, table.getType())) {
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
}
