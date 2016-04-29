package com.fr.bi.cluster.zookeeper.operation;

import com.fr.bi.cal.generate.CubeBuildFirstIndexOperation;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import com.fr.bi.stable.relation.BITableRelation;
import com.fr.json.JSONCreator;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Connery on 2015/4/2.
 */
public class BIMasterFirstIndexOperation implements BIMasterOperation {
    @Override
    public void prepare(String basePath, String tmpPath, long userId) {
        HashSet<BITableRelation> relations;
        HashSet<JSONCreator> generateTableObject = new HashSet<JSONCreator>();
        if (ClusterEnv.isCluster()) {
            CubeBuildFirstIndexOperation operation = new CubeBuildFirstIndexOperation(basePath, tmpPath, userId);
            relations = (HashSet<BITableRelation>) operation.getData();
            System.out.println("Current user id is：" + userId);
            Iterator<BITableRelation> it = relations.iterator();
            while (it.hasNext()) {
                generateTableObject.add(it.next());
            }
            ClusterAdapter.getManager().getMissionManager().prepareMissions(BIWorkerNodeValue.TASK_BASIC_INDEX, generateTableObject);

        }
    }
}