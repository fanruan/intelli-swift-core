package com.fr.swift.cube.io;

import com.fr.swift.cube.io.impl.mem.MemIo;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.util.Clearable;

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

    /**
     * 目前只能删除seg，或者seg中的某一个column
     * 现只针对于内存数据
     *
     * @param location 位置
     */
    void release(IResourceLocation location);

    /**
     * 删除所有内存数据
     */
    void releaseAll();
}
