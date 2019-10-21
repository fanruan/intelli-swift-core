package com.fr.swift.config.dao;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.oper.ConfigSession;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftMetaDataDao extends SwiftConfigDao<SwiftMetaDataBean> {
    /**
     * 根据SourceKey查找
     *
     * @param sourceKey
     * @return
     */
    SwiftMetaDataBean findBySourceKey(ConfigSession session, String sourceKey) throws SQLException;

    /**
     * 根据表名查找
     *
     * @param tableName
     * @return
     */
    SwiftMetaDataBean findByTableName(ConfigSession session, String tableName) throws SQLException;

    /**
     * 保存SwiftMetaDataBean
     *
     * @param metaDataBean
     * @return
     */
    boolean addOrUpdateSwiftMetaData(ConfigSession session, SwiftMetaDataBean metaDataBean) throws SQLException;

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
    List<SwiftMetaDataBean> findAll(ConfigSession session);

    /**
     * 模糊查詢
     *
     * @param session
     * @param fuzzyName
     * @return
     */
    List<SwiftMetaDataBean> fuzzyFind(ConfigSession session, String fuzzyName);
}
