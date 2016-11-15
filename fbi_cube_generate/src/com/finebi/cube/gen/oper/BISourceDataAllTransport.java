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
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.DateUtils;
import com.fr.stable.bridge.StableFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by kary on 16/7/13.
 */
public class BISourceDataAllTransport extends BISourceDataTransport {
    private static final Logger logger = LoggerFactory.getLogger(BISourceDataAllTransport.class);

    public BISourceDataAllTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
        super(cube, tableSource, allSources, parentTableSource, version);
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        logger.info(BIStringUtils.append("The table:", fetchTableInfo(), " start transport task"));
        tableEntityService.recordCurrentExecuteTime();
        long t = System.currentTimeMillis();
        try {
            logger.info(BIStringUtils.append("The table:", fetchTableInfo(), " record table structure info"));
            recordTableInfo();
            logger.info(BIStringUtils.append("The table:", fetchTableInfo(), " process transportation operation"));
            buildTableBasicStructure();
            long count = transport();
            logger.info(BIStringUtils.append("The table:", fetchTableInfo(), " finish transportation operation and record ",
                    String.valueOf(count), " records"));
            if (count >= 0) {
                /*清除remove的过滤条件*/
                TreeSet<Integer> sortRemovedList = new TreeSet<Integer>(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
                tableEntityService.recordRemovedLine(sortRemovedList);
                tableEntityService.recordRowCount(count);
            }
            tableEntityService.addVersion(version);
            tableEntityService.forceReleaseWriter();
            long tableCostTime = System.currentTimeMillis() - t;
            System.out.println("tableName: " + tableSource.getTableName() + " tableSourceId: " + tableSource.getSourceID() + " table usage:" + DateUtils.timeCostFrom(t));
            try {
                biLogManager.infoTable(tableSource.getPersistentTable(), tableCostTime, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(tableSource.getTableName()+e.getMessage(), e);
            }
            return null;
        } catch (Exception e) {
            try {
                biLogManager.errorTable(tableSource.getPersistentTable(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            } catch (Exception e1) {
                BILoggerFactory.getLogger().error(e1.getMessage(), e1);
            }
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }

    private String fetchTableInfo() {
        return BIStringUtils.append(tableSource.getTableName(), " ,", tableSource.getSourceID());
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
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                }
            }
        }, cubeFieldSources, new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube));
    }


}
