package com.fr.bi.cluster.zookeeper.operation;


import com.fr.bi.cal.generate.CubeBuildBasicDBOperation;
import com.fr.bi.cluster.ClusterAdapter;
import com.fr.bi.cluster.utils.ClusterEnv;
import com.fr.bi.cluster.zookeeper.BIWorkerNodeValue;
import com.fr.bi.stable.data.Table;
import com.fr.json.JSONCreator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Connery on 2015/4/2.
 */
public class BIMasterDBOperation implements BIMasterOperation {
    @Override
    public void prepare(String basePath, String tmpPath, long userId) {
        Map<Integer, Set<Table>> generateTable;
        HashSet<JSONCreator> generateTableObject = new HashSet<JSONCreator>();
        if (ClusterEnv.isCluster()) {
            CubeBuildBasicDBOperation operation = new CubeBuildBasicDBOperation(basePath, tmpPath, userId);
            generateTable = (Map<Integer, Set<Table>>) operation.getData();
//            System.out.println("Current user id is：" + userId);

            for (Map.Entry<Integer, Set<Table>> entry : generateTable.entrySet()) {
                for (Table key : entry.getValue()) {
//                    generateTableObject.add(key);
                }
            }
            ClusterAdapter.getManager().getMissionManager().prepareMissions(BIWorkerNodeValue.TASK_LOAD_DB, generateTableObject);

        }
    }
}