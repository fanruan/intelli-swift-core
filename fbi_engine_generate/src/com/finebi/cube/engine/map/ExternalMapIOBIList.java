package com.finebi.cube.engine.map;


import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.read.AbstractNIOReader;
import com.fr.bi.stable.io.newio.write.AbstractNIOWriter;
import com.fr.bi.stable.structure.collection.list.BIList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connery on 2015/12/2.
 */
public abstract class ExternalMapIOBIList<K, T> implements ExternalMapIO<K, BIList<T>> {
    protected AbstractNIOWriter<T> valueWriter = null;
    protected AbstractNIOReader<T> valueReader = null;
    protected Position positionWriter;
    protected Position positionReader;
    protected File keyFile;
    protected File valueFile;
    protected NIOWriter<K> keyWriter = null;
    protected NIOReader<K> keyReader = null;
    protected int size;

    public ExternalMapIOBIList(String ID_path) {
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
                BILogger.getLogger().error(ex.getMessage(), ex);
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

    public AbstractNIOWriter<T> getValueWriter() {
        if (valueWriter == null) {
            initialValueWriter();
        }
        return valueWriter;
    }

    public AbstractNIOReader<T> getValueReader() throws FileNotFoundException {
        if (valueReader == null) {
            initialValueReader();

        }
        return valueReader;
    }

    abstract void initialKeyReader() throws FileNotFoundException;

    abstract void initialKeyWriter();

    abstract void initialValueReader() throws FileNotFoundException;


    abstract void initialValueWriter();

    @Override
	public void write(K key, BIList<T> value) {
        writeKey(key);
        /**
         * 记录下来有多少个数据，以为用到writer对象。
         */
        getValueWriter().add(positionWriter.valuePosition++, recordAmount(value));
        for (int i = 0; i < value.size(); i++) {
            getValueWriter().add(positionWriter.valuePosition++, value.get(i));
        }
    }

    abstract T recordAmount(BIList<T> value);

    public void writeKey(K key) {
        getKeyWriter().add(positionWriter.keyPosition++, key);
    }

    public K readKey() throws FileNotFoundException {
        return getKeyReader().get(positionReader.keyPosition++);
    }

    @Override
	public Map<K, BIList<T>> read() throws FileNotFoundException {
        if (canRead()) {
            K key = readKey();
            T amount = getValueReader().get(positionReader.valuePosition++);
            BIList list = generateList();
            for (int i = 0; compare(i, amount); i++) {
                list.add(getValueReader().get(positionReader.valuePosition++));
            }
            if (!isEmpty(key) || list.size() != 0) {
                Map<K, BIList<T>> result = new HashMap<K, BIList<T>>();
                result.put(key, list);
                return result;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    abstract Boolean compare(int i, T amount);

    abstract BIList generateList();

    private boolean canRead() {
        return positionReader.keyPosition < size;
    }

    public abstract boolean isEmpty(K key);

    @Override
	public void close() {
        getValueWriter().clear();
        try {
            getValueReader().clear();
        } catch (FileNotFoundException ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
        try {
            getKeyReader().clear();
        } catch (FileNotFoundException ex) {
            BILogger.getLogger().error(ex.getMessage(), ex);
        }
        getKeyWriter().clear();
    }

    @Override
	public void setSize(Integer size) {
        this.size = size;
    }

    class Position {
        public long keyPosition;
        public long valuePosition;
    }
}