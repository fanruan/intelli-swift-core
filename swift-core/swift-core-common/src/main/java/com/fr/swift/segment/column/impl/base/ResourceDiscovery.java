package com.fr.swift.segment.column.impl.base;

import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.Readers;
import com.fr.swift.cube.io.Writers;
import com.fr.swift.cube.io.impl.mem.MemIo;
import com.fr.swift.cube.io.impl.mem.MemIoBuilder;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.IoUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
     * schema/table/seg -> (column/... -> mem io)
     */
    private final ConcurrentMap<String, ConcurrentMap<String, MemIo>> minorMemIos = new ConcurrentHashMap<String, ConcurrentMap<String, MemIo>>();

    /**
     * 增量realtime mem io
     * <p>
     * schema/table/seg -> (column/... -> mem io)
     */
    private final ConcurrentMap<String, ConcurrentMap<String, MemIo>> cubeMemIos = new ConcurrentHashMap<String, ConcurrentMap<String, MemIo>>();

    private static MemIo getMemIo(ConcurrentMap<String, ConcurrentMap<String, MemIo>> segMemIos, IResourceLocation location, BuildConf conf) {
        String path = location.getPath();
        String segPath = getSegPath(path);
        String ioPath = path.substring(segPath.length());

        if (!segMemIos.containsKey(segPath)) {
            ConcurrentMap<String, MemIo> baseMemIos = new ConcurrentHashMap<String, MemIo>();
            MemIo memIo = MemIoBuilder.build(conf);
            baseMemIos.put(ioPath, memIo);
            segMemIos.put(segPath, baseMemIos);
            return memIo;
        }

        Map<String, MemIo> baseMemIos = segMemIos.get(segPath);
        if (!baseMemIos.containsKey(ioPath)) {
            MemIo memIo = MemIoBuilder.build(conf);
            baseMemIos.put(ioPath, memIo);
            return memIo;
        }

        return segMemIos.get(segPath).get(ioPath);
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
            synchronized (minorMemIos) {
                return (R) getMemIo(minorMemIos, location, conf);
            }
        }
        synchronized (cubeMemIos) {
            return (R) getMemIo(cubeMemIos, location, conf);
        }
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
        for (Map.Entry<String, ConcurrentMap<String, MemIo>> mapEntry : minorMemIos.entrySet()) {
            for (Map.Entry<String, MemIo> entry : mapEntry.getValue().entrySet()) {
                entry.getValue().release();
            }
        }
        minorMemIos.clear();
    }

    @Override
    public Map<String, MemIo> removeCubeResource(String basePath) {
        return cubeMemIos.remove(new ResourceLocation(basePath).getPath());
    }

    @Override
    public void releaseTable(SwiftDatabase schema, SourceKey tableKey) {
        synchronized (cubeMemIos) {
            for (Iterator<Entry<String, ConcurrentMap<String, MemIo>>> segItr = cubeMemIos.entrySet().iterator(); segItr.hasNext(); ) {
                Entry<String, ConcurrentMap<String, MemIo>> segEntry = segItr.next();
                String segPath = segEntry.getKey();
                String tablePath = new CubePathBuilder().setSwiftSchema(schema).setTableKey(tableKey).build();
                if (segPath.startsWith(tablePath + "/")) {
                    // 匹配到seg，则整个release
                    for (MemIo memIo : segEntry.getValue().values()) {
                        IoUtil.release(memIo);
                    }

                    segItr.remove();
                }
            }
        }
    }

    @Override
    public void releaseSegment(SwiftDatabase schema, SourceKey tableKey, int segOrder) {
        synchronized (cubeMemIos) {
            for (Iterator<Entry<String, ConcurrentMap<String, MemIo>>> segItr = cubeMemIos.entrySet().iterator(); segItr.hasNext(); ) {
                Entry<String, ConcurrentMap<String, MemIo>> segEntry = segItr.next();
                String segPath = segEntry.getKey();
                if (segPath.equals(new CubePathBuilder().setSwiftSchema(schema).setTableKey(tableKey).setSegOrder(segOrder).build())) {
                    // 匹配到seg，则整个release
                    for (MemIo memIo : segEntry.getValue().values()) {
                        IoUtil.release(memIo);
                    }
                    segItr.remove();
                }
            }
        }
    }

    @Override
    public void releaseColumn(SwiftDatabase schema, SourceKey tableKey, ColumnKey columnKey) {
        synchronized (cubeMemIos) {
            for (Entry<String, ConcurrentMap<String, MemIo>> segEntry : cubeMemIos.entrySet()) {
                String segPath = segEntry.getKey();
                String tablePath = new CubePathBuilder().setSwiftSchema(schema).setTableKey(tableKey).build();
                if (!segPath.startsWith(tablePath + "/")) {
                    // 不是相关表直接跳过
                    continue;
                }

                for (Iterator<Entry<String, MemIo>> columnItr = segEntry.getValue().entrySet().iterator(); columnItr.hasNext(); ) {
                    Entry<String, MemIo> columnEntry = columnItr.next();
                    if (columnEntry.getKey().equals("/" + columnKey.getName())) {
                        IoUtil.release(columnEntry.getValue());

                        columnItr.remove();
                    }
                }
            }
        }
    }

    @Override
    public void releaseAll() {
        synchronized (cubeMemIos) {
            for (Iterator<ConcurrentMap<String, MemIo>> segItr = cubeMemIos.values().iterator(); segItr.hasNext(); ) {
                Map<String, MemIo> memIoMap = segItr.next();
                for (MemIo memIo : memIoMap.values()) {
                    IoUtil.release(memIo);
                }
                segItr.remove();
            }
        }
    }
}
