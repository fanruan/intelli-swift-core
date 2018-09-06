package com.fr.swift.config.bean;


import java.util.Set;

/**
 * @author yee
 * @date 2018/6/11
 */
public interface IndexingSelectRule {
    String select(Set<String> indexingServices) throws Exception;

}
