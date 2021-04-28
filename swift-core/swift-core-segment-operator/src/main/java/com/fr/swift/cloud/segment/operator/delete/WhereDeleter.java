package com.fr.swift.cloud.segment.operator.delete;

import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.segment.operator.Deleter;

/**
 * @author anchore
 * @date 2018/6/19
 * <p>
 * 按明细值删
 */
public interface WhereDeleter extends Deleter {
    boolean delete(Where where) throws Exception;
}