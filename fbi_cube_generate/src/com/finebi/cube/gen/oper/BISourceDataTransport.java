package com.finebi.cube.gen.oper;

import com.finebi.cube.adapter.BIUserCubeManager;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BICubeTableEntity;
import com.finebi.cube.structure.ICube;
import com.finebi.cube.structure.ICubeTableEntityService;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.fs.control.UserControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class created on 2016/4/5.
 *
 * @author Connery
 * @since 4.0
 */
public class BISourceDataTransport extends BIProcessor {
    protected ITableSource tableSource;
    protected Set<ITableSource> allSources;
    protected ICubeTableEntityService tableEntityService;
    protected ICube cube;

    public BISourceDataTransport(ICube cube, ITableSource tableSource, Set<ITableSource> allSources) {
        this.tableSource = tableSource;
        this.allSources = allSources;
        this.cube = cube;
        tableEntityService = (BICubeTableEntity) cube.getCubeTable(BITableKeyUtils.convert(tableSource));
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        recordTableInfo();
        long count = transport();
        tableEntityService.recordRowCount(count);
        return null;
    }

    @Override
    public void release() {
        tableEntityService.clear();
    }

    private void recordTableInfo() {
        DBField[] columns = getFieldsArray();
        List<DBField> columnList = new ArrayList<DBField>();
        for (DBField col : columns) {
            columnList.add(convert(col));
        }
        tableEntityService.recordTableStructure(columnList);
    }

    private long transport() {
        List<DBField> fieldList = tableEntityService.getFieldInfo();
        DBField[] dbFields = new DBField[fieldList.size()];
        for (int i = 0; i < fieldList.size(); i++) {
            dbFields[i] = fieldList.get(i);
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
        }, dbFields, new BIUserCubeManager(UserControl.getInstance().getSuperManagerID(), cube));
    }

    private DBField convert(DBField column) {
        return new DBField(tableSource.getSourceID(), column.getFieldName(), column.getClassType(), column.getFieldSize());
    }

    private DBField[] getFieldsArray() {
        return tableSource.getFieldsArray(allSources);
    }

}
