package com.fr.swift.cloud.executor.task.info.interval;

import com.fr.swift.cloud.config.entity.SwiftNodeInfo;
import com.fr.swift.cloud.source.alloter.impl.hash.function.HashType;

import java.util.List;

/**
 * @author Heng.J
 * @date 2020/11/12
 * @description 迁移配置 beginIndex、endIndex
 * @since swift-1.2.0
 */
public interface MigInterval {

    static MigInterval getMigrateInterval(SwiftNodeInfo nodeInfo) {
        if (HashType.APPID_YEARMONTH.equals(nodeInfo.getRelatedHashType())) {
            return new MonthMigInterval(nodeInfo.getBeginIndex(), nodeInfo.getEndIndex(), nodeInfo.getLimitNum());
        } else {
            // for other hash function
            return null;
        }
    }

    String getBeginIndex();

    String getEndIndex();

    void addOnePeriod();

    boolean containsIndex(String index);

    List<String> getIndexCoverRange();

    List<String> getPreMigIndex();
}
