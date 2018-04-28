package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.impl.mem.MemIo;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.Writer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2017/11/16
 */
public class ResourceDiscoveryImpl implements ResourceDiscovery {

    /**
     * 预览memio map
     * key: basePath
     * field: path
     * value: memio
     */
    private final Map<String, Map<String, MemIo>> minorMemios = new ConcurrentHashMap<String, Map<String, MemIo>>();

    /**
     * 增量realtime memio map
     * key: basePath
     * field: path
     * value: memio
     */
    private final Map<String, Map<String, MemIo>> cubeMemios = new ConcurrentHashMap<String, Map<String, MemIo>>();

    private static final ResourceDiscoveryImpl INSTANCE = new ResourceDiscoveryImpl();

    private ResourceDiscoveryImpl() {
    }

    public static ResourceDiscovery getInstance() {
        return INSTANCE;
    }

    @Override
    public <R extends Reader> R getReader(IResourceLocation location, BuildConf conf) {
        String path = location.getPath();
        String basePath = getCubeBasePath(path);
        if (!isMemory(location.getStoreType())) {
            return (R) Readers.build(location, conf);
        } else {
            if (isMinor(path)) {
                return getReader(minorMemios, basePath, path, location, conf);
            } else {
                return getReader(cubeMemios, basePath, path, location, conf);
            }
        }
    }

    @Override
    public <W extends Writer> W getWriter(IResourceLocation location, BuildConf conf) {
        String path = location.getPath();
        String basePath = getCubeBasePath(path);
        if (!isMemory(location.getStoreType())) {
            return (W) Writers.build(location, conf);
        } else {
            if (isMinor(path)) {
                return getWriter(minorMemios, basePath, path, location, conf);
            } else {
                return getWriter(cubeMemios, basePath, path, location, conf);
            }
        }
    }

    @Override
    public boolean exists(IResourceLocation location, BuildConf conf) {
        return getReader(location, conf).isReadable();
    }

    private <W extends Writer> W getWriter(Map<String, Map<String, MemIo>> paramMemios, String basePath, String path, IResourceLocation location, BuildConf conf) {
        synchronized (paramMemios) {
            if (paramMemios.containsKey(basePath)) {
                Map<String, MemIo> baseMemios = paramMemios.get(basePath);
                if (!baseMemios.containsKey(path)) {
                    MemIo reader = (MemIo) Writers.build(location, conf);
                    baseMemios.put(path, reader);
                }
            } else {
                Map<String, MemIo> baseMemios = new HashMap<String, MemIo>();
                MemIo reader = (MemIo) Writers.build(location, conf);
                baseMemios.put(path, reader);
                paramMemios.put(basePath, baseMemios);
            }
            return (W) paramMemios.get(basePath).get(path);
        }
    }

    private <R extends Reader> R getReader(Map<String, Map<String, MemIo>> paramMemios, String basePath, String path, IResourceLocation location, BuildConf conf) {
        synchronized (paramMemios) {
            if (paramMemios.containsKey(basePath)) {
                Map<String, MemIo> baseMemios = paramMemios.get(basePath);
                if (!baseMemios.containsKey(path)) {
                    MemIo reader = (MemIo) Readers.build(location, conf);
                    baseMemios.put(path, reader);
                }
            } else {
                Map<String, MemIo> baseMemios = new HashMap<String, MemIo>();
                MemIo reader = (MemIo) Readers.build(location, conf);
                baseMemios.put(path, reader);
                paramMemios.put(basePath, baseMemios);
            }
            return (R) paramMemios.get(basePath).get(path);
        }
    }

    private static boolean isMemory(StoreType storeType) {
        return storeType == StoreType.MEMORY;
    }

    //todo 增量的memio慎重clear!!!
    @Override
    public void clear() {
        for (Map.Entry<String, Map<String, MemIo>> mapEntry : minorMemios.entrySet()) {
            for (Map.Entry<String, MemIo> entry : mapEntry.getValue().entrySet()) {
                entry.getValue().release();
            }
        }
        minorMemios.clear();
    }

    private boolean isMinor(String path) {
        return path.contains("minor_cubes");
    }

    //todo 路径需要单独配置，后续需要对此进行改正，现在先简单处理
    private String getCubeBasePath(String path) {
        if (isMinor(path)) {
            int index = path.indexOf("minor_cubes/");
            return path.substring(0, index + "minor_cubes/".length() + 8);
        } else {
            int index = path.indexOf("cubes/");
            return path.substring(0, index + "cubes/".length() + 8);
        }
    }

    @Override
    public boolean isCubeResourceEmpty() {
        return cubeMemios.isEmpty();
    }

    @Override
    public Map<String, MemIo> removeCubeResource(String basePath) {
        return cubeMemios.remove(new ResourceLocation(basePath).getPath());
    }
}
