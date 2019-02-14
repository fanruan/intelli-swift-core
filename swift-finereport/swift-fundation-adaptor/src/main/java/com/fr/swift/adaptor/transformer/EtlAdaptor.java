package com.fr.swift.adaptor.transformer;

import com.finebi.conf.constant.ConfConstant.AnalysisType;
import com.finebi.conf.internalimp.analysis.bean.operator.add.AddNewColumnBean;
import com.finebi.conf.internalimp.analysis.bean.operator.circulate.CirculateOneFieldBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.DataMiningBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.rcompile.RCompileBean;
import com.finebi.conf.internalimp.analysis.bean.operator.filter.FilterOperatorBean;
import com.finebi.conf.internalimp.analysis.bean.operator.group.GroupBean;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinBean;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinBeanValue;
import com.finebi.conf.internalimp.analysis.bean.operator.trans.ColumnRowTransBean;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBean;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBeanValue;
import com.finebi.conf.internalimp.analysis.bean.operator.union.UnionBeanValueTable;
import com.finebi.conf.internalimp.analysis.table.FineAnalysisTableImpl;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.swift.adaptor.transformer.etl.AddColumnAdaptor;
import com.fr.swift.adaptor.transformer.etl.ColumnFilterAdaptor;
import com.fr.swift.adaptor.transformer.etl.ColumnRowTransAdaptor;
import com.fr.swift.adaptor.transformer.etl.DataMiningAdaptor;
import com.fr.swift.adaptor.transformer.etl.FieldSettingAdaptor;
import com.fr.swift.adaptor.transformer.etl.GroupSumAdaptor;
import com.fr.swift.adaptor.transformer.etl.JoinAdaptor;
import com.fr.swift.adaptor.transformer.etl.SelectFieldAdaptor;
import com.fr.swift.adaptor.transformer.etl.SelfCirculateAdaptor;
import com.fr.swift.adaptor.transformer.etl.UnionAdaptor;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.EtlSource;
import com.fr.swift.utils.BusinessTableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/30 0030 16:38
 */
public class EtlAdaptor {
    public static DataSource adaptEtlDataSource(FineBusinessTable table) throws Exception {
        FineAnalysisTable analysis = ((FineAnalysisTable) table);
        FineOperator op = analysis.getOperator();

        if (op.getType() == AnalysisType.SELECT_FIELD) {
            return SelectFieldAdaptor.adaptSelectField(analysis);
        }
        //排序没用，只能当作预览的属性和某些新增列的属性
        if (op.getType() == AnalysisType.SORT) {
            return adaptEtlDataSource(analysis.getBaseTable());
        }
        if (op.getType() == AnalysisType.FIELD_SETTING) {
            return FieldSettingAdaptor.adaptFieldSetting(analysis);
        }
        List<DataSource> dataSources = new ArrayList<DataSource>();
        FineBusinessTable baseTable = analysis.getBaseTable();
        try {
            if (baseTable != null) {
                dataSources.add(DataSourceFactory.getDataSourceInCache(baseTable));
            }
            dataSources.addAll(fromOperator(op));
            return new EtlSource(dataSources, adaptEtlOperator(op, table));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            return DataSourceFactory.getDataSourceInCache(baseTable);
        }
    }


    private static List<DataSource> fromOperator(FineOperator op) throws Exception {
        List<DataSource> dataSources = new ArrayList<DataSource>();
        switch (op.getType()) {
            case AnalysisType.JOIN: {
                JoinBeanValue jbv = op.<JoinBean>getValue().getValue();
                FineBusinessTable busiTable = BusinessTableUtils.getTableByTableName(jbv.getTable().getName());
                dataSources.add(DataSourceFactory.getDataSourceInCache(busiTable));
                break;
            }
            case AnalysisType.UNION:
                UnionBeanValue ubv = op.<UnionBean>getValue().getValue();
                for (UnionBeanValueTable table : ubv.getTables()) {
                    try {
                        FineBusinessTable busiTable = BusinessTableUtils.getTableByTableName(table.getName());
                        dataSources.add(DataSourceFactory.getDataSourceInCache(busiTable));
                    } catch (Exception e) {
                        continue;
                    }
                }
                break;
            default:
        }
        return dataSources;
    }

    private static SwiftMetaData toMeta(FineBusinessTable table) {
        String name = table.getName();
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (FineBusinessField field : table.getFields()) {
            columns.add(new MetaDataColumnBean(field.getName(), field.getType(), field.getSize()));
        }
        return new SwiftMetaDataBean(name, columns);
    }

    static ETLOperator adaptEtlOperator(FineOperator op, FineBusinessTable table) throws Exception {
        switch (op.getType()) {
            case AnalysisType.JOIN:
                return JoinAdaptor.fromJoinBean(op.<JoinBean>getValue());
            case AnalysisType.UNION:
                return UnionAdaptor.fromUnionBean(op.<UnionBean>getValue());
            case AnalysisType.FILTER:
                return ColumnFilterAdaptor.fromColumnFilterBean(op.<FilterOperatorBean>getValue(), table);
            case AnalysisType.CIRCLE_ONE_FIELD_CALCULATE:
                return SelfCirculateAdaptor.fromOneUnionRelationBean(op.<CirculateOneFieldBean>getValue(), table);
            case AnalysisType.CIRCLE_TWO_FIELD_CALCULATE:
                return SelfCirculateAdaptor.fromTwoUnionRelationBean(op.<CirculateOneFieldBean>getValue(), table);
            case AnalysisType.COLUMN_ROW_TRANS:
                return ColumnRowTransAdaptor.fromColumnRowTransBean(op.<ColumnRowTransBean>getValue(), table);
            case AnalysisType.ADD_COLUMN:
                return AddColumnAdaptor.fromAddNewColumnBean(op.<AddNewColumnBean>getValue(), table);
            case AnalysisType.GROUP:
                return GroupSumAdaptor.fromSumByGroupBean(op.<GroupBean>getValue());
            case AnalysisType.DATA_MINING:
                return DataMiningAdaptor.fromDataMiningBean(op.<DataMiningBean>getValue());
            case AnalysisType.R_COMPILE: {
                DataSource source = adaptEtlDataSource(((FineAnalysisTableImpl) table).getBaseTable());
                return DataMiningAdaptor.fromRCompileOperator(op.<RCompileBean>getValue(), source);
            }
            default:
        }
        return null;
    }
}