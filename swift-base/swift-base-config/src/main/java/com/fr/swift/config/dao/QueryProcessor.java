package com.fr.swift.config.dao;

import org.hibernate.query.Query;

/**
 * @author anchore
 * @date 2019/12/30
 */
public interface QueryProcessor {
    void process(Query query);
}