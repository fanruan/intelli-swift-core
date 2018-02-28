package com.fr.swift.adaptor.transformer;

import com.finebi.base.constant.BaseConstant;
import com.finebi.base.constant.BaseConstant.TABLETYPE;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.internalimp.analysis.bean.operator.confselect.ConfSelectBeanItem;
import com.finebi.conf.internalimp.analysis.operator.confselect.ConfSelectOperator;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.basictable.table.FineSQLBusinessTable;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.table.AbstractFineTable;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.utils.FineConnectionUtils;
import com.fr.swift.increase.IncrementImpl;
import com.fr.swift.increment.Increment;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.DBDataSource;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.container.SourceContainerManager;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.etl.ETLSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-12 15:49:48
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class IndexingDataSourceFactory {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    public static void transformDataSources(Map<FineBusinessTable, TableUpdateInfo> infoMap, List<String> updateTableSourceKeys, SourceContainerManager updateSourceContainer, Map<String, List<Increment>> incrementMap) throws Exception {
        for (Map.Entry<FineBusinessTable, TableUpdateInfo> infoEntry : infoMap.entrySet()) {
            DataSource updateDataSource = transformDataSource(infoEntry.getKey());
            if (updateDataSource != null) {
                updateTableSourceKeys.add(updateDataSource.getSourceKey().getId());
                updateSourceContainer.getDataSourceContainer().addSource(updateDataSource);

                if (updateDataSource instanceof DBDataSource && infoEntry.getValue() != null) {
                    Increment increment;
                    if (updateDataSource instanceof QueryDBSource) {
                        increment = transformIncrement(infoEntry.getValue(), updateDataSource.getSourceKey(), ((QueryDBSource) updateDataSource).getConnectionName());
                    } else {
                        increment = transformIncrement(infoEntry.getValue(), updateDataSource.getSourceKey(), ((TableDBSource) updateDataSource).getConnectionName());
                    }

                    if (incrementMap.containsKey(updateDataSource.getSourceKey().getId())) {
                        incrementMap.get(updateDataSource.getSourceKey().getId()).add(increment);
                    } else {
                        incrementMap.put(updateDataSource.getSourceKey().getId(), new ArrayList<Increment>());
                        incrementMap.get(updateDataSource.getSourceKey().getId()).add(increment);
                    }
                }
            }
        }
    }

    public static List<DataSource> transformDataSources(List<FineBusinessTable> tables) throws Exception {
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        if (tables != null) {
            for (FineBusinessTable table : tables) {
                DataSource updateDataSource = transformDataSource(table);
                if (updateDataSource != null) {
                    dataSourceList.add(updateDataSource);
                }
            }
        }
        return dataSourceList;
    }

    public static DataSource transformDataSource(FineBusinessTable table) throws Exception {
        DataSource dataSource = null;
        List<FineOperator> ops = ((AbstractFineTable) table).getOperators();
        switch (table.getType()) {
            case BaseConstant.TABLETYPE.DB:
                dataSource = transformTableDBSource((FineDBBusinessTable) table);
                break;
            case BaseConstant.TABLETYPE.SQL:
                dataSource = transformQueryDBSource((FineSQLBusinessTable) table);
                break;
            case BaseConstant.TABLETYPE.SERVER:
                break;
//            case BaseConstant.TABLETYPE.EXCEL:
//                dataSource = transformExcelDataSource((FineExcelBusinessTable) table);
//                break;
            case TABLETYPE.ETL:
            case BaseConstant.TABLETYPE.ANALYSIS:
                dataSource = EtlAdaptor.adaptEtlDataSource(table);
                break;
            default:
                dataSource = EtlAdaptor.adaptEtlDataSource(table);
        }
        return dataSource;
    }

//    private static ETLSource transformETLDataSource(FineAnalysisTable table) {
//
//        try {
//            LinkedHashMap<String, List<ColumnKey>> sourceKeyColumnMap = new LinkedHashMap<String, List<ColumnKey>>();
//            LinkedHashMap<String, DataSource> sourceKeyDataSourceMap = new LinkedHashMap<String, DataSource>();
//            SelectFieldOperator selectFieldOperator = table.getOperator();
//            List<SelectFieldBeanItem> selectFieldBeanItemList = selectFieldOperator.getValue().getValue();
//            for (SelectFieldBeanItem selectFieldBeanItem : selectFieldBeanItemList) {
//                FineBusinessTable fineBusinessTable = FineTableUtils.getTableByFieldId(selectFieldBeanItem.getField());
//                FineBusinessField fineBusinessField = fineBusinessTable.getFieldByFieldId(selectFieldBeanItem.getField());
//                DataSource baseDataSource = transformDataSource(fineBusinessTable);
//
//                if (sourceKeyColumnMap.containsKey(baseDataSource.getSourceKey().getId())) {
//                    sourceKeyColumnMap.get(baseDataSource.getSourceKey().getId()).add(new ColumnKey(fineBusinessField.getName()));
//                } else {
//                    sourceKeyColumnMap.put(baseDataSource.getSourceKey().getId(), new ArrayList<ColumnKey>());
//                    sourceKeyColumnMap.get(baseDataSource.getSourceKey().getId()).add(new ColumnKey(fineBusinessField.getName()));
//                }
//                if (!sourceKeyDataSourceMap.containsKey(baseDataSource.getSourceKey().getId())) {
//                    sourceKeyDataSourceMap.put(baseDataSource.getSourceKey().getId(), baseDataSource);
//                }
//            }
//            List<DataSource> baseDatas = new ArrayList<DataSource>();
//            List<SwiftMetaData> swiftMetaDatas = new ArrayList<SwiftMetaData>();
//            List<ColumnKey[]> fields = new ArrayList<ColumnKey[]>();
//
//            for (Map.Entry<String, List<ColumnKey>> entry : sourceKeyColumnMap.entrySet()) {
//                DataSource dataSource = sourceKeyDataSourceMap.get(entry.getKey());
//                baseDatas.add(dataSource);
//                swiftMetaDatas.add(dataSource.getMetadata());
//                fields.add(entry.getValue().toArray(new ColumnKey[entry.getValue().size()]));
//            }
//            ETLOperator operator = new DetailOperator(fields, swiftMetaDatas);
//            ETLSource etlSource = new ETLSource(baseDatas, operator);
//            return etlSource;
//        } catch (FineConfigException e) {
//            LOGGER.error(e.getMessage(), e);
//        } catch (Exception ee) {
//            LOGGER.error(ee.getMessage(), ee);
//        }
//        return null;
//    }


    private static DataSource transformTableDBSource(FineDBBusinessTable table) throws Exception {
        String connectionName = table.getConnName();
        FineConnection fineConnection = FineConnectionUtils.getConnectionByName(connectionName);
        ConnectionInfo connectionInfo = new SwiftConnectionInfo(fineConnection.getSchema(), fineConnection.getConnection());
        ConnectionManager.getInstance().registerConnectionInfo(connectionName, connectionInfo);
        Map<String, ColumnType> fieldColumnTypes = checkFieldTypes(table.getOperators());
        TableDBSource tableDBSource = fieldColumnTypes == null ?
                new TableDBSource(table.getTableName(), connectionName) : new TableDBSource(table.getTableName(), connectionName, fieldColumnTypes);
        return checkETL(tableDBSource, table);
    }

    private static Map<String, ColumnType> checkFieldTypes(List<FineOperator> operators) {
        if (operators != null && !operators.isEmpty()) {
            FineOperator op = operators.get(0);
            if (op.getType() == ConfConstant.AnalysisType.CONF_SELECT) {
                List<ConfSelectBeanItem> items = ((ConfSelectOperator) op).getFields();
                Map<String, ColumnType> fieldsTypes = new HashMap<String, ColumnType>();
                for (ConfSelectBeanItem item : items) {
                    if (item.isUsable()) {
                        fieldsTypes.put(item.getName(), FieldFactory.transformBIColumnType2SwiftColumnType(item.getType()));
                    }
                }
                return fieldsTypes;
            }
        }
        return null;
    }

    private static DataSource transformQueryDBSource(FineSQLBusinessTable table) throws Exception {

        String connectionName = table.getConnName();
        FineConnection fineConnection = FineConnectionUtils.getConnectionByName(connectionName);
        ConnectionInfo connectionInfo = new SwiftConnectionInfo(fineConnection.getSchema(), fineConnection.getConnection());
        ConnectionManager.getInstance().registerConnectionInfo(connectionName, connectionInfo);
        Map<String, ColumnType> fieldColumnTypes = checkFieldTypes(table.getOperators());
        QueryDBSource queryDBSource = fieldColumnTypes == null ?
                new QueryDBSource(table.getSql(), table.getConnName()) : new QueryDBSource(table.getSql(), table.getConnName(), fieldColumnTypes);
        return checkETL(queryDBSource, table);
    }

    private static DataSource checkETL(DataSource source, AbstractFineTable table) throws Exception{
        List<FineOperator> operators = table.getOperators();
        if (operators == null || operators.size() < 2){
            return source;
        }
        List<DataSource> baseSource = new ArrayList<DataSource>();
        baseSource.add(source);
        return new ETLSource(baseSource, EtlAdaptor.adaptEtlOperator(operators.get(operators.size() - 1), table));
    }

//    private static ExcelDataSource transformExcelDataSource(FineExcelBusinessTable table) {
//        return null;
//    }

    public static Increment transformIncrement(TableUpdateInfo tableUpdateInfo, SourceKey sourceKey, String connectionName) {
        Increment increment = new IncrementImpl(tableUpdateInfo.getAddSql(), tableUpdateInfo.getDeleteSql(), tableUpdateInfo.getModifySql(), sourceKey, connectionName, tableUpdateInfo.getUpdateType());
        return increment;
    }


//    private static DataSource transformETLDataSource(FineAnalysisTable table) throws Exception {
//        FineBusinessTable baseTable = table.getBaseTable();
//        switch (table.getOperator().getType()) {
//            case ConfConstant.AnalysisType.SELECT_FIELD:
//                return transformSelectField(table);
//            case ConfConstant.AnalysisType.SORT:
//                return transformSort(table);
////            case ConfConstant.AnalysisType.FIELD_SETTING:
//
//        }
//        return null;
//    }

    //    private static DataSource transformSort(FineAnalysisTable table) throws Exception {
//        List<DataSource> baseDatas = new ArrayList<DataSource>();
//        if (table.getBaseTable() != null) {
//            baseDatas.add(transformDataSource(table.getBaseTable()));
//        }
//        ETLSource etlSource = new ETLSource(baseDatas, null);
//        return null;
//    }
//
}