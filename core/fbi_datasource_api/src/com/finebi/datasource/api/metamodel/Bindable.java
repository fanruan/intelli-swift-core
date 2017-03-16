
package com.finebi.datasource.api.metamodel;

/**
 * 代表当前对象是可以绑定到Path的
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface Bindable<T> {

    public static enum BindableType {

        /**
         * 单属性
         */
        SINGULAR_ATTRIBUTE,

        /**
         * 多值属性,要去
         */
        PLURAL_ATTRIBUTE,

        /**
         * 实体属性
         */
        ENTITY_TYPE
    }

    /**
     * 返回邦定类型
     *
     * @return 绑定类型
     */
    BindableType getBindableType();

    /**
     * 返回当前对象的类型
     *
     * @return Java类型
     */
    Class<T> getBindableJavaType();
}
