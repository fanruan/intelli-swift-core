package com.fr.bi.cluster.zookeeper.operation;


import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import com.fr.json.JSONCreator;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Connery on 2015/4/2.
 */
public class BIMasterFirstIndexOperation implements BIMasterOperation {
    @Override
    public void prepare(String basePath, String tmpPath, long userId) {
        HashSet<BITableRelation> relations = null;
        HashSet<JSONCreator> generateTableObject = new HashSet<JSONCreator>();
        if (ClusterEnv.isCluster()) {
            System.out.println("Current user id is：" + userId);
            Iterator<BITableRelation> it = relations.iterator();
            while (it.hasNext()) {
                generateTableObject.add(it.next());
            }
            ClusterAdapter.getManager().getMissionManager().prepareMissions(BIWorkerNodeValue.TASK_BASIC_INDEX, generateTableObject);

        }
    }
}