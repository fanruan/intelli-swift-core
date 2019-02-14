package com.fr.swift.driver;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.driver.CommonDatabaseDataSourceDriver;
import com.finebi.common.internalimp.config.driver.utils.CheckSqlDriverTypeUtils;
import com.finebi.common.internalimp.config.driver.utils.TransferNameUtils;
import com.finebi.common.internalimp.config.entryinfo.sql.DatabaseEntryInfo;
import com.finebi.common.internalimp.config.fieldinfo.SimpleFieldPersist;
import com.finebi.common.structure.config.driver.CommonDBDataSourceDriver;
import com.finebi.common.structure.config.entryinfo.EntryInfo;
import com.finebi.common.structure.config.fieldinfo.FieldInfoPersist;
import com.finebi.common.structure.config.relation.Relation;
import com.finebi.common.structure.config.utils.TableIdCreator;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.engine.utils.SecurityUtil;
import com.fr.engine.utils.StringUtils;
import com.fr.general.ComparatorUtils;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.conf.business.relation.TableRelationReader;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/4/10
 */
public class SwiftDatabaseDataSourceDriver extends CommonDatabaseDataSourceDriver implements CommonDBDataSourceDriver {

    public SwiftDatabaseDataSourceDriver() {
        super(FineEngineType.Cube);
    }

    @Override
    public EntryInfo createEntryInfoWithTranslations(FineBusinessTable table) {
        FineDBBusinessTable dbBusinessTable = (FineDBBusinessTable) table;
        DatabaseEntryInfo entryInfo = new DatabaseEntryInfo();
        entryInfo.setDbTableName(dbBusinessTable.getTableName());
        entryInfo.setDbName(dbBusinessTable.getConnName());
        entryInfo.setId(SecurityUtil.MD5_16(dbBusinessTable.getId()));
        entryInfo.setSchema(getSchemaFromConnName(dbBusinessTable.getConnName()));
        entryInfo.setName(dbBusinessTable.getName());
        Map<String, String> escapeMap = new HashMap<String, String>();
        try {
            SwiftMetaData metaData = DataSourceFactory.getDataSourceInCache(table).getMetadata();
            String tableRemark = metaData.getRemark();
            if (!StringUtils.isEmpty(tableRemark)) {
                escapeMap.put(entryInfo.getID(), tableRemark);
            }
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnRemark = metaData.getColumnRemark(i);
                for (FineBusinessField fineBusinessField : table.getFields()) {
                    if (ComparatorUtils.equals(fineBusinessField.getName(), metaData.getColumnName(i))) {
                        columnRemark = fineBusinessField.getTransferName();
                    }
                }
                if (!StringUtils.isEmpty(columnRemark)) {
                    escapeMap.put(metaData.getColumnName(i), columnRemark);
                }
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        entryInfo.setEscapeMap(escapeMap);
        return entryInfo;
    }

    private String getSchemaFromConnName(String connName) {
        ConnectionInfo info = ConnectionManager.getInstance().getConnectionInfo(connName);
        if (null != info) {
            return info.getSchema();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public EntryInfo createEntryInfo(FineBusinessTable table) {
        CheckSqlDriverTypeUtils.checkDBDriverType(table.getType());
        FineDBBusinessTable dbBusinessTable = (FineDBBusinessTable) table;
        DatabaseEntryInfo entryInfo = new DatabaseEntryInfo();
        entryInfo.setDbTableName(dbBusinessTable.getTableName());
        entryInfo.setDbName(dbBusinessTable.getConnName());
        entryInfo.setId(TableIdCreator.getInstance().createTableId(dbBusinessTable.getId()));
        entryInfo.setSchema(this.getSchemaFromConnName(dbBusinessTable.getConnName()));
        entryInfo.setName(dbBusinessTable.getName());
        Map<String, String> escapeMap = TransferNameUtils.updateFieldEscapeMap(table, entryInfo);
        entryInfo.setEscapeMap(escapeMap);
        this.entryInfoFactory.updateEntryInfoName(entryInfo, table.getTransferName());
        return entryInfo;
    }


    @Override
    public List<Relation> getDataBaseTableRelations(String s, EntryInfo entryInfo) {
        return TableRelationReader.create(s, entryInfo).build();
    }

    @Override
    public FieldInfoPersist getFieldInfoPersist() {
        return SimpleFieldPersist.INSTANCE;
    }
}
