package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.WriteType;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.IntWriter;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.util.ArrayLookupHelper;
import com.fr.swift.util.IoUtil;

import java.util.Comparator;

/**
 * @author anchore
 * @date 2017/11/7
 */
abstract class BaseDictColumn<T, R extends Reader> extends AbstractDictColumn<T> {
    static final String KEY = "dict_key";

    private static final String INDEX = "dict_index";
    private static final String SIZE = "dict_size";

    private static final String GLOBAL_INDEX = "global_dict_index";
    private static final String GLOBAL_SIZE = "global_dict_size";

    static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    IResourceLocation parent;

    R keyReader;

    private IntReader sizeReader;

    private IntReader indexReader;

    private IntReader globalSizeReader;

    private IntReader globalIndexReader;

    private Comparator<T> keyComparator;

    Putter<T> putter;

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
    public int globalSize() {
        initGlobalSizeReader();
        return globalSizeReader.get(0);
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
    public int getIndex(Object value) {
        if (value == null) {
            return 0;
        }
        return ArrayLookupHelper.lookup((T[]) new Object[]{value}, lookup)[0];
    }

    @Override
    public Comparator<T> getComparator() {
        return keyComparator;
    }

    private void initSizeReader() {
        if (sizeReader != null) {
            return;
        }
        IResourceLocation sizeLocation = parent.buildChildLocation(SIZE);
        sizeReader = DISCOVERY.getReader(sizeLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initIndexReader() {
        if (indexReader != null) {
            return;
        }
        IResourceLocation indexLocation = parent.buildChildLocation(INDEX);
        indexReader = DISCOVERY.getReader(indexLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initGlobalSizeReader() {
        if (globalSizeReader != null) {
            return;
        }
        IResourceLocation globalSizeLocation = parent.buildChildLocation(GLOBAL_SIZE);
        globalSizeReader = DISCOVERY.getReader(globalSizeLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    private void initGlobalIndexReader() {
        if (globalIndexReader != null) {
            return;
        }
        IResourceLocation globalIndexLocation = parent.buildChildLocation(GLOBAL_INDEX);
        globalIndexReader = DISCOVERY.getReader(globalIndexLocation, new BuildConf(IoType.READ, DataType.INT));
    }

    abstract void initKeyReader();

    @Override
    public boolean isReadable() {
        initSizeReader();
        boolean readable = sizeReader.isReadable();
        if (parent.getStoreType().isPersistent()) {
            IoUtil.release(sizeReader);
        }
        sizeReader = null;
        return readable;
    }

    @Override
    public void release() {
        IoUtil.release(keyReader, sizeReader, indexReader, globalSizeReader, globalIndexReader, putter);
        keyReader = null;
        sizeReader = null;
        indexReader = null;
        globalSizeReader = null;
        globalIndexReader = null;

        putter = null;
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

    abstract class BasePutter<W extends Writer> implements Putter<T> {
        W keyWriter;

        private IntWriter sizeWriter;

        private IntWriter indexWriter;

        private IntWriter globalSizeWriter;

        private IntWriter globalIndexWriter;

        @Override
        public void putSize(int size) {
            initSizeWriter();
            sizeWriter.put(0, size);
        }

        @Override
        public void putIndex(int row, int index) {
            initIndexWriter();
            indexWriter.put(row, index);
        }

        @Override
        public void putGlobalSize(int globalSize) {
            initGlobalSizeWriter();
            globalSizeWriter.put(0, globalSize);
        }

        @Override
        public void putGlobalIndex(int index, int globalIndex) {
            initGlobalIndexWriter();
            globalIndexWriter.put(index, globalIndex);
        }

        private void initSizeWriter() {
            if (sizeWriter != null) {
                return;
            }
            IResourceLocation sizeLocation = parent.buildChildLocation(SIZE);
            sizeWriter = DISCOVERY.getWriter(sizeLocation, new BuildConf(IoType.WRITE, DataType.INT, WriteType.OVERWRITE));
        }

        private void initIndexWriter() {
            if (indexWriter != null) {
                return;
            }
            IResourceLocation indexLocation = parent.buildChildLocation(INDEX);
            indexWriter = DISCOVERY.getWriter(indexLocation, new BuildConf(IoType.WRITE, DataType.INT));
        }

        private void initGlobalSizeWriter() {
            if (globalSizeWriter != null) {
                return;
            }
            IResourceLocation globalSizeLocation = parent.buildChildLocation(GLOBAL_SIZE);
            globalSizeWriter = DISCOVERY.getWriter(globalSizeLocation, new BuildConf(IoType.WRITE, DataType.INT, WriteType.OVERWRITE));
        }

        private void initGlobalIndexWriter() {
            if (globalIndexWriter != null) {
                return;
            }
            IResourceLocation globalIndexLocation = parent.buildChildLocation(GLOBAL_INDEX);
            globalIndexWriter = DISCOVERY.getWriter(globalIndexLocation, new BuildConf(IoType.WRITE, DataType.INT));
        }

        abstract void initKeyWriter();

        @Override
        public void release() {
            IoUtil.release(keyWriter, sizeWriter, indexWriter, globalSizeWriter, globalIndexWriter);
            keyWriter = null;
            sizeWriter = null;
            indexWriter = null;
            globalSizeWriter = null;
            globalIndexWriter = null;
        }
    }
}