package com.fr.swift.api.rpc;

import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.service.ServiceType;
import com.fr.swift.source.SwiftMetaData;

import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/8/23
 */
public interface DetectService {
    /**
     * 获取Analyse和RealTime地址信息
     *
     * @return
     */
    Map<ServiceType, List<String>> detectiveAnalyseAndRealTime(String defaultAddress);

    /**
     * 获取metadata
     *
     * @param tableName
     * @return
     */
    SwiftMetaData detectiveMetaData(String tableName) throws SwiftMetaDataAbsentException;
}
