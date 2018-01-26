package com.finebi.conf.provider;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.service.engine.provider.table.EngineTableManager;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.general.ComparatorUtils;
import com.fr.swift.conf.business.ISwiftXmlWriter;
import com.fr.swift.conf.business.table.SwiftTableDao;
import com.fr.swift.conf.business.table.TableParseXml;
import com.fr.swift.conf.business.table.TableXmlWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-23 13:58:46
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftTableConfProvider implements EngineTableManager {

    private SwiftTableDao businessTableDAO;
    private SwiftPackageConfProvider swiftPackageConfProvider;
    private String xmlFileName = "table.xml";

    public SwiftTableConfProvider() {
        TableParseXml xmlHandler = new TableParseXml();
        ISwiftXmlWriter swiftXmlWriter = new TableXmlWriter();
        businessTableDAO = new SwiftTableDao(xmlHandler, xmlFileName, swiftXmlWriter);
        swiftPackageConfProvider = new SwiftPackageConfProvider();
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
    public FineBusinessTable getSingleTable(String tableId) {
        for (FineBusinessTable fineBusinessTable : businessTableDAO.getAllConfig()) {
            if (ComparatorUtils.equals(tableId, fineBusinessTable.getId())) {
                return fineBusinessTable;
            }
        }
        return null;
    }

    @Override
    public FineBusinessTable getTableByName(String tableName) {
        for (FineBusinessTable fineBusinessTable : businessTableDAO.getAllConfig()) {
            if (ComparatorUtils.equals(tableName, fineBusinessTable.getName())) {
                return fineBusinessTable;
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
    public boolean addTables(List<FineBusinessTable> tables) {
        return businessTableDAO.saveConfigs(tables);
    }

    @Override
    public boolean removeTable(List<String> tableIds) {
        List<FineBusinessTable> removeTableList = new ArrayList<FineBusinessTable>();
        List<FineBusinessTable> fineBusinessTableList = this.getAllTable();

        for (FineBusinessTable fineBusinessTable : fineBusinessTableList) {
            if (tableIds.contains(fineBusinessTable.getId())) {
                removeTableList.add(fineBusinessTable);
            }
        }
        return businessTableDAO.removeConfigs(removeTableList);
    }

    @Override
    public boolean updateTable(String tableId, String newName) {
        FineBusinessTable fineBusinessTable = this.getSingleTable(tableId);
        fineBusinessTable.updateTableName(newName);
        return businessTableDAO.updateConfig(fineBusinessTable);
    }

    @Override
    public boolean updateTables(List<FineBusinessTable> needUpdateTables) {
        return businessTableDAO.updateConfigs(needUpdateTables);
    }

    @Override
    public boolean updateField(String tableId, FineBusinessField field) {
        FineBusinessTable fineBusinessTable = this.getSingleTable(tableId);
        fineBusinessTable.addField(field);
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
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
