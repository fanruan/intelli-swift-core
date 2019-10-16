package com.fr.swift.config;

import com.fr.swift.source.alloter.AllotRule;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface TableAllotConf extends IndexingConf {
    AllotRule getAllotRule();
}