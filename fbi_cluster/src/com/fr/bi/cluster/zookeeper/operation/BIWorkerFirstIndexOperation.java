package com.fr.bi.cluster.zookeeper.operation;

import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.cluster.zookeeper.BINodeValueParser;
import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.json.JSONParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Connery on 2015/4/2.
 */
public class BIWorkerFirstIndexOperation implements BIWorkerOperation {
//    private static final Logger LOG = LoggerFactory.getLogger(BIWorkerFirstIndexOperation.class);

    @Override

    public BIWorkerNodeValue operate(BIWorkerNodeValue nodeValue) {
        long userId = nodeValue.getUserId();
//        LOG.info("当前work正在进行基础索引的工作" );
//        CubeBuildOperation operation = new CubeBuildFirstIndexOperation(nodeValue.getBasePath(), nodeValue.getTmpPath(), userId);
        try {
            ArrayList<JSONParser> jsons = BINodeValueParser.string2BIJson(nodeValue.getTaskContent(), nodeValue.getTaskName());
//            LOG.info("当前worker处理的基础索引数量为：" + jsons.size());
            HashSet<BITableRelation> relations = new HashSet<BITableRelation>();
            Iterator<JSONParser> it = jsons.iterator();
            while (it.hasNext()) {
                relations.add((BITableRelation) it.next());
            }
//            operation.process(relations);
        } catch (Exception ex) {
             BILogger.getLogger().error(ex.getMessage(), ex);
        } finally {
            nodeValue.setStatus(BIWorkerNodeValue.STATUS_FINISH);
        }
        return nodeValue;
    }
}