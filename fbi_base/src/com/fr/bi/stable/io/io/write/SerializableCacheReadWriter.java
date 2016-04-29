/**
 *
 */
package com.fr.bi.stable.io.io.write;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.io.io.read.ByteReadMappedList;
import com.fr.bi.common.inter.Release;
import com.fr.stable.core.UUID;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;


public class SerializableCacheReadWriter<T extends ParseBytes> implements Release {

    private ByteReadMappedList reader;

    private ByteWriteMappedList writer;

    private int size = 0;

    private File baseFile;


    public SerializableCacheReadWriter() {
        this.baseFile = createDistinctFile();
        this.baseFile.deleteOnExit();
        this.writer = new ByteWriteMappedList(baseFile.getAbsolutePath());
    }

    private static File createDistinctFile() {
        synchronized (SerializableCacheReadWriter.class) {
            initDirectory();
            File f = null;
            while (f == null || f.isFile()) {
                f = new File(BIBaseConstant.CACHE.getCacheDirectory(), UUID.randomUUID().toString());
            }
            return f;
        }
    }

    private static void initDirectory() {
        File f = BIBaseConstant.CACHE.getCacheDirectory();
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.newio.NIO#clear()
     * TODO 需要删除
     */
    @Override
    public void clear() {
        if (reader != null) {
            reader.clear();
        }
        if (writer != null) {
            writer.clear();
        }
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.newio.NIOReader#get(long)
     */
    public T get(long row) {
        synchronized (this) {
            if (row >= size || row < 0) {
                throw new ArrayIndexOutOfBoundsException((int) row);
            }
            ByteArrayInputStream bais = null;
            GZIPInputStream gzip = null;
            ObjectInputStream ois = null;
            try {
                if (reader == null) {
                    reader = new ByteReadMappedList(baseFile.getAbsolutePath());
                }
                byte[] b = reader.get(row);
                bais = new ByteArrayInputStream(b);
                gzip = new GZIPInputStream(bais);
                ois = new ObjectInputStream(gzip);
                T result = (T) ois.readObject();
                b = null;
                return result;
            } catch (IOException e) {
                FRContext.getLogger().error(e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                FRContext.getLogger().error(e.getMessage(), e);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                    }
                }
                if (gzip != null) {
                    try {
                        gzip.close();
                    } catch (IOException e) {
                    }
                }
                if (bais != null) {
                    try {
                        bais.close();
                    } catch (IOException e) {
                    }
                }
            }
            return null;
        }
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.newio.NIOWriter#add(long, java.lang.Object)
     */
    public void add(T value) {
        synchronized (this) {
            writer.add(size++, value == null ? null : value.getBytes());
            if (reader != null) {
                reader.clear();
            }
            reader = new ByteReadMappedList(baseFile.getAbsolutePath());
        }
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.newio.NIOWriter#getBaseFile()
     */
    public File getBaseFile() {
        return baseFile;
    }

    public int size() {
        return size;
    }

	/* (non-Javadoc)
     * @see com.fr.bi.cube.engine.base.NeedRelease#release()
	 */

    /**
     *
     */
    public void clearChilds() {
        synchronized (this) {
            size = 0;
        }
    }

}