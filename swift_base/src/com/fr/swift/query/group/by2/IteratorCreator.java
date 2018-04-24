package com.fr.swift.query.group.by2;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/23.
 */
interface IteratorCreator<ENTRY> {

    Iterator<ENTRY> createIterator(int stackSize, ENTRY entry);
}
