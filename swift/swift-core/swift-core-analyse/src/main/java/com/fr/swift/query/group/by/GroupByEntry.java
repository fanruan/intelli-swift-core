package com.fr.swift.query.group.by;


import com.fr.swift.structure.iterator.RowTraversal;

/**
 * @author pony
 * @date 2017/12/6
 * 这个不直接保存索引，是因为访问迭代器是单线程的，而使用迭代器是其他的多线程的，这么做可以通过多线程来getbitmapindex提高效率
 */
public interface GroupByEntry {
    int getIndex();

    RowTraversal getTraversal();
}
