package com.fr.swift.service.handler.history;

import com.fr.swift.service.SwiftHistoryService;
import com.fr.third.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

/**
 * @author yee
 * @date 2018/6/8
 */
@Service
public class HistoryDataSyncManager {
    public <S extends Serializable> S handle(List<SwiftHistoryService> historyServices) {
        return null;
    }

}
