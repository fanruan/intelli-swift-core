package com.fr.swift.source.etl.datamining;

import com.finebi.conf.algorithm.DMAbstractAlgorithm;
import com.finebi.conf.algorithm.DMAlgorithmFactory;
import com.finebi.conf.algorithm.DMColMetaData;
import com.finebi.conf.algorithm.DMDataModel;
import com.finebi.conf.algorithm.DMRowMetaData;
import com.finebi.conf.algorithm.DMType;
import com.finebi.conf.algorithm.EmptyAlgorithm;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2018/3/12 9:00
 */
public class DataMiningOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(DataMiningOperator.class);

    @CoreField
    private AlgorithmBean algorithmBean = null;

    private DMAbstractAlgorithm algorithm;

    public DataMiningOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
        try {
            this.algorithm = DMAlgorithmFactory.create(algorithmBean.getAlgorithmName());
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public AlgorithmBean getAlgorithmBean() {
        return this.algorithmBean;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        SwiftMetaData table = tables[0];
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        try {
            DMRowMetaData inputData = new DMRowMetaData();
            for (int i = 0; i < table.getColumnCount(); i++) {
                SwiftMetaDataColumn column = table.getColumn(i + 1);
                inputData.addColMeta(new DMColMetaData(column.getName(), DMType.fromSwiftInt(column.getType())));
            }
            DMRowMetaData outputMetaData;
            // 算法为empty的时候发布会上张表的数据
            if (algorithm instanceof EmptyAlgorithm) {
                outputMetaData = inputData;
            } else {
                DMDataModel inputModel = new DMDataModel(null, inputData);
                algorithm.init(algorithmBean, inputModel);
                outputMetaData = algorithm.getOutputMetaData();

            }
            for (DMColMetaData colMetaData : outputMetaData.getColMetas()) {
                columnList.add(new MetaDataColumnBean(colMetaData.getColName(), colMetaData.getColType().toSwiftInt()));
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        if (algorithm.isAddColumns()) {
            return OperatorType.EXTRA_TRUE;
        } else {
            return OperatorType.EXTRA_FALSE;
        }
    }

    @Override
    public List<String> getNewAddedName() {
        DMRowMetaData outputMetaData;
        List<String> list = new ArrayList<String>();
        try {
            outputMetaData = algorithm.getOutputMetaData();
            for (DMColMetaData colMetaData : outputMetaData.getColMetas()) {
                list.add(colMetaData.getColName());
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return list;
    }
}
