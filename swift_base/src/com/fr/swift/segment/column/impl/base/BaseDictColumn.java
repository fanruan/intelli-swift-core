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
        return ArrayLookupHelper.lookup((T[]) new Object[]{ convertValue(value) }, lookup)[0];
    }

    @Override
    public Comparator<T> getComparator() {
        return keyComparator;
    }

    /**
     * 初始化字典键的writer
     */
    abstract void initKeyWriter();

    /**
     * 初始化字典键的reader
     */
    abstract void initKeyReader();

    private void initSizeWriter() {
        if (sizeWriter != null) {
            return;
        }
        IResourceLocation sizeLocation = parent.buildChildLocation(SIZE);
        sizeWriter = (IntWriter) DISCOVERY.getWriter(sizeLocation, new BuildConf(IoType.WRITE, DataType.INT));
    }

    private void initSizeReader() {
        if (sizeReader != null) {
            return;
        }
        IResourceLocation sizeLocation = parent.buildChildLocation(SIZE);
        sizeReader = (IntReader) DISCOVERY.getReader(sizeLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initIndexWriter() {
        if (indexWriter != null) {
            return;
        }
        IResourceLocation indexLocation = parent.buildChildLocation(INDEX);
        indexWriter = (IntWriter) DISCOVERY.getWriter(indexLocation, new BuildConf(IoType.WRITE, DataType.INT));
    }

    private void initIndexReader() {
        if (indexReader != null) {
            return;
        }
        IResourceLocation indexLocation = parent.buildChildLocation(INDEX);
        indexReader = (IntReader) DISCOVERY.getReader(indexLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initGlobalSizeWriter() {
        if (globalSizeWriter != null) {
            return;
        }
        IResourceLocation globalSizeLocation = parent.buildChildLocation(GLOBAL_SIZE);
        globalSizeWriter = (IntWriter) DISCOVERY.getWriter(globalSizeLocation, new BuildConf(IoType.WRITE, DataType.INT));
    }

    private void initGlobalSizeReader() {
        if (globalSizeReader != null) {
            return;
        }
        IResourceLocation globalSizeLocation = parent.buildChildLocation(GLOBAL_SIZE);
        globalSizeReader = (IntReader) DISCOVERY.getReader(globalSizeLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initGlobalIndexWriter() {
        if (globalIndexWriter != null) {
            return;
        }
        IResourceLocation globalIndexLocation = parent.buildChildLocation(GLOBAL_INDEX);
        globalIndexWriter = (IntWriter) DISCOVERY.getWriter(globalIndexLocation, new BuildConf(IoType.WRITE, DataType.INT));
    }

    private void initGlobalIndexReader() {
        if (globalIndexReader != null) {
            return;
        }
        IResourceLocation globalIndexLocation = parent.buildChildLocation(GLOBAL_INDEX);
        globalIndexReader = (IntReader) DISCOVERY.getReader(globalIndexLocation, new BuildConf(IoType.READ, DataType.INT));
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
            return 0;
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