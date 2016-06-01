package com.fr.bi.cal.generate;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.oper.observer.BICubeFinishObserver;
import com.finebi.cube.impl.message.BIMessage;
import com.finebi.cube.impl.message.BIMessageTopic;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.message.IMessageTopic;
import com.finebi.cube.structure.BICube;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.util.concurrent.Future;

/**
 * This class created on 2016/4/19.
 *
 * @author Connery
 * @since 4.0
 */
public class StopCubeTask implements CubeTask {

    private CubeBuildStuff cubeBuildStuffManager;
    private BIUser biUser;
    protected ICubeResourceRetrievalService retrievalService;
    protected ICubeConfiguration cubeConfiguration;
    protected BICube cube;
    private BICubeFinishObserver<Future<String>> finishObserver;

    public StopCubeTask(BIUser biUser) {
        this.biUser = biUser;
        cubeConfiguration = BICubeConfiguration.getConf(Long.toString(biUser.getUserId()));
        retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        cube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
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
//            BIFileUtils.moveFile(BICubeConfiguration.getTempConf(Long.toString(biUser.getUserId())).getRootURI().getPath(),
//                    BICubeConfiguration.getConf(Long.toString(biUser.getUserId())).getRootURI().getPath());
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void run() {

    }



    public static IMessage generateMessageStopDataSourceStart() {
        return buildTopic(new BIMessageTopic(BICubeBuildTopicTag.START_BUILD_CUBE));
    }

    private static IMessage buildTopic(IMessageTopic topic) {
//        BIStatusUtils.generateStopFromFinish(BICubeBuildTopicTag.START_BUILD_CUBE);
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
