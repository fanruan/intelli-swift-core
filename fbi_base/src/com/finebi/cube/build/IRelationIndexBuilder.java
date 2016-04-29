package com.finebi.cube.build;

/**
 * 构建表之间的直接关联索引。
 * 例如a->b,b->c。那么构建a->b和b->c的关联索引。
 * 而a->c的间接索引不在此类中构建。
 *
 * Created by Connery on 2016/2/26.
 */
public interface IRelationIndexBuilder {
}
