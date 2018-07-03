package com.fr.swift.generate.conf;

/**
 * @author anchore
 * @date 2018/7/2
 */
public interface ColumnIndexingConf extends IndexingConf {
    String getColumn();

    boolean requireIndex();

    boolean requireGlobalDict();
}