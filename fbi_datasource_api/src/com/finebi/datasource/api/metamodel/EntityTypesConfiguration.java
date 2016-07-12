package com.finebi.datasource.api.metamodel;

import java.util.Set;

/**
 * manager的配置数据，指定生成的EntityTypeManager
 * 对象包含的信息
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface EntityTypesConfiguration {

    /**
     * 需要包含的EntityType的名字
     *
     * @return 包含EntityType的名字的集合
     */
    Set<String> allEntityTypeNames();
}
