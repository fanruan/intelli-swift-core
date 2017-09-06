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

    private GroupValueIndex filterGvi;

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

    private class SizeIterator implements Iterator<Map.Entry> {

        private Iterator<Map.Entry> iterator;

        private int index = 0;

        private Map.Entry next;

        private Map.Entry current;

        public SizeIterator(Iterator<Map.Entry> iterator) {

            this.iterator = iterator;
            moveToNext();
        }

        @Override
        public boolean hasNext() {

            return next != null;
        }

        @Override
        public Map.Entry next() {

            current = next;
            moveToNext();
            return current;
        }

        private void moveToNext() {

            Map.Entry<Object, GroupValueIndex> entry = null;


            if (filterGvi != null && !filterGvi.isAllEmpty()) {
                while (index < size) {
                    if (entry != null && (entry.getValue().AND(filterGvi)).getRowsCountWithData() > 0) {
                        index++;
                        next = entry;
                        return;
                    } else if (iterator.hasNext()) {
                        entry = iterator.next();
                    } else {
                        next = null;
                        return;
                    }
                }
                next = null;
            } else {
                if (index < size) {
                    index++;
                    next = iterator.next();
                } else {
                    next = null;
                }
            }
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

    public void applyFilter(GroupValueIndex gvi) {
        //pony这个不需要，使用部分字段结果为空也没关系，加了这个如果维度很多的话会非常非常卡
       // filterGvi = gvi;
    }
}
