package com.fr.bi.stable.structure.collection;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by 小灰灰 on 2014/8/13.
 */
public class CubeIndexGetterWithNullValue implements ICubeColumnIndexReader {
    private ICubeColumnIndexReader getter;
    private GroupValueIndex nullIndex;

    public CubeIndexGetterWithNullValue(ICubeColumnIndexReader getter, GroupValueIndex nullIndex) {
        this.getter = getter;
        this.nullIndex = nullIndex;
    }

    @Override
    public int sizeOfGroup() {
        return getter.sizeOfGroup() + 1;
    }

    public GroupValueIndex getIndex (Object groupValues) {
        if(groupValues == null) {
            return nullIndex;
        } else {
            return getter.getIndex(groupValues);
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
        return getter.createKey(length);
    }

    @Override
    public GroupValueIndex getNULLIndex() {
        return nullIndex;
    }

    @Override
    public Object createValue(Object v) {
        return getter.createValue(v);
    }

	@Override
	public Iterator iterator() {
		return new CIterator(getter.iterator());
	}

	@Override
	public Iterator previousIterator() {
		return new CIterator(getter.previousIterator());
	}

    /**
     * 从某个值开始的entry的set
     *
     * @param start
     * @return entry的set
     */
    @Override
    public Iterator<Map.Entry> iterator(Object start) {
        if (start == null){
            Iterator iter = getter.iterator(getter.firstKey());
            if (iter.hasNext()){
                iter.next();
            }
            return new CIterator(iter);
        } else {
            return new CIterator(getter.iterator(start));
        }
    }

    /**
     * 从某个值开始的反向的entry的set
     *
     * @param start
     * @return entry的set
     */
    @Override
    public Iterator<Map.Entry> previousIterator(Object start) {
        if (start == null){
            Iterator iter = getter.previousIterator(getter.lastKey());
            if (iter.hasNext()){
                iter.next();
            }
            return new CIterator(iter);
        } else {
            return new CIterator(getter.previousIterator(start));
        }
    }

    @Override
    public Object getGroupValue(int position) {
        return null;
    }

    @Override
    public Object getOriginalValue(int rowNumber) {
        return null;
    }

    @Override
	public Object firstKey() {
		return getter.firstKey();
	}

	@Override
	public Object lastKey() {
		return getter.lastKey();
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
		return getter.nonPrecisionSize() + 1;
	}

}