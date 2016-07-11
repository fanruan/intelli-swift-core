package com.finebi.analysis.api.criteria;

import java.util.Set;

/**
 * 用于生产EntityTypeManager
 * 其中包含有需要的entityType名字
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface PlainTable {
    Set<String> getEntityName();
}
