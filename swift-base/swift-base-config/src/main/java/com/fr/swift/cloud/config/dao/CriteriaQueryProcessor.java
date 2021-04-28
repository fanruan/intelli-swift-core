package com.fr.swift.cloud.config.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/5/27
 */
public interface CriteriaQueryProcessor {
    void process(CriteriaQuery query, CriteriaBuilder builder, Root from);
}
