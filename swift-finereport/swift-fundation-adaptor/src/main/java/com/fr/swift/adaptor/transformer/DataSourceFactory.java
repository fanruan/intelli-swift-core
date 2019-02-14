package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.BICommonConstants;
import com.finebi.conf.constant.ConfConstant;
import com.finebi.conf.internalimp.analysis.bean.operator.confselect.ConfSelectBeanItem;
import com.finebi.conf.internalimp.analysis.operator.confselect.ConfSelectOperator;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.basictable.table.FineExcelBusinessTable;
import com.finebi.conf.internalimp.basictable.table.FineSQLBusinessTable;
import com.finebi.conf.internalimp.basictable.table.FineSQLTableParameter;
import com.finebi.conf.internalimp.bean.table.FineAttachment;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.bean.table.AbstractFineTable;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.swift.cache.SourceCache;
import com.fr.swift.generate.preview.MinorSegmentManager;
import com.fr.swift.increase.IncrementImpl;
import com.fr.swift.increment.Increment;
import com.fr.swift.increment.Increment.UpdateType;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.DBDataSource;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.container.SourceContainerManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.empty.EmptyDataSource;
import com.fr.swift.source.etl.EtlSource;
import com.fr.swift.source.excel.ExcelDataSource;
import com.fr.swift.source.excel.data.ExcelDataModelCreator;
import com.fr.swift.source.excel.data.IExcelDataModel;
import com.fr.swift.source.excel.exception.ExcelException;

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
public class DataSourceFactory {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger();

    public static void transformDataSources(Map<FineBusinessTable, TableUpdateInfo> infoMap, SourceContainerManager updateSourceContainer, Map<String, List<Increment>> incrementMap) {
        for (Map.Entry<FineBusinessTable, TableUpdateInfo> infoEntry : infoMap.entrySet()) {
            DataSource updateDataSource = getDataSourceInCache(infoEntry.getKey());
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

    public static List<DataSource> transformDataSources(List<FineBusinessTable> tables) {
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        if (tables != null) {
            for (FineBusinessTable table : tables) {
                try {
                    DataSource updateDataSource = getDataSourceInCache(table);
                    if (updateDataSource != null) {
                        dataSourceList.add(updateDataSource);
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
        return dataSourceList;
    }


    /**
     * @param table
     * @return
     * @throws Exception
     * @description 优先从cache中取，取不到去配置取，再取不到才去数据源取
     * 内部统一调用此方法!!
     */
    public static DataSource getDataSourceInCache(FineBusinessTable table) {
        try {
            DataSource dataSource = getDataSource(table);
            dataSource = SourceCache.getCache().getMetaDataBySource(dataSource);
            return dataSource;
        } catch (ExcelException e){
            // 如果是ExcelException表示DataSource是Excel，抛错没影响，没毛病
            throw e;
        } catch (Exception e) {
            LOGGER.error(e);
            return new EmptyDataSource();
        }
    }

    /**
     * @param table
     * @return
     * @description 直接从数据源取，同时刷新cache
     * 只有前端新增和更新表，会走这个方法
     */
    public static DataSource getDataSourceInSource(FineBusinessTable table) {
        try {
            DataSource dataSource = getDataSource(table);
            SourceCache.getCache().putSource2MetaData(dataSource);
            MinorSegmentManager.getInstance().remove(dataSource.getSourceKey());
            return dataSource;
        } catch (Exception e) {
            LOGGER.error(e);
            return new EmptyDataSource();
        }
    }

    private static DataSource getDataSource(FineBusinessTable table) throws Exception {
        DataSource dataSource = null;
        switch (table.getType()) {
            case BICommonConstants.TABLE.DATABASE:
                dataSource = transformTableDBSource((FineDBBusinessTable) table);
                break;
            case BICommonConstants.TABLE.SQL:
                dataSource = transformQueryDBSource((FineSQLBusinessTable) table);
                break;
            case BICommonConstants.TABLE.EXCEL:
                dataSource = transformExcelDataSource((FineExcelBusinessTable) table);
                break;
            case BICommonConstants.TABLE.ANALYSIS:
                dataSource = EtlAdaptor.adaptEtlDataSource(table);
                break;
            default:
                dataSource = EtlAdaptor.adaptEtlDataSource(table);
        }
        return dataSource;
    }

    private static DataSource transformTableDBSource(FineDBBusinessTable table) throws Exception {
        String connectionName = table.getConnName();
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
                        fieldsTypes.put(item.getName(), FieldFactory.toColumnType(item.getType()));
                    }
                }
                return fieldsTypes;
            }
        }
        return null;
    }

    private static DataSource transformExcelDataSource(FineExcelBusinessTable table) throws Exception{
        Attachment baseAttachment = AttachmentSource.getAttachment(table.getBaseAttach().getId());
//        String path = FRContext.getCurrentEnv().getPath() + File.separator + baseAttachment.getPath();
        IExcelDataModel excelDataModel = ExcelDataModelCreator.createDataModel(null);
        String[] columnNames = excelDataModel.onlyGetColumnNames();
        ColumnType[] columnTypes = excelDataModel.onlyGetColumnTypes();

        List<FineAttachment> additionAttachments = table.getAdditionalAttach();
        List<String> additionPaths = new ArrayList<String>();
        if (additionAttachments != null && !additionAttachments.isEmpty()) {
            for (FineAttachment additionAttachment : additionAttachments) {
                Attachment attachment = AttachmentSource.getAttachment(additionAttachment.getId());
//                String additionPath = FRContext.getCurrentEnv().getPath() + File.separator + attachment.getPath();
                additionPaths.add(null);
            }
        }
        Map<String, ColumnType> fieldColumnTypes = checkFieldTypes(table.getOperators());
        if (fieldColumnTypes != null && !fieldColumnTypes.isEmpty()){
            columnTypes = new ColumnType[columnNames.length];
            for (int i = 0; i < columnNames.length; i++){
                columnTypes[i] = fieldColumnTypes.get(columnNames[i]);
            }
        }
        ExcelDataSource excelDataSource = new ExcelDataSource(null, columnNames, columnTypes, additionPaths);
        return checkETL(excelDataSource, table);
    }

    private static DataSource transformQueryDBSource(FineSQLBusinessTable table) throws Exception {

        Map<String, ColumnType> fieldColumnTypes = checkFieldTypes(table.getOperators());

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

    public static boolean isIncrement(TableUpdateInfo tableUpdateInfo) {
        return !tableUpdateInfo.getAddSql().isEmpty() || !tableUpdateInfo.getDeleteSql().isEmpty() || !tableUpdateInfo.getModifySql().isEmpty();
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