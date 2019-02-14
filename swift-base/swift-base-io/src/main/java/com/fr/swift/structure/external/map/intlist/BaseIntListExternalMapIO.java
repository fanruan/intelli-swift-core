package com.fr.swift.structure.external.map.intlist;

import com.fr.swift.cube.nio.NIOReader;
import com.fr.swift.cube.nio.NIOWriter;
import com.fr.swift.cube.nio.read.IntNIOReader;
import com.fr.swift.cube.nio.write.IntNIOWriter;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;
import com.fr.swift.structure.external.map.ExternalMapIO;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author wang
 * @date 2016/9/2
 */
abstract class BaseIntListExternalMapIO<K> implements ExternalMapIO<K, IntList> {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(BaseIntListExternalMapIO.class);
    protected IntNIOWriter valueWriter = null;
    protected IntNIOReader valueReader = null;
    protected Position positionWriter;
    protected Position positionReader;
    protected File keyFile;
    protected File valueFile;
    protected NIOWriter<K> keyWriter = null;
    protected NIOReader<K> keyReader = null;
    protected int size;

    public BaseIntListExternalMapIO(String ID_path) {
        String intPath = getValuePath(ID_path);
        String keyPath = getKeyPath(ID_path);
        keyFile = initialFile(keyPath);
        valueFile = initialFile(intPath);
        positionReader = new Position();
        positionWriter = new Position();
    }

    protected String getValuePath(String ID_path) {
        if (ID_path != null) {
            return ID_path + "_value";
        }
        return null;
    }

    protected String getKeyPath(String ID_path) {
        if (ID_path != null) {
            return ID_path + "_key";
        }
        return null;
    }

    public File initialFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (file.getParentFile().exists()) {
                    file.createNewFile();
                } else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        return file;
    }

    public NIOWriter<K> getKeyWriter() {
        if (keyWriter == null) {
            initialKeyWriter();
        }
        return keyWriter;
    }

    public NIOReader<K> getKeyReader() throws FileNotFoundException {
        if (keyReader == null) {
            initialKeyReader();
        }
        return keyReader;
    }

    public IntNIOWriter getValueWriter() {
        if (valueWriter == null) {
            initialValueWriter();
        }
        return valueWriter;
    }

    public IntNIOReader getValueReader() throws FileNotFoundException {
        if (valueReader == null) {
            initialValueReader();

        }
        return valueReader;
    }

    abstract void initialKeyReader() throws FileNotFoundException;

    abstract void initialKeyWriter();

    private void initialValueReader() throws FileNotFoundException {
        if (valueFile.exists()) {
            valueReader = new IntNIOReader(valueFile);
        } else {
            throw new FileNotFoundException();
        }
    }

    private void initialValueWriter() {
        valueWriter = new IntNIOWriter(valueFile);
    }

    @Override
    public void write(K key, IntList value) {
        writeKey(key);
        /**
         * 记录下来有多少个数据，以为用到writer对象。
         */
        getValueWriter().add(positionWriter.valuePosition++, value.size());
        for (int i = 0; i < value.size(); i++) {
            getValueWriter().add(positionWriter.valuePosition++, value.get(i));
        }
        value.clear();
    }

//    abstract int recordAmount(IntArrayList value);

    public void writeKey(K key) {
        getKeyWriter().add(positionWriter.keyPosition++, key);
    }

    public K readKey() throws FileNotFoundException {
        return getKeyReader().get(positionReader.keyPosition++);
    }

    @Override
    public Pair<K, IntList> read() throws FileNotFoundException {
        if (canRead()) {
            K key = readKey();
            int amount = getValueReader().get(positionReader.valuePosition++);
            IntList list = IntListFactory.createIntList();
            for (int i = 0; i < amount; i++) {
                list.add(getValueReader().get(positionReader.valuePosition++));
            }
            if (!isEmpty(key) || list.size() != 0) {
                return Pair.of(key, list);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean canRead() {
        return positionReader.keyPosition < size;
    }

    public abstract boolean isEmpty(K key);

    @Override
    public void close() {
        getValueWriter().release();
        try {
            getValueReader().release();
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        try {
            getKeyReader().release();
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        getKeyWriter().release();
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    class Position {
        public long keyPosition;
        public long valuePosition;
    }
}
