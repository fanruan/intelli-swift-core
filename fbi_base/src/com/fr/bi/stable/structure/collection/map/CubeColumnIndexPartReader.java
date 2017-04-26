package com.fr.bi.stable.structure.collection.map;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2017/4/12.
 */
public class CubeColumnIndexPartReader implements ICubeColumnIndexReader {
    private ICubeColumnIndexReader reader;
    private int size;

    public CubeColumnIndexPartReader(ICubeColumnIndexReader reader, int size) {
        this.reader = reader;
        this.size = Math.min(size, reader.sizeOfGroup());
    }

    @Override
    public GroupValueIndex[] getGroupIndex(Object[] groupValues) {
        return reader.getGroupIndex(groupValues);
    }

    @Override
    public GroupValueIndex getIndex(Object groupValue) {
        return reader.getIndex(groupValue);
    }

    @Override
    public Object firstKey() {
        return reader.firstKey();
    }

    @Override
    public Object lastKey() {
        return reader.lastKey();
    }

    @Override
    public Iterator<Map.Entry> iterator() {
        return new SizeIterator(reader.iterator());
    }

    @Override
    public Iterator<Map.Entry> previousIterator() {
        return new SizeIterator(reader.previousIterator());
    }

    @Override
    public Iterator<Map.Entry> iterator(Object start) {
        return new SizeIterator(reader.iterator(start));
    }

    @Override
    public Iterator<Map.Entry> previousIterator(Object start) {
        return new SizeIterator(reader.previousIterator(start));
    }

    private class SizeIterator implements Iterator<Map.Entry>{
        private Iterator<Map.Entry> iterator;
        private int index = 0;
        public SizeIterator(Iterator<Map.Entry> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Map.Entry next() {
            index++;
            return iterator.next();
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

    @Override
    public long nonPrecisionSize() {
        return reader.nonPrecisionSize();
    }

    @Override
    public Object[] createKey(int length) {
        return reader.createKey(length);
    }

    @Override
    public Object createValue(Object v) {
        return null;
    }

    @Override
    public Object getGroupValue(int position) {
        return reader.getGroupValue(position);
    }

    @Override
    public GroupValueIndex getGroupValueIndex(int groupValuePosition) {
        return reader.getGroupValueIndex(groupValuePosition);
    }

    @Override
    public Object getOriginalValue(int rowNumber) {
        return reader.getOriginalValue(rowNumber);
    }

    @Override
    public int sizeOfGroup() {
        return size;
    }

    @Override
    public GroupValueIndex getNULLIndex() {
        return reader.getNULLIndex();
    }
}
