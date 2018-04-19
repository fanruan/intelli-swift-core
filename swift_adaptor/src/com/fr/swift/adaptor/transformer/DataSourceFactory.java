package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.internalimp.analysis.bean.operator.confselect.ConfSelectBeanItem;
import com.finebi.conf.internalimp.analysis.operator.confselect.ConfSelectOperator;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.basictable.table.FineSQLBusinessTable;
import com.finebi.conf.internalimp.basictable.table.FineSQLTableParameter;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.bean.table.AbstractFineTable;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.cache.SourceCache;
import com.fr.swift.increase.IncrementImpl;
import com.fr.swift.increment.Increment;
import com.fr.swift.increment.Increment.UpdateType;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.ConnectionProvider;
import com.fr.swift.service.LocalSwiftServerService;
import com.fr.swift.service.SwiftAnalyseService;
import com.fr.swift.service.SwiftHistoryService;
import com.fr.swift.service.SwiftIndexingService;
import com.fr.swift.service.SwiftRealTimeService;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.DBDataSource;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.container.SourceContainerManager;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.empty.EmptyDataSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLTransferOperator;
import com.fr.swift.source.etl.EtlSource;
import com.fr.swift.source.etl.EtlTransferOperatorFactory;
import com.fr.swift.source.etl.datamining.DataMiningOperator;
import com.fr.swift.source.etl.datamining.DataMiningTransferOperator;
import com.fr.swift.source.etl.datamining.rcompile.RCompileOperator;
import com.fr.swift.source.etl.datamining.rcompile.RCompileTransferOperator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-12 15:49:48
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class DataSourceFactory {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    static {
        ConnectionManager.getInstance().registerProvider(new ConnectionProvider());
        EtlTransferOperatorFactory.register(DataMiningOperator.class, new EtlTransferOperatorFactory.ETLTransferCreator() {

            @Override
            public ETLTransferOperator createTransferOperator(ETLOperator operator) {
                return new DataMiningTransferOperator(((DataMiningOperator) operator).getAlgorithmBean());
            }
        });
        EtlTransferOperatorFactory.register(RCompileOperator.class, new EtlTransferOperatorFactory.ETLTransferCreator() {

            @Override
            public ETLTransferOperator createTransferOperator(ETLOperator operator) {
                RCompileOperator op = (RCompileOperator) operator;
                List dataList = op.getDataList();
                String[] column = (String[]) dataList.get(0);
                int[] columnType = (int[]) dataList.get(1);
                return new RCompileTransferOperator(column, columnType, op.getDataList());
            }
        });

        try {
            new LocalSwiftServerService().start();
            new SwiftAnalyseService().start();
            new SwiftHistoryService().start();
            new SwiftIndexingService().start();
            new SwiftRealTimeService().start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void transformDataSources(Map<FineBusinessTable, TableUpdateInfo> infoMap, SourceContainerManager updateSourceContainer, Map<String, List<Increment>> incrementMap) throws Exception {
        for (Map.Entry<FineBusinessTable, TableUpdateInfo> infoEntry : infoMap.entrySet()) {
            DataSource updateDataSource = getDataSource(infoEntry.getKey());
            if (updateDataSource != null) {
                updateSourceContainer.getDataSourceContainer().addSource(updateDataSource);

                if (updateDataSource instanceof DBDataSource && infoEntry.getValue() != null) {
                    Increment increment;
                    if (updateDataSource instanceof QueryDBSource) {
                        increment = transformIncrement(infoEntry.getValue(), updateDataSource.getSourceKey(), ((QueryDBSource) updateDataSource).getConnectionName());
                    } else {
                        increment = transformIncrement(infoEntry.getValue(), updateDataSource.getSourceKey(), ((TableDBSource) updateDataSource).getConnectionName());
                    }

                    if (increment != null) {
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
    }

    public static List<DataSource> transformDataSources(List<FineBusinessTable> tables) throws Exception {
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        if (tables != null) {
            for (FineBusinessTable table : tables) {
                DataSource updateDataSource = getDataSource(table);
                if (updateDataSource != null) {
                    dataSourceList.add(updateDataSource);
                }
            }
        }
        return dataSourceList;
    }


    /**
     * 外部和接口调用
     * 转换table->datasource
     *
     * @param table
     * @return
     */
    public static DataSource transformDataSource(FineBusinessTable table) {
        try {
            return getDataSource(table);
        } catch (Exception e) {
            LOGGER.error(e);
            return new EmptyDataSource();
        }
    }

    /**
     * 内部和包内调用
     * 转换table->datasource
     *
     * @param table
     * @return
     * @throws Exception
     */
    public static DataSource getDataSource(FineBusinessTable table) throws Exception {
        DataSource dataSource = null;
        switch (table.getType()) {
            case BICommonConstants.TABLE.DATABASE:
                dataSource = transformTableDBSource((FineDBBusinessTable) table);
                break;
            case BICommonConstants.TABLE.SQL:
                dataSource = transformQueryDBSource((FineSQLBusinessTable) table);
                break;
//            case BICommonConstants.TABLE.EXCEL:
//                dataSource = transformExcelDataSource((FineExcelBusinessTable) table);
//                break;
            case BICommonConstants.TABLE.ANALYSIS:
                dataSource = EtlAdaptor.adaptEtlDataSource(table);
                break;
            default:
                dataSource = EtlAdaptor.adaptEtlDataSource(table);
        }
        dataSource = SourceCache.getCache().getMetaDataBySource(dataSource);
        return dataSource;
    }

    private static DataSource transformTableDBSource(FineDBBusinessTable table) throws Exception {
        String connectionName = table.getConnName();
        LinkedHashMap<String, ColumnType> fieldColumnTypes = checkFieldTypes(table.getOperators());
        TableDBSource tableDBSource = fieldColumnTypes == null ?
                new TableDBSource(table.getTableName(), connectionName) : new TableDBSource(table.getTableName(), connectionName, fieldColumnTypes);
        return checkETL(tableDBSource, table);
    }

    private static LinkedHashMap<String, ColumnType> checkFieldTypes(List<FineOperator> operators) {
        if (operators != null && !operators.isEmpty()) {
            FineOperator op = operators.get(0);
            if (op.getType() == ConfConstant.AnalysisType.CONF_SELECT) {
                List<ConfSelectBeanItem> items = ((ConfSelectOperator) op).getFields();
                LinkedHashMap<String, ColumnType> fieldsTypes = new LinkedHashMap<String, ColumnType>();
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

        LinkedHashMap<String, ColumnType> fieldColumnTypes = checkFieldTypes(table.getOperators());

        String sql = table.getSql();
        List<FineSQLTableParameter> sqlTableParameters = table.getParamSetting();
        if (sqlTableParameters != null) {
            for (FineSQLTableParameter parameter : sqlTableParameters) {
                String name = parameter.getName();
                String value = parameter.getValue();
                String compareName = "\\$" + "\\{" + name + "\\}";
                sql = sql.replaceAll(compareName, value);
            }
        }

        QueryDBSource queryDBSource = fieldColumnTypes == null ?
                new QueryDBSource(sql, table.getConnName()) : new QueryDBSource(table.getSql(), table.getConnName(), fieldColumnTypes);
        return checkETL(queryDBSource, table);
    }

    private static DataSource checkETL(DataSource source, AbstractFineTable table) throws Exception {
        List<FineOperator> operators = table.getOperators();
        if (operators == null || operators.size() < 2) {
            return source;
        }
        List<DataSource> baseSource = new ArrayList<DataSource>();
        baseSource.add(source);
        return new EtlSource(baseSource, EtlAdaptor.adaptEtlOperator(operators.get(operators.size() - 1), table));
    }

    private static Increment transformIncrement(TableUpdateInfo tableUpdateInfo, SourceKey sourceKey, String connectionName) {
        if (!isIncrement(tableUpdateInfo)) {
            return null;
        }
        Increment increment = new IncrementImpl(tableUpdateInfo.getAddSql(), tableUpdateInfo.getDeleteSql(), tableUpdateInfo.getModifySql(), sourceKey, connectionName, adaptUpdateType(tableUpdateInfo.getUpdateType()));
        return increment;
    }

    private static boolean isIncrement(TableUpdateInfo tableUpdateInfo) {
        if (tableUpdateInfo.getAddSql().isEmpty() && tableUpdateInfo.getDeleteSql().isEmpty() && tableUpdateInfo.getModifySql().isEmpty()) {
            return false;
        }
        return true;
    }

    private static UpdateType adaptUpdateType(int type) {
        switch (type) {
            case 1:
                return UpdateType.ALL;
            case 2:
                return UpdateType.PART;
            case 3:
                return UpdateType.NEVER;
            default:
                return null;
        }
    }
}