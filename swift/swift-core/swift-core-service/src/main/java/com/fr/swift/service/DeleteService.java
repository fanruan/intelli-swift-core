package com.fr.swift.service;

import com.fr.swift.db.Where;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/11/8
 */
interface DeleteService {

    boolean delete(SourceKey sourceKey, Where where, List<String> segKeys) throws Exception;

    void truncate(SourceKey sourceKey);
}