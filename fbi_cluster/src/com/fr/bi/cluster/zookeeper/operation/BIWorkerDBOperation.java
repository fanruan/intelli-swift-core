package com.fr.bi.cluster.zookeeper.operation;

import com.fr.bi.cal.generate.CubeBuildBasicDBOperation;
import com.fr.bi.cal.generate.CubeBuildOperation;
import com.fr.bi.cluster.zookeeper.BINodeValueParser;
import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONParser;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Connery on 2015/4/2.
 */
public class BIWorkerDBOperation implements BIWorkerOperation {
    //    private static final FRLogger LOG = FRContext.getLogger();
//    private static final Logger LOG = LoggerFactory.getLogger(BIWorkerDBOperation.class);

    @Override
    public BIWorkerNodeValue operate(BIWorkerNodeValue nodeValue) {
        long userId = nodeValue.getUserId();
//        LOG.info("当前work正在进行读取数据库的工作");
        CubeBuildOperation operation = new CubeBuildBasicDBOperation(nodeValue.getBasePath(), nodeValue.getTmpPath(), userId);
        try {
            ArrayList<JSONParser> jsons = BINodeValueParser.string2BIJson(nodeValue.getTaskContent(), nodeValue.getTaskName());
//            LOG.info("当前worker处理表数量为：" + jsons.size());
//            System.out.println("当前worker处理表数量为：" + jsons.size());
            ArrayList<Table> tableKeys = new ArrayList<Table>();
            Iterator<JSONParser> it = jsons.iterator();
            while (it.hasNext()) {
                tableKeys.add((Table) it.next());
            }
            operation.process(tableKeys);
        } catch (Exception ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
        } finally {
            nodeValue.setStatus(BIWorkerNodeValue.STATUS_FINISH);
        }
        return nodeValue;
    }
}