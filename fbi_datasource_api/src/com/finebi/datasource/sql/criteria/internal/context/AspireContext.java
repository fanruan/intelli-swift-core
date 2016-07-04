
package com.finebi.datasource.sql.criteria.internal.context;


import com.finebi.datasource.sql.criteria.internal.important.MetamodelExpander;
import com.finebi.datasource.sql.criteria.internal.render.RenderFactory;

/**
 * 中间层上下文。有以下几点作用
 * 1.基本元数据
 * 2.配置信息
 * <p/>
 * Context被实例化后，Context是不可变的。虽然Context不是线程安全的对象，
 * 但是使用者是可以多线程访问，
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface AspireContext {


    MetamodelExpander getMetamodel();

    RenderFactory getRenderFactory();
}
