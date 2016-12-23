package com.fr.bi.stable.structure.collection;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeValueEntryGetter;
import com.fr.bi.stable.engine.cal.DimensionIteratorCreator;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by 小灰灰 on 2014/8/13.
 */
public class CubeIndexGetterWithNullValue implements ICubeColumnIndexReader {
    private ICubeColumnIndexReader reader;
    private ICubeValueEntryGetter getter;
    private GroupValueIndex nullIndex;

    public CubeIndexGetterWithNullValue(ICubeColumnIndexReader reader, ICubeValueEntryGetter getter, GroupValueIndex nullIndex) {
        this.reader = reader;
        this.getter = getter;
        this.nullIndex = nullIndex;
    }

    @Override
    public int sizeOfGroup() {
        return reader.sizeOfGroup() + 1;
    }

    public GroupValueIndex getIndex (Object groupValues) {
        if(groupValues == null) {
            return nullIndex;
        } else {
            return reader.getIndex(groupValues);
        }
    }

    @Override
    public GroupValueIndex[] getGroupIndex(Object[] groupValues) {
        if(groupValues == null) {
            return new GroupValueIndex[0];
        }
        GroupValueIndex[] gvi = new GroupValueIndex[groupValues.length];
        for(int i = 0; i < gvi.length; i++) {
            gvi[i] = getIndex(groupValues[i]);
        }
        return gvi;
    }
   
    /**
     * key数组
     *
     * @param length 长度
     * @return key 数组
     */
    @Override
    public Object[] createKey(int length) {
        return reader.createKey(length);
    }

    @Override
    public GroupValueIndex getNULLIndex() {
        return nullIndex;
    }

    @Override
    public Object createValue(Object v) {
        return reader.createValue(v);
    }

	@Override
	public Iterator<Map.Entry<Object, GroupValueIndex>> iterator() {
		return new CIterator(reader.iterator());
	}

	@Override
	public Iterator<Map.Entry<Object, GroupValueIndex>> previousIterator() {
		return new CIterator(reader.previousIterator());
	}

    /**
     * 从某个值开始的entry的set
     *
     * @param start
     * @return entry的set
     */
    @Override
    public Iterator<Map.Entry<Object, GroupValueIndex>> iterator(Object start) {
        if (start == null){
            Iterator iter = reader.iterator(reader.firstKey());
            if (iter.hasNext()){
                iter.next();
            }
            return new CIterator(iter);
        } else {
            return new CIterator(reader.iterator(start));
        }
    }

    /**
     * 从某个值开始的反向的entry的set
     *
     * @param start
     * @return entry的set
     */
    @Override
    public Iterator<Map.Entry<Object, GroupValueIndex>> previousIterator(Object start) {
        if (start == null){
            Iterator iter = reader.previousIterator(reader.lastKey());
            if (iter.hasNext()){
                iter.next();
            }
            return new CIterator(iter);
        } else {
            return new CIterator(reader.previousIterator(start));
        }
    }

    public Iterator<Map.Entry<Object, GroupValueIndex>> iterator(GroupValueIndex filterGVI) {
        if (filterGVI == null || getter == null){
            Iterator iter = reader.iterator();
            return new CIterator(iter);
        } else {
            return new CIterator(DimensionIteratorCreator.createValueMapIterator(getter, filterGVI, true));
        }
    }

    public Iterator<Map.Entry<Object, GroupValueIndex>> previousIterator(GroupValueIndex filterGVI) {
        if (filterGVI == null|| getter == null){
            Iterator iter = reader.previousIterator();
            return new CIterator(iter);
        } else {
            return new CIterator(DimensionIteratorCreator.createValueMapIterator(getter, filterGVI, false));
        }
    }


    public Iterator<Map.Entry<Object, GroupValueIndex>> iterator(Object start, GroupValueIndex filterGVI) {
        if (start == null){
            Iterator iter = reader.iterator(reader.firstKey());
            if (iter.hasNext()){
                iter.next();
            }
            return new CIterator(iter);
        } else {
            if (getter == null){
                return new CIterator(reader.iterator(start));
            } else {
                return new CIterator(DimensionIteratorCreator.createValueMapIterator(getter, filterGVI, start, true));
            }
        }
    }

    public Iterator<Map.Entry<Object, GroupValueIndex>> previousIterator(Object start, GroupValueIndex filterGVI) {
        if (start == null){
            Iterator iter = reader.previousIterator(reader.lastKey());
            if (iter.hasNext()){
                iter.next();
            }
            return new CIterator(iter);
        } else {
            if (getter == null){
                return new CIterator(reader.previousIterator(start));
            } else {
                return new CIterator(DimensionIteratorCreator.createValueMapIterator(getter, filterGVI, start, false));
            }
        }
    }

    @Override
    public Object getGroupValue(int position) {
        if (position == sizeOfGroup() - 1){
            return null;
        }
        return reader.getGroupValue(position);
    }

    @Override
    public GroupValueIndex getGroupValueIndex(int groupValuePosition) {
        if (groupValuePosition == sizeOfGroup() - 1){
            return nullIndex;
        }
        return reader.getGroupValueIndex(groupValuePosition);
    }

    @Override
    public Object getOriginalValue(int rowNumber) {
        return null;
    }

    @Override
	public Object firstKey() {
		return reader.firstKey();
	}

	@Override
	public Object lastKey() {
		return reader.lastKey();
	}
	
	private class CIterator implements Iterator {
		private Iterator iter;
		private boolean doesntReadEnd = true;
		
		public CIterator (Iterator iter) {
			this.iter = iter;
		}

		@Override
		public boolean hasNext() {
			return doesntReadEnd;
		}

		@Override
		public Object next() {
			if(iter.hasNext()){
				return iter.next();
			} else if(doesntReadEnd){
				doesntReadEnd = false;
				return new NullEntry();
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
		}

	}
	
	private class NullEntry implements Map.Entry {

		@Override
		public Object getKey() {
			return null;
		}

		@Override
		public Object getValue() {
			return nullIndex;
		}

		@Override
		public Object setValue(Object value) {
			return null;
		}
		
	}

	@Override
	public long nonPrecisionSize() {
		return reader.nonPrecisionSize() + 1;
	}

}