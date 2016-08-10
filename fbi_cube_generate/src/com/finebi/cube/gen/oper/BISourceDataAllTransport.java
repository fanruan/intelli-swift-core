package com.finebi.cube.gen.oper;

import com.finebi.cube.adapter.BIUserCubeManager;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.stable.bridge.StableFactory;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by kary on 16/7/13.
 */
public class BISourceDataAllTransport extends BISourceDataTransport {
    public BISourceDataAllTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
        super(cube, tableSource, allSources, parentTableSource, version);
    }
    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        long t = System.currentTimeMillis();
        try {
            recordTableInfo();
            long count = transport();
            if (count >= 0) {
                /*清除remove的过滤条件*/
                TreeSet<Integer> sortRemovedList = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
                tableEntityService.recordRemovedLine(sortRemovedList);
                tableEntityService.recordRowCount(count);
                tableEntityService.clear();
            }
            tableEntityService.addVersion(version);
            long tableCostTime = System.currentTimeMillis() - t;
            if (null != tableSource.getPersistentTable()) {
                System.out.println("table usage:" + tableCostTime);
                biLogManager.infoTable(tableSource.getPersistentTable(), tableCostTime, UserControl.getInstance().getSuperManagerID());
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            if (null != tableSource.getPersistentTable()) {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            }
        } finally {
            return null;
        }
    }
    private long transport() {
        List<ICubeFieldSource> fieldList = tableEntityService.getFieldInfo();
        ICubeFieldSource[] cubeFieldSources = new ICubeFieldSource[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            fieldList.get(i).setTableBelongTo(tableSource);
            cubeFieldSources[i] = fieldList.get(i);
        }
        return this.tableSource.read(new Traversal<BIDataValue>() {
            @Override
            public void actionPerformed(BIDataValue v) {
                try {
                    tableEntityService.addDataValue(v);
                } catch (BICubeColumnAbsentException e) {
                    e.printStackTrace();
                }
            }
        }, cubeFieldSources, new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube));
    }
}
