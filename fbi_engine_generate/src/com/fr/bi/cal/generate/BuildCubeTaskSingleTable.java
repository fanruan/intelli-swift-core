package com.fr.bi.cal.generate;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.gen.arrange.BICubeBuildTopicManager;
import com.finebi.cube.gen.arrange.BICubeOperationManager;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.impl.message.BIMessage;
import com.finebi.cube.impl.message.BIMessageTopic;
import com.finebi.cube.impl.operate.BIOperationID;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageTopic;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.engine.CubeBuildStuffManager;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.relation.BITableSourceRelationPath;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Created by wuk on 16/5/26.
 * 单表更新
 */
public class BuildCubeTaskSingleTable implements CubeTask {

    private CubeBuildStuffManager cubeBuildStuffManager;
    private BIUser biUser;
    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    protected BICube cube;
    private BICubeFinishObserver<Future<String>> finishObserver;
    private BITable biTable;

    public BuildCubeTaskSingleTable(BIUser biUser, BITable biTable) {
        this.biUser = biUser;
        this.biTable = biTable;
        init();

    }

    private void init() {
        this.cubeBuildStuffManager = new CubeBuildStuffManager(biUser);
       this.cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
       this.retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
       this.cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        this.cubeBuildStuffManager.initialCubeStuff();
    }
    
    @Override
    public String getUUID() {
        return "BUILD_CUBE";
    }

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.BUILD;
    }

    @Override
    public void start() {

    }

    @Override
    public void end() {
        Future<String> result = finishObserver.getOperationResult();
        try {
            BILogger.getLogger().info(result.get());
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void run() {
        BICubeBuildTopicManager manager = new BICubeBuildTopicManager();
        BICubeOperationManager operationManager = new BICubeOperationManager(cube, cubeBuildStuffManager.getSources());
        
        operationManager.initialWatcher();
        ETLTableSource tableSource = (ETLTableSource) BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(biTable.getID(), biUser);
        if(ComparatorUtils.equals(null,tableSource)){
            return;
        }
        Set<ITableSource> currentTableSet = new HashSet<ITableSource>();
        currentTableSet.add(tableSource);
        
        Set<List<Set<ITableSource>>> depends = cubeBuildStuffManager.calculateTableSource(currentTableSet);
        cubeBuildStuffManager.setDependTableResource(cubeBuildStuffManager.calculateTableSource(currentTableSet));
        Set<BITableSourceRelationPath> relationPathSet = filterPath(cubeBuildStuffManager.getRelationPaths());
        
        manager.registerDataSource(currentTableSet);
        manager.registerRelation(cubeBuildStuffManager.getTableSourceRelationSet());
        manager.registerTableRelationPath(relationPathSet);
        finishObserver = new BICubeFinishObserver<Future<String>>(new BIOperationID("FINEBI_E_SINGLE"));
        operationManager.generateDataSource(depends);
        operationManager.generateRelationBuilder(cubeBuildStuffManager.getTableSourceRelationSet());
        operationManager.generateTableRelationPath(relationPathSet);
        IRouter router = BIFactoryHelper.getObject(IRouter.class);
        try {
            router.deliverMessage(generateMessageDataSourceStart());
        } catch (BIDeliverFailureException e) {
            throw BINonValueUtils.beyondControl(e);
        }

    }

    private Set<BITableSourceRelationPath> filterPath(Set<BITableSourceRelationPath> paths) {
        Iterator<BITableSourceRelationPath> iterator = paths.iterator();
        Set<BITableSourceRelationPath> result = new HashSet<BITableSourceRelationPath>();
        while (iterator.hasNext()) {
            BITableSourceRelationPath path = iterator.next();
            if (path.getAllRelations().size() > 1) {
                result.add(path);
            }
        }
        return result;
    }


    public static IMessage generateMessageDataSourceStart() {
        return buildTopic(new BIMessageTopic(BICubeBuildTopicTag.START_BUILD_CUBE));

    }

    private static IMessage buildTopic(IMessageTopic topic) {
        return new BIMessage(topic, null, null, null);
    }

    @Override
    public long getUserId() {
        return -999;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }
}
