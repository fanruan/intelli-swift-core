package com.finebi.cube.gen.oper;

import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by kary on 16/7/13.
 */
public class BISourceDataNeverTransport extends BISourceDataTransport{
    public BISourceDataNeverTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
        super(cube, tableSource, allSources, parentTableSource, version);
    }

    /**
     * TODO 捕获异常，发送终止消息，停止其他监听对象的等待。
     *
     * @param lastReceiveMessage
     * @return
     */
    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        return null;
    }
}
