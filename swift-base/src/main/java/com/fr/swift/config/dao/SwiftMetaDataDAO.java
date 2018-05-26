package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.entity.SwiftMetaDataEntity;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftMetaDataDAO extends SwiftConfigDAO<SwiftMetaDataEntity> {
    /**
     * 根据SourceKey查找
     *
     * @param sourceKey
     * @return
     */
    SwiftMetaDataBean findBySourceKey(String sourceKey) throws SQLException;

    /**
     * 根据表名查找
     * @param tableName
     * @return
     */
    SwiftMetaDataBean findByTableName(String tableName) throws SQLException;

    /**
     * 保存SwiftMetaDataBean
     * @param metaDataBean
     * @return
     */
    boolean addOrUpdateSwiftMetaData(SwiftMetaDataBean metaDataBean) throws SQLException;

    /**
     * 根据SourceKey删除
     * @param sourceKey
     * @return
     */
    boolean deleteSwiftMetaDataBean(String sourceKey) throws SQLException;

    /**
     * 获取所有SwiftMetaDataBean
     * @return
     */
    List<SwiftMetaDataBean> findAll();
}
