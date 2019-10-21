package com.fr.swift.segment.column.impl.multi;

import com.fr.swift.query.aggregator.Combiner;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.multi.DictAdapter.Elem;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author anchore
 * @date 2019/7/16
 */
class DictAdapter<T> extends AbstractList<Elem<T>> {
    private DictionaryEncodedColumn<T> dict;
    private int nthDict;

    DictAdapter(DictionaryEncodedColumn<T> dict, int nthDict) {
        this.dict = dict;
        this.nthDict = nthDict;
    }

    @Override
    public Elem<T> get(int index) {
        return new Elem<T>(dict.getValue(index + 1), nthDict, index + 1);
    }

    @Override
    public int size() {
        return dict.size() - 1;
    }

    static class Elem<T> {
        T val;
        List<Integer> nthDicts = new ArrayList<Integer>();
        List<Integer> localIndices = new ArrayList<Integer>();

        Elem(T val, int nthDict, int localIndex) {
            this.val = val;
            this.nthDicts.add(nthDict);
            this.localIndices.add(localIndex);
        }
    }

    static class ElemComparator<T> implements Comparator<Elem<T>> {
        Comparator<T> c;

        ElemComparator(Comparator<T> c) {
            this.c = c;
        }

        @Override
        public int compare(Elem<T> o1, Elem<T> o2) {
            return c.compare(o1.val, o2.val);
        }
    }

    static class ElemCombiner<T> implements Combiner<Elem<T>> {
        @Override
        public void combine(Elem<T> current, Elem<T> other) {
            current.nthDicts.addAll(other.nthDicts);
            current.localIndices.addAll(other.localIndices);
        }
    }
}