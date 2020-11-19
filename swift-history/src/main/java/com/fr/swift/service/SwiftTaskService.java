package com.fr.swift.service;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.executor.task.bean.PlanningBean;
import com.fr.swift.executor.task.info.PlanningInfo;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.service.info.TaskInfo;

/**
 * @author Heng.J
 * @date 2020/11/13
 * @description
 * @since swift-1.2.0
 */
@SwiftService(name = "swiftTaskService")
@SwiftBean(name = "swiftTaskService")
public class SwiftTaskService extends AbstractSwiftService implements TaskService {

    private static final long serialVersionUID = -5089910046955955074L;

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        return true;
    }

    @Override
    public void distributeTask(TaskInfo taskInfo, String target) {
        ServiceContext serviceContext = ProxySelector.getProxy(ServiceContext.class);
        boolean success = false;
        try {
            success = serviceContext.dispatch(JsonBuilder.writeJsonString(new PlanningBean((PlanningInfo) taskInfo)), target);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        } finally {
            if (!success) {
                // 其他异常暂不处理 数据库任务失败 cause记录
            }
        }
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DISTRIBUTE;
    }
}
