package com.fr.swift.cloud.segment.column.impl.base;

import com.fr.swift.cloud.cube.io.BuildConf;
import com.fr.swift.cloud.cube.io.impl.mem.MemIo;
import com.fr.swift.cloud.cube.io.input.Reader;
import com.fr.swift.cloud.cube.io.location.IResourceLocation;
import com.fr.swift.cloud.cube.io.output.Writer;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.segment.column.ColumnKey;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.util.Clearable;

import java.util.Map;

/**
 * This class created on 2016/3/10.
 * <p/>
 * 获得Cube数据的接口
 * 对象通过resourceRetrieve获得Location后。
 * 通过该接口即可获得相应的目标读写接口。
 *
 * @author Connery
 * @since 4.0
 */
public interface IResourceDiscovery extends Clearable {

    <R extends Reader> R getReader(IResourceLocation location, BuildConf conf);

    <W extends Writer> W getWriter(IResourceLocation location, BuildConf conf);

    boolean exists(IResourceLocation location, BuildConf conf);

    /**
     * reader，writer用完自行release，此处只负责丢弃
     */
    @Override
    void clear();

    Map<String, MemIo> removeCubeResource(String basePath);

    void releaseTable(SwiftDatabase schema, SourceKey tableKey);

    void releaseSegment(SwiftDatabase schema, SourceKey tableKey, int segOrder);

    void releaseColumn(SwiftDatabase schema, SourceKey tableKey, ColumnKey columnKey);

    /**
     * 删除所有内存数据
     */
    void releaseAll();
}