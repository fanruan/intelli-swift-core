package com.fr.bi.cluster.zookeeper;

import com.fr.base.FRContext;
import com.fr.bi.cluster.BIMissionManager;
import com.fr.bi.cluster.zookeeper.watcher.BIMaster;
import com.fr.bi.cluster.zookeeper.watcher.BIWorker;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.FRLogger;
import com.fr.json.JSONCreator;
import org.apache.zookeeper.KeeperException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Connery on 2015/3/30.
 * 分布式cube生成的实现逻辑：
 * master节点会将读取数据库数据、生成索引等步骤，所需数据
 * 分别放到worker节点下。
 * worker节点会执行当前任务，并且改变任务状态。
 * monitor负责监视所有任务完成情况。任务完成会促发任务完成的观察者。
 * <p/>
 * 如果要添加一个新的任务
 * 首先第一步，需要分发的对象实现BIJsonInterface 接口。
 * 然后需要分别实现一个，master节点分发任务的方法，和worker节点完成相应工作的方法。
 * 给该任务一个唯一的taskname，在BIWorkerNodeValue下。
 * 此外，还需实现一个BIWorkerOperation和BIMasterOperation接口，分别是用于处理当前任务。
 * 最后，分别在BIWorker和BIMaster下将新添加的方法。
 */
public class BIClusterMissionManager implements BIMissionManager {
    private static FRLogger LOG = FRContext.getLogger();
    private BIWorker worker;
    private BIMaster master;
//    private static final Logger LOG = LoggerFactory.getLogger(BIClusterMissionManager.class);

    @Override
    public void prepareMissions(int taskName, HashSet<JSONCreator> ingredient) {
        worker = (BIWorker) ZooKeeperManager.getInstance().getWatcher(ZooKeeperManager.WORKER);
        master = (BIMaster) ZooKeeperManager.getInstance().getWatcher(ZooKeeperManager.MASTER);
        if (master.isMaster()) {
            try {
                int size = worker.getWorkers().size();
//                LOG.info("当前工作的Worker数量为：" + size);
                BIMasterController controller = master.getController();
                ArrayList<BIWorkerNodeValue> missions = generateNodeValue(taskName, ingredient, size);
                controller.addMission(missions);
            } catch (KeeperException ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);

            } catch (InterruptedException ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            } catch (Exception ex) {
                 BILogger.getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void startBuildCube(String basePath, String tmpPath, long userId) {
//        LOG.info("采用分布式方法，构建CUBE数据");

        master = (BIMaster) ZooKeeperManager.getInstance().getWatcher(ZooKeeperManager.MASTER);
        master.startBuildCube(basePath, tmpPath, userId);
    }

    public void init(BIWorker worker, BIMaster master) {
        this.worker = worker;
        this.master = master;
    }

    private ArrayList<BIWorkerNodeValue> generateNodeValue(int taskName, HashSet<JSONCreator> ingredient, int workerAmount) throws Exception {
        Iterator<String> it = generateMissionContent(ingredient, workerAmount).iterator();
        ArrayList<BIWorkerNodeValue> result = new ArrayList<BIWorkerNodeValue>();
        while (it.hasNext()) {
            String content = it.next();
            BIWorkerNodeValue value = new BIWorkerNodeValue();
            value.setDefault();
            value.setTaskName(taskName);
            value.setTaskContent(content);
            result.add(value);
        }
        return result;
    }

    private ArrayList<String> generateMissionContent(HashSet<JSONCreator> ingredient, int workerAmount) throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        if (workerAmount != 0) {
            int sumMission = ingredient.size();
            int averageMission = sumMission / workerAmount;
            ArrayList<JSONCreator> listIngredient = new ArrayList<JSONCreator>();

            Iterator<JSONCreator> it = ingredient.iterator();
            while (it.hasNext()) {
                listIngredient.add(it.next());
            }
            int position = 0;
            while (workerAmount > 0) {
                if (workerAmount != 1) {
                    result.add(contentSwitch(listIngredient.subList(position, position + averageMission)));
                } else {
                    result.add(contentSwitch(listIngredient.subList(position, listIngredient.size())));
                }
                position = position + averageMission;
                workerAmount--;
            }
        }
        return result;

    }

    private String contentSwitch(List<JSONCreator> list) throws Exception {
        Iterator<JSONCreator> it = list.iterator();
        List<JSONCreator> tableKeys = new ArrayList<JSONCreator>();
        while (it.hasNext()) {
            tableKeys.add(it.next());
        }
        return BINodeValueParser.BIJson2String(tableKeys);
    }


}