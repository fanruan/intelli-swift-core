package com.fr.swift.service.handler.history;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.event.history.HistoryLoadRpcEvent;
import com.fr.swift.service.HistoryService;
import com.fr.swift.service.handler.base.Handler;
import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class HistoryDataSyncManager implements Handler<HistoryLoadRpcEvent> {

    @Autowired
    private SwiftServiceInfoService serviceInfoService;

    public <S extends Serializable> S handle(HistoryLoadRpcEvent event) {
        // TODO 获取historyService代理
        Map<String, HistoryService> historyServiceMap = new HashMap<String, HistoryService>();
        List<SwiftServiceInfoBean> infoList = serviceInfoService.getServiceInfoByService("SEGMENT");
        return null;
    }

}
