package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.Writer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2017/11/16
 */
public class ResourceDiscoveryImpl implements ResourceDiscovery {
    /**
     * resourcePath -> reader
     */
    private final Map<String, Reader> readers = new ConcurrentHashMap<String, Reader>();
    /**
     * resourcePath -> writer
     */
    private final Map<String, Writer> writers = new ConcurrentHashMap<String, Writer>();

    private static boolean isMemory(StoreType storeType) {
        return storeType == StoreType.MEMORY;

    }

    private static boolean shouldCache(StoreType storeType) {
        return storeType == StoreType.MEMORY;
    }

    private static boolean shouldShare(StoreType storeType) {
        return storeType == StoreType.MEMORY;
    }

    @Override
    public <R extends Reader> R getReader(IResourceLocation location, BuildConf conf) {
        String path = location.getPath();
        if (!isMemory(location.getStoreType())) {
            return (R) Readers.build(location, conf);
        } else {
            synchronized (readers) {
                if (!readers.containsKey(path)) {
                    Reader reader = Readers.build(location, conf);
                    readers.put(path, reader);
                    if (shouldShare(location.getStoreType())) {
                        writers.put(path, (Writer) reader);
                    }
                    return (R) reader;
                }
                return (R) readers.get(path);
            }
        }
    }

    @Override
    public <W extends Writer> W getWriter(IResourceLocation location, BuildConf conf) {
        String path = location.getPath();
        if (!isMemory(location.getStoreType())) {
            return (W) Writers.build(location, conf);
        } else {
            synchronized (writers) {
                if (!writers.containsKey(path)) {
                    Writer writer = Writers.build(location, conf);
                    if (shouldCache(location.getStoreType())) {
                        writers.put(path, writer);
                    }
                    readers.put(path, (Reader) writer);
                    return (W) writer;
                }
                return (W) writers.get(path);
            }
        }
    }


    @Override
    public boolean exists(IResourceLocation location, BuildConf conf) {
        return getReader(location, conf).isReadable();
    }

    @Override
    public void clear() {
        readers.clear();
        writers.clear();
    }

    private static final ResourceDiscoveryImpl INSTANCE = new ResourceDiscoveryImpl();

    private ResourceDiscoveryImpl() {
    }

    public static ResourceDiscovery getInstance() {
        return INSTANCE;
    }
}
