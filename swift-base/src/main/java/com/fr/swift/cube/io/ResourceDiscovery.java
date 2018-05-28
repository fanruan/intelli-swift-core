package com.fr.swift.cube.io;

import com.fr.base.FRContext;
import com.fr.general.ComparatorUtils;
import com.fr.swift.config.service.SwiftConfigServiceProvider;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.impl.mem.MemIo;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.source.SourceKey;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author anchore
 * @date 2017/11/16
 */
public class ResourceDiscovery implements IResourceDiscovery {

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

    private static final ResourceDiscovery INSTANCE = new ResourceDiscovery();

    private String defaultSwiftPath = FRContext.getCurrentEnv().getPath() + File.separator + "cubes";

    private Map<SourceKey, Long> lastUpdateTime;

    private ResourceDiscovery() {
        lastUpdateTime = new ConcurrentHashMap<SourceKey, Long>();
    }

    public static IResourceDiscovery getInstance() {
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

    @Override
    public void clear() {
        //todo 增量的memio慎重clear!!!
        for (Map.Entry<String, Map<String, MemIo>> mapEntry : minorMemios.entrySet()) {
            for (Map.Entry<String, MemIo> entry : mapEntry.getValue().entrySet()) {
                entry.getValue().release();
            }
        }
        minorMemios.clear();
    }

    private static final Pattern MINOR_PATTERN = Pattern.compile("/minor_cubes/(.+?)/.+");

    private static final Pattern PATTERN = Pattern.compile("/cubes/(.+?)/.+");

    private boolean isMinor(String path) {
        return path.contains("minor_cubes");
    }

    private String getCubeBasePath(String path) {
        //todo 路径需要单独配置，后续需要对此进行改正，现在先简单处理
        if (isMinor(path)) {
            Matcher matcher = MINOR_PATTERN.matcher(path);
            matcher.find();
            return path.substring(0, matcher.start(1));
        }

        Matcher matcher = PATTERN.matcher(path);
        matcher.find();
        return path.substring(0, matcher.start(1));
    }

    @Override
    public boolean isCubeResourceEmpty() {
        return cubeMemios.isEmpty();
    }

    @Override
    public Map<String, MemIo> removeCubeResource(String basePath) {
        return cubeMemios.remove(new ResourceLocation(basePath).getPath());
    }

    @Override
    public String getCubePath() {
        String swiftPath = SwiftConfigServiceProvider.getInstance().getSwiftPath();
        if (checkCubePath(swiftPath)) {
            return swiftPath;
        }
        return defaultSwiftPath;
    }

    @Override
    public boolean setCubePath(String path) {
        return SwiftConfigServiceProvider.getInstance().setSwiftPath(path);
    }

    @Override
    public boolean checkCubePath(String path) {
        return path != null && !ComparatorUtils.equals(path, "");
    }

    @Override
    public Date getLastUpdateTime(SourceKey sourceKey) {
        if (lastUpdateTime.containsKey(sourceKey)) {
            return new Date(lastUpdateTime.get(sourceKey));
        } else {
            return new Date(0l);
        }
    }

    @Override
    public void setLastUpdateTime(SourceKey sourceKey, long lastUpdateTime) {
        this.lastUpdateTime.put(sourceKey, lastUpdateTime);
    }
}
