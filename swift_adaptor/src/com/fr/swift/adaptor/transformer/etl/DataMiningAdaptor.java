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
import com.fr.swift.source.etl.datamining.rcompile.RCompileOperator;
import com.fr.swift.source.etl.datamining.rcompile.RConnectionFactory;
import org.rosuda.REngine.Rserve.RConnection;

import java.util.List;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class DataMiningAdaptor {
    public static DataMiningOperator fromDataMiningBean(DataMiningBean dmb) {
        AlgorithmBean dmbv = dmb.getValue();

        return new DataMiningOperator(dmbv);
    }

    public static RCompileOperator fromRCompileOperator(RCompileBean value, DataSource dataSource) {
        RCompileBeanValue bean = value.getValue();
        boolean needExecute = bean.isNeedExecute();
        boolean init = bean.isInit();
        boolean cancelPrevious = bean.isCancelPreviousStep();
        String tableName = bean.getTableName();
        RConnection conn = RConnectionFactory.getRConnection();
        if (null != dataSource) {
            try {
                DataProvider dataProvider = new SwiftDataProvider();
                List<Segment> segments = dataProvider.getPreviewData(dataSource);
                if (!init) {
                    String commands = bean.getCommands();
                    if (null != commands && !"".equals(commands)) {
                        return new RCompileOperator(commands, needExecute, conn, tableName,
                                segments.toArray(new Segment[segments.size()]), null, null, cancelPrevious, init);
                    }
                } else {
                    String[] columns = bean.getColumns();
                    int[] columnType = bean.getColumnType();
                    return new RCompileOperator(null, needExecute, conn, tableName,
                            segments.toArray(new Segment[segments.size()]), columnType, columns, cancelPrevious, init);
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
        return null;
    }
}