package com.fr.swift.config.dao;

import com.fr.swift.base.meta.SwiftMetaDataEntity;
import com.fr.swift.config.oper.ConfigSession;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftMetaDataDao extends SwiftConfigDao<SwiftMetaDataEntity> {
    /**
     * 根据SourceKey查找
     *
     * @param sourceKey
     * @return
     */
    SwiftMetaDataEntity findBySourceKey(ConfigSession session, String sourceKey) throws SQLException;

    /**
     * 根据表名查找
     *
     * @param tableName
     * @return
     */
    SwiftMetaDataEntity findByTableName(ConfigSession session, String tableName) throws SQLException;

    /**
     * 保存SwiftMetaDataBean
     *
     * @param metaDataBean
     * @return
     */
    boolean addOrUpdateSwiftMetaData(ConfigSession session, SwiftMetaDataEntity metaDataBean) throws SQLException;

    /**
     * 根据SourceKey删除
     *
     * @param sourceKey
     * @return
     */
    boolean deleteSwiftMetaDataBean(ConfigSession session, String sourceKey) throws SQLException;

    /**
     * 获取所有SwiftMetaDataBean
     *
     * @return
     */
    List<SwiftMetaDataEntity> findAll(ConfigSession session);

    /**
     * 模糊查詢
     *
     * @param session
     * @param fuzzyName
     * @return
     */
    List<SwiftMetaDataEntity> fuzzyFind(ConfigSession session, String fuzzyName);
}
