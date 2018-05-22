package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.constant.ConfConstant.AnalysisType;
import com.finebi.conf.internalimp.analysis.bean.operator.setting.FieldSettingBeanItem;
import com.finebi.conf.internalimp.analysis.operator.setting.FieldSettingOperator;
import com.finebi.conf.structure.analysis.operator.FineOperator;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.etl.EtlSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class FieldSettingAdaptor {
    public static DataSource adaptFieldSetting(FineAnalysisTable analysis) throws Exception {
        FineOperator op = analysis.getOperator();
        List<FieldSettingOperator> fieldSettingOperatorList = new ArrayList<FieldSettingOperator>();
        while (op.getType() == AnalysisType.FIELD_SETTING) {
            fieldSettingOperatorList.add(analysis.<FieldSettingOperator>getOperator());
            analysis = analysis.getBaseTable();
            op = analysis.getOperator();
            if (op.getType() == AnalysisType.SELECT_FIELD) {
                break;
            }
        }
        EtlSource source = (EtlSource) DataSourceFactory.getDataSourceInCache(analysis);
        return new EtlSource(source.getBasedSources(), source.getOperator(), createFieldsInfo(fieldSettingOperatorList, source));
    }

    private static Map<Integer, String> createFieldsInfo(List<FieldSettingOperator> fieldSettingOperatorList, EtlSource source) throws SwiftMetaDataException {
        Map<Integer, String> sourceFieldsInfo = source.getFieldsInfo();
        Map<Integer, String> sourceFullFieldInfo = new TreeMap<Integer, String>();
        Map<Integer, String> fieldInfo = new TreeMap<Integer, String>();
        //如果为空，就根据metadata创建
        if (sourceFieldsInfo == null || sourceFieldsInfo.isEmpty()) {
            for (int i = 0; i < source.getMetadata().getColumnCount(); ) {
                sourceFullFieldInfo.put(i, source.getMetadata().getColumnName(++i));
            }
        } else {
            sourceFullFieldInfo.putAll(sourceFieldsInfo);
        }
        for (int i = fieldSettingOperatorList.size() - 1; i >= 0; i--) {
            List<FieldSettingBeanItem> fieldSettings = fieldSettingOperatorList.get(i).getValue().getValue();
            Iterator<Entry<Integer, String>> fullFieldInfoIter = sourceFullFieldInfo.entrySet().iterator();
            for (FieldSettingBeanItem setting : fieldSettings) {
                Entry<Integer, String> entry = fullFieldInfoIter.next();
                if (setting.isUsed()) {
                    fieldInfo.put(entry.getKey(), setting.getName());
                }
            }
        }
        return fieldInfo.isEmpty() ? sourceFieldsInfo : fieldInfo;
    }
}