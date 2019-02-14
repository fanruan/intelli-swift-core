package com.fr.swift.db;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.Source;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/3/26
 * <p>
 * todo 讨论：这个接口是否太顶层了，比如导入，查询都要依赖底层，考虑去除？
 */
public interface Table extends Source, DataSource {
    /**
     * 元数据
     *
     * @return meta
     * @throws SQLException 异常
     */
    SwiftMetaData getMeta() throws SQLException;
}