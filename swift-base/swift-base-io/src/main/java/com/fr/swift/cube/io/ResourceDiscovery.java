package com.fr.swift.cube.io;

import com.fr.swift.cube.io.impl.mem.MemIo;
import com.fr.swift.cube.io.impl.mem.MemIoBuilder;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.function.Predicate;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author anchore
 * @date 2017/11/16
 */
public class ResourceDiscovery implements IResourceDiscovery {
    /**
     * schema/table/seg/column/...
     */
    private static final Pattern PATTERN = Pattern.compile(".+/seg\\d+?(/).+");
    private static final ResourceDiscovery INSTANCE = new ResourceDiscovery();
    /**
     * 预览mem io
     * <p>
     * schema/table/seg -> (schema/table/seg/column/... -> mem io)
     */
    private final Map<String, Map<String, MemIo>> minorMemIos = new ConcurrentHashMap<String, Map<String, MemIo>>(),

    /**
     * 增量realtime mem io
     * <p>
     * schema/table/seg -> (schema/table/seg/column/... -> mem io)
     */
    cubeMemIos = new ConcurrentHashMap<String, Map<String, MemIo>>();
    private Map<SourceKey, Long> lastUpdateTime;

    private ResourceDiscovery() {
        lastUpdateTime = new ConcurrentHashMap<SourceKey, Long>();
    }

    private static MemIo getMemIo(Map<String, Map<String, MemIo>> segMemIos, IResourceLocation location, BuildConf conf) {
        synchronized (segMemIos) {
            String path = location.getPath();
            String segPath = getSegPath(path);

            if (!segMemIos.containsKey(segPath)) {
                Map<String, MemIo> baseMemIos = new HashMap<String, MemIo>();
                MemIo memIo = MemIoBuilder.build(conf);
                baseMemIos.put(path, memIo);
                segMemIos.put(segPath, baseMemIos);
                return memIo;
            }

            Map<String, MemIo> baseMemIos = segMemIos.get(segPath);
            if (!baseMemIos.containsKey(path)) {
                MemIo memIo = MemIoBuilder.build(conf);
                baseMemIos.put(path, memIo);
                return memIo;
            }

            return segMemIos.get(segPath).get(path);
        }
    }

    private static boolean isMemory(IResourceLocation location) {
        return location.getStoreType().isTransient();
    }

    private static boolean isMinor(String path) {
        return path.contains(SwiftDatabase.MINOR_CUBE.getDir());
    }

    private static String getSegPath(String path) {
        //todo 路径需要单独配置，后续需要对此进行改正，现在先简单处理
        Matcher matcher = PATTERN.matcher(path);
        matcher.find();
        return path.substring(0, matcher.start(1));
    }

    public static IResourceDiscovery getInstance() {
        return INSTANCE;
    }

    @Override
    public <R extends Reader> R getReader(IResourceLocation location, BuildConf conf) {
        String path = location.getPath();
        if (!isMemory(location)) {
            return (R) Readers.build(location, conf);
        }
        if (isMinor(path)) {
            return (R) getMemIo(minorMemIos, location, conf);
        }
        return (R) getMemIo(cubeMemIos, location, conf);
    }

    @Override
    public <W extends Writer> W getWriter(IResourceLocation location, BuildConf conf) {
        String path = location.getPath();
        if (!isMemory(location)) {
            return (W) Writers.build(location, conf);
        }
        if (isMinor(path)) {
            return (W) getMemIo(minorMemIos, location, conf);
        }
        return (W) getMemIo(cubeMemIos, location, conf);
    }

    @Override
    public boolean exists(IResourceLocation location, BuildConf conf) {
        return getReader(location, conf).isReadable();
    }

    @Override
    public void clear() {
        //todo 增量的memio慎重clear!!!
        for (Map.Entry<String, Map<String, MemIo>> mapEntry : minorMemIos.entrySet()) {
            for (Map.Entry<String, MemIo> entry : mapEntry.getValue().entrySet()) {
                entry.getValue().release();
            }
        }
        minorMemIos.clear();
    }

    @Override
    public boolean isCubeResourceEmpty() {
        return cubeMemIos.isEmpty();
    }

    @Override
    public Map<String, MemIo> removeCubeResource(String basePath) {
        return cubeMemIos.remove(new ResourceLocation(basePath).getPath());
    }

    @Override
    public void removeIf(Predicate<String> predicate) {
        for (Iterator<Entry<String, Map<String, MemIo>>> itr = cubeMemIos.entrySet().iterator(); itr.hasNext(); ) {
            Entry<String, Map<String, MemIo>> entry = itr.next();
            if (predicate.test(entry.getKey())) {
                // 为内存io，可直接丢给gc
                itr.remove();
                continue;
            }

            for (Iterator<Entry<String, MemIo>> memIoItr = entry.getValue().entrySet().iterator(); memIoItr.hasNext(); ) {
                Entry<String, MemIo> ioEntry = memIoItr.next();
                if (predicate.test(ioEntry.getKey())) {
                    // 为内存io，可直接丢给gc
                    memIoItr.remove();
                }
            }
        }
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
