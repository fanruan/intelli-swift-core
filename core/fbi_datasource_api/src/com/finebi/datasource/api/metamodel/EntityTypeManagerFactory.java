package com.finebi.datasource.api.metamodel;

/**
 * 访问全局上下文交互的接口。
 * EntityTypeManager的工厂类。依据配置信息生成EntityTypeManager
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface EntityTypeManagerFactory {

    /**
     * 创建一个单次的查询计算的EntityTypeManager
     * <p/>
     * 默认获得一个包含全部全局的EntityType等信息的
     * EntityTypeManager
     *
     * @return EntityTypeManager
     */
    EntityTypeManager createEntityManager();

    /**
     * 创建一个单次的查询计算的EntityTypeManager
     * <p/>
     * 默认获得一个包含全部EntityType等信息的
     * EntityTypeManager
     *
     * @return EntityTypeManager
     * @since Advanced FineBI Analysis 1.0
     */
    EntityTypeManager createEntityManager(EntityTypesConfiguration configure);


    /**
     * 全局中添加一个全新的entityType
     *
     * @param entityType 全新的entityType
     * @since Advanced FineBI Analysis 1.0
     */
    void registerEntityType(EntityType entityType);


}