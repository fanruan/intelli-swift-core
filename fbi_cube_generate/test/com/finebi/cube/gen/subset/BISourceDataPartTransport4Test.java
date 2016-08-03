package com.finebi.cube.gen.subset;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.gen.oper.BISourceDataPartTransport;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/8/03.
 */
public class BISourceDataPartTransport4Test extends BISourceDataPartTransport {
    private int oldCount;

    @Override
    public void release() {

    }

    public BISourceDataPartTransport4Test(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, int oldCount) {
        super(cube, tableSource, allSources, parentTableSource, 1);
        this.oldCount = oldCount;
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        super.recordTableInfo();
        long count = transport();
        if (count >= 0) {
            tableEntityService.recordRowCount(count);
        }
        tableEntityService.addVersion(version);
        tableEntityService.recordRowCount(count);
    return null;
    }

    private long transport() {
        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            fieldList.get(i).setTableBelongTo(tableSource);
            cubeFieldSources[i] = fieldList.get(i);
        }

        return this.tableSource.read4Part(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                try {
                    tableEntityService.addDataValue(v);
                } catch (BICubeColumnAbsentException e) {
                    e.printStackTrace();
                }
            }
        }, cubeFieldSources, "", oldCount);
    }
}
