package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.ResourceDiscoveryImpl;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.util.ArrayLookupHelper;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/7
 */
abstract class BaseDictColumn<T> implements DictionaryEncodedColumn<T> {
    static final String KEY = "dict_key";

    private static final String INDEX = "dict_index";
    private static final String SIZE = "dict_size";

    private static final String GLOBAL_INDEX = "global_dict_index";
    private static final String GLOBAL_SIZE = "global_dict_size";

    static final ResourceDiscovery DISCOVERY = ResourceDiscoveryImpl.getInstance();

    IResourceLocation parent;

    private IntWriter sizeWriter;
    private IntReader sizeReader;

    private IntWriter indexWriter;
    private IntReader indexReader;

    private IntWriter globalSizeWriter;
    private IntReader globalSizeReader;

    private IntWriter globalIndexWriter;
    private IntReader globalIndexReader;

    private Comparator<T> keyComparator;

    BaseDictColumn(IResourceLocation parent, Comparator<T> keyComparator) {
        this.parent = parent;
        this.keyComparator = keyComparator;
    }

    @Override
    public int size() {
        initSizeReader();
        return sizeReader.get(0);
    }

    @Override
    public void putSize(int size) {
        initSizeWriter();
        sizeWriter.put(0, size);
    }

    @Override
    public int globalSize() {
        initGlobalSizeReader();
        return globalSizeReader.get(0);
    }

    @Override
    public void putGlobalSize(int globalSize) {
        initGlobalSizeWriter();
        globalSizeWriter.put(0, globalSize);
    }

    @Override
    public int getIndexByRow(int row) {
        initIndexReader();
        return indexReader.get(row);
    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        initGlobalIndexReader();
        return globalIndexReader.get(index);
    }

    @Override
    public void putGlobalIndex(int index, int globalIndex) {
        initGlobalIndexWriter();
        globalIndexWriter.put(index, globalIndex);
    }

    @Override
    public int getGlobalIndexByRow(int row) {
        return getGlobalIndexByIndex(getIndexByRow(row));
    }

    @Override
    public void putIndex(int row, int index) {
        initIndexWriter();
        indexWriter.put(row, index);
    }

    @Override
    public int getIndex(Object value) {
        if (value == null) {
            return 0;
        }
        return ArrayLookupHelper.lookup((T[]) new Object[]{convertValue(value)}, lookup)[0];
    }

    /**
     * 用于不同数值类型之间转换。
     * ArrayLookupHelper.binarySearch(Lookup<T> lookup, T value | T[] values)用到的比较器要求类型一致。
     * 把要查找的值类型转化为lookup用到的字典类型参数类型，可以减少数值类过滤器处理不同类型的代码。
     *
     * @param value
     * @return
     */
    abstract T convertValue(Object value);

    @Override
    public Comparator<T> getComparator() {
        return keyComparator;
    }

    private void initSizeWriter() {
        if (sizeWriter != null) {
            return;
        }
        IResourceLocation sizeLocation = parent.buildChildLocation(SIZE);
        sizeWriter = DISCOVERY.getWriter(sizeLocation, new BuildConf(IoType.WRITE, DataType.INT));
    }

    private void initSizeReader() {
        if (sizeReader != null) {
            return;
        }
        IResourceLocation sizeLocation = parent.buildChildLocation(SIZE);
        sizeReader = DISCOVERY.getReader(sizeLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initIndexWriter() {
        if (indexWriter != null) {
            return;
        }
        IResourceLocation indexLocation = parent.buildChildLocation(INDEX);
        indexWriter = DISCOVERY.getWriter(indexLocation, new BuildConf(IoType.WRITE, DataType.INT));
    }

    private void initIndexReader() {
        if (indexReader != null) {
            return;
        }
        IResourceLocation indexLocation = parent.buildChildLocation(INDEX);
        indexReader = DISCOVERY.getReader(indexLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initGlobalSizeWriter() {
        if (globalSizeWriter != null) {
            return;
        }
        IResourceLocation globalSizeLocation = parent.buildChildLocation(GLOBAL_SIZE);
        globalSizeWriter = DISCOVERY.getWriter(globalSizeLocation, new BuildConf(IoType.WRITE, DataType.INT));
    }

    private void initGlobalSizeReader() {
        if (globalSizeReader != null) {
            return;
        }
        IResourceLocation globalSizeLocation = parent.buildChildLocation(GLOBAL_SIZE);
        globalSizeReader = DISCOVERY.getReader(globalSizeLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initGlobalIndexWriter() {
        if (globalIndexWriter != null) {
            return;
        }
        IResourceLocation globalIndexLocation = parent.buildChildLocation(GLOBAL_INDEX);
        globalIndexWriter = DISCOVERY.getWriter(globalIndexLocation, new BuildConf(IoType.WRITE, DataType.INT));
    }

    private void initGlobalIndexReader() {
        if (globalIndexReader != null) {
            return;
        }
        IResourceLocation globalIndexLocation = parent.buildChildLocation(GLOBAL_INDEX);
        globalIndexReader = DISCOVERY.getReader(globalIndexLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    @Override
    public void flush() {
        if (sizeWriter != null) {
            sizeWriter.flush();
        }
        if (indexWriter != null) {
            indexWriter.flush();
        }
        if (globalSizeWriter != null) {
            globalSizeWriter.flush();
        }
        if (globalIndexWriter != null) {
            globalIndexWriter.flush();
        }
    }

    @Override
    public void release() {
        if (sizeWriter != null) {
            sizeWriter.release();
            sizeWriter = null;
        }
        if (sizeReader != null) {
            sizeReader.release();
            sizeReader = null;
        }
        if (indexWriter != null) {
            indexWriter.release();
            indexWriter = null;
        }
        if (indexReader != null) {
            indexReader.release();
            indexReader = null;
        }
        if (globalSizeWriter != null) {
            globalSizeWriter.release();
            globalSizeWriter = null;
        }
        if (globalSizeReader != null) {
            globalSizeReader.release();
            globalSizeReader = null;
        }
        if (globalIndexWriter != null) {
            globalIndexWriter.release();
            globalIndexWriter = null;
        }
        if (globalIndexReader != null) {
            globalIndexReader.release();
            globalIndexReader = null;
        }
    }

    private ArrayLookupHelper.Lookup<T> lookup = new ArrayLookupHelper.Lookup<T>() {
        @Override
        public int minIndex() {
            return NOT_NULL_START_INDEX;
        }

        @Override
        public int maxIndex() {
            return size() - 1;
        }

        @Override
        public T lookupByIndex(int index) {
            return getValue(index);
        }

        @Override
        public int compare(T t1, T t2) {
            return keyComparator.compare(t1, t2);
        }

    };
}