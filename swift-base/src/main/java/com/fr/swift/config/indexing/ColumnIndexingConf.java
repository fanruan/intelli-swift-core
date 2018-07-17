package com.fr.swift.config.indexing;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface ColumnIndexingConf extends IndexingConf {
    String getColumn();

    boolean requireIndex();

    boolean requireGlobalDict();
}