package com.fr.swift.config.dao;


import org.hibernate.Criteria;

/**
 * @author anchore
 * @date 2019/12/28
 */
public interface CriteriaProcessor {
    void process(Criteria criteria);
}