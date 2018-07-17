package com.fr.swift.config.indexing;

import com.fr.swift.source.alloter.AllotRule;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface TableIndexingConf extends IndexingConf {
    AllotRule getAllotRule();
}