package com.fr.swift.source.alloter;


import com.fr.swift.cluster.ClusterEntity;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface IndexingNodeSelectRule {
    /**
     * 选择要导入的机器
     *
     * @param entities 所有机器
     * @return 选中的机器
     * @throws Exception
     */
    List<ClusterEntity> select(List<ClusterEntity> entities) throws Exception;
}
