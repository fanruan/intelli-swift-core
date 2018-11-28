package com.fr.swift.config.bean;


import java.util.List;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface IndexingSelectRule {
    ServerCurrentStatus select(List<ServerCurrentStatus> statuses) throws Exception;

}
