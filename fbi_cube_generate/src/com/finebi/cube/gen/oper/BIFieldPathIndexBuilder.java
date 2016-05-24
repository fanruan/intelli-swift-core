package com.finebi.cube.gen.oper;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/8.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldPathIndexBuilder extends BITablePathIndexBuilder {
    private BIColumnKey field;

    public BIFieldPathIndexBuilder(ICube cube, ICubeFieldSource field, BICubeTablePath relationPath) {
        this(cube, BIColumnKey.covertColumnKey(field), relationPath);
    }

    public BIFieldPathIndexBuilder(ICube cube, BIColumnKey columnKey, BICubeTablePath relationPath) {
        super(cube, relationPath);
        this.field = columnKey;
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        buildFieldPathIndex();
        return null;
    }

    @Override
    public void release() {

    }

    private void buildFieldPathIndex() {
        ICubeColumnReaderService primaryColumnReader = null;
        ICubeRelationEntityGetterService tablePathReader = null;
        ICubeRelationEntityService targetPathEntity = null;
        try {
            primaryColumnReader = buildPrimaryColumnReader();
            int primaryFieldRowCount = primaryColumnReader.sizeOfGroup();
            tablePathReader = buildTableRelationPathReader();
            targetPathEntity = buildTargetRelationPathWriter();
            final GroupValueIndex appearPrimaryValue = GVIFactory.createAllEmptyIndexGVI();
            for (int row = 0; row < primaryFieldRowCount; row++) {
                GroupValueIndex frontGroupValueIndex = primaryColumnReader.getBitmapIndex(row);
                GroupValueIndex resultGroupValueIndex = BITablePathIndexBuilder.getTableLinkedOrGVI(frontGroupValueIndex, tablePathReader);
                targetPathEntity.addRelationIndex(row, resultGroupValueIndex);
                appearPrimaryValue.or(resultGroupValueIndex);
            }
            targetPathEntity.addRelationNULLIndex(0, appearPrimaryValue.NOT(getJuniorTableRowCount()));
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        } finally {
            if (primaryColumnReader != null) {
                primaryColumnReader.clear();
            }
            if (tablePathReader != null) {
                tablePathReader.clear();
            }
            if (targetPathEntity != null) {
                tablePathReader.clear();
            }
        }
    }



    private ICubeColumnReaderService buildPrimaryColumnReader() throws BITablePathEmptyException, BICubeColumnAbsentException {
        ITableKey primaryTableKey = relationPath.getFirstRelation().getPrimaryTable();
        return cube.getCubeColumn(primaryTableKey, field);
    }


    private ICubeRelationEntityService buildTargetRelationPathWriter() throws
            BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException, BITablePathEmptyException {
        BICubeTablePath frontRelation = new BICubeTablePath();
        frontRelation.copyFrom(relationPath);
        ITableKey firstPrimaryKey = relationPath.getFirstRelation().getPrimaryTable();
        cube.getCubeColumn(firstPrimaryKey, field);
        return (ICubeRelationEntityService) cube.getCubeColumn(firstPrimaryKey, field).getRelationIndexGetter(frontRelation);
    }

    private ICubeRelationEntityGetterService buildTableRelationPathReader() throws
            BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException, BITablePathEmptyException {
        ITableKey firstPrimaryKey = relationPath.getFirstRelation().getPrimaryTable();
        BICubeTablePath tableRelationPath = buildTableRelationPath();
        return cube.getCubeRelation(firstPrimaryKey, tableRelationPath);
    }

    private BICubeTablePath buildTableRelationPath() throws BITablePathEmptyException {
        BICubeTablePath tableRelationPath = new BICubeTablePath();
        tableRelationPath.copyFrom(relationPath);
        return tableRelationPath;
    }

}
