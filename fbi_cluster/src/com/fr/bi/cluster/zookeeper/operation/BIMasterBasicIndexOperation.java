package com.fr.bi.cluster.zookeeper.operation;

/**
 * Created by Connery on 2015/4/3.
 */
public class BIMasterBasicIndexOperation implements BIMasterOperation {
    @Override
    public void prepare(String basePath, String tmpPath, long userId) {
//        Set<Table> generateTable;
//        HashSet<JSONCreator> generateTableObject = new HashSet<JSONCreator>();
//        if (ClusterEnv.isCluster()) {
//            CubeBuildBasicIndexOperation operation = new CubeBuildBasicIndexOperation(basePath, tmpPath, userId);
//            generateTable = (Set<Table>) operation.getData();
////            System.out.println("Current user id is：" + userId);
//            BILogger.getLogger().info("Current user id is：" + userId);
//            Iterator<Table> it = generateTable.iterator();
//            while (it.hasNext()) {
////                generateTableObject.add(it.next());
//            }
//            ClusterAdapter.getManager().getMissionManager().prepareMissions(BIWorkerNodeValue.TASK_BASIC_INDEX, generateTableObject);
//
//        }
    }
}