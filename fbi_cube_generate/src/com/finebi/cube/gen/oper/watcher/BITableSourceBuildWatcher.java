package com.finebi.cube.gen.oper.watcher;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.CubeTableEntityService;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableSourceBuildWatcher extends BICubeBuildWatcher {
    private CubeTableEntityService tableEntityService;

    public BITableSourceBuildWatcher(CubeTableEntityService tableEntityService) {
        this.tableEntityService = tableEntityService;
    }

    @Override
    public void process(IMessage lastReceiveMessage) {
        try {
            if (lastReceiveMessage.isStopStatus()) {
                messagePublish.publicStopMessage(generateStopBody(""));
            } else {
                messagePublish.publicFinishMessage(generateFinishBody(""));
                tableEntityService.recordLastTime();
                tableEntityService.clear();
            }
        } catch (BIDeliverFailureException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
