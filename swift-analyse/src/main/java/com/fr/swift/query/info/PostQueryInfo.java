package com.fr.swift.query.info;

import com.fr.swift.query.info.target.GroupTarget;

import java.util.List;

/**
 * Created by Lyon on 2018/5/29.
 * 对明细聚合结果的二维表进行处理
 */
public interface PostQueryInfo {

    List<GroupTarget> getCalInfo();
}
