package com.fr.swift.config;


import com.fr.swift.config.bean.ServerCurrentStatus;

import java.util.List;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface IndexingSelectRule {
    ServerCurrentStatus select(List<ServerCurrentStatus> statuses) throws Exception;

}
