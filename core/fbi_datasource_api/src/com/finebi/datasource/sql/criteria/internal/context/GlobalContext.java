package com.finebi.datasource.sql.criteria.internal.context;

import com.finebi.datasource.api.metamodel.Metamodel;

/**
 * 全局的上下文环境。是线程安全的。
 * 保存着全部的元数据，配置数据。
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface GlobalContext {

    Metamodel getMetamodel();

}
