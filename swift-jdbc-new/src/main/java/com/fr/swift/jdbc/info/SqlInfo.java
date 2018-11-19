package com.fr.swift.jdbc.info;

/**
 * 预解析后的sql信息
 * TODO 实现 将sql转成sqlInfo，sqlInfo包括原sql，用户authCode，查询类型，where，order等条件
 * @see com.fr.swift.jdbc.factory.JsonRequestBuilder
 * @author yee
 * @date 2018/11/16
 */
public interface SqlInfo {
    String sql();

    String authCode();
}
