package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.DataMiningBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.rcompile.RCompileBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.rcompile.RCompileBeanValue;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.provider.DataProvider;
import com.fr.swift.provider.impl.SwiftDataProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.etl.datamining.DataMiningOperator;
import com.fr.swift.source.etl.rcompile.RCompileOperator;

import java.util.List;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class DataMiningAdaptor {
    public static DataMiningOperator fromDataMiningBean(DataMiningBean dmb) {
        AlgorithmBean bean = dmb.getValue();

        return new DataMiningOperator(bean);
    }

    public static RCompileOperator fromRCompileOperator(RCompileBean value, DataSource dataSource) {
        RCompileBeanValue bean = value.getValue();
        String uuid = bean.getUuid();
        String commands = bean.getCommands();
        if (null != dataSource) {
            try {
                DataProvider dataProvider = new SwiftDataProvider();
                List<Segment> segments = dataProvider.getPreviewData(dataSource);
                return new RCompileOperator(commands, uuid,
                        segments.toArray(new Segment[segments.size()]));
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
        return null;
    }
}