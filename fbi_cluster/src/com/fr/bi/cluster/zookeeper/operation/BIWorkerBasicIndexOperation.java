package com.fr.bi.cluster.zookeeper.operation;

//import com.fr.bi.cal.generate.CubeBuildBasicIndexOperation;
//import com.fr.bi.cal.generate.CubeBuildOperation;
import com.fr.bi.cluster.zookeeper.BINodeValueParser;
import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import com.fr.bi.stable.data.Table;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.json.JSONParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Connery on 2015/4/3.
 */
public class BIWorkerBasicIndexOperation implements BIWorkerOperation {
//    private static final Logger LOG = LoggerFactory.getLogger(BIWorkerDBOperation.class);

    @Override
    public BIWorkerNodeValue operate(BIWorkerNodeValue nodeValue) {
//            现有集群未使用，先注释掉，否则造成循环依赖
//        long userId = nodeValue.getUserId();
//        LOG.info("当前work正在进行关联索引的生成工作" );
//        CubeBuildOperation operation = new CubeBuildBasicIndexOperation(nodeValue.getBasePath(), nodeValue.getTmpPath(), userId);
//        try {
//            ArrayList<JSONParser> jsons = BINodeValueParser.string2BIJson(nodeValue.getTaskContent(), nodeValue.getTaskName());
////            LOG.info("amount of current worker dealing basic index is: " + jsons.size());
//
//            HashSet<Table> tableKeys = new HashSet<Table>();
//            Iterator<JSONParser> it = jsons.iterator();
//            while (it.hasNext()) {
//                tableKeys.add((Table) it.next());
//            }
//            operation.process(tableKeys);
//
//        } catch (Exception ex) {
//             BILoggerFactory.getLogger().error(ex.getMessage(), ex);
//        } finally {
//            nodeValue.setStatus(BIWorkerNodeValue.STATUS_FINISH);
//        }
//        return nodeValue;
        return nodeValue;
    }
}