package com.fr.swift.result;

import com.fr.swift.source.Row;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Lyon
 * @date 2018/6/13
 */
public interface SwiftRowOperator<T extends Row> {

    /**
     * TODO: 2018/6/13 这边的相关接口待调整，因为配置计算那块都是依赖node结构处理的
     */
    List<T> operate(SwiftNode... node) throws SQLException;
}
