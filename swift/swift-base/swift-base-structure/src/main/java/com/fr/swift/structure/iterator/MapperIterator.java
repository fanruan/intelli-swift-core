package com.fr.swift.structure.iterator;

import com.fr.swift.util.function.Function;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/3.
 */
public class MapperIterator<Param, Return> implements Iterator<Return> {

    private Iterator<Param> iterator;
    private Function<Param, Return> fn;

    public MapperIterator(Iterator<Param> iterator, Function<Param, Return> fn) {
        this.iterator = iterator;
        this.fn = fn;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Return next() {
        return fn.apply(iterator.next());
    }

    @Override
    public void remove() {

    }
}
