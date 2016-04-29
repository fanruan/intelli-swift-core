package com.finebi.cube.gen.oper;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;
import com.fr.bi.stable.data.db.DBField;
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
public class BIFieldPathIndexBuilder extends BIProcessor {
    private ICube cube;
    private BICubeTablePath relationPath;
    private BIColumnKey field;

    public BIFieldPathIndexBuilder(ICube cube, DBField field, BICubeTablePath relationPath) {
        this(cube, BIColumnKey.covertColumnKey(field), relationPath);
    }

    public BIFieldPathIndexBuilder(ICube cube, BIColumnKey columnKey, BICubeTablePath relationPath) {
        this.cube = cube;
        this.relationPath = relationPath;
        this.field = columnKey;
    }

    @Override
    public Object mainTask() {
        buildFieldPathIndex();
        return null;
    }

    private void buildFieldPathIndex() {
        try {
            ICubeColumnReaderService primaryColumnReader = buildPrimaryColumnReader();
            int primaryFieldRowCount = primaryColumnReader.sizeOfGroup();
            ICubeRelationEntityGetterService tablePathReader = buildTableRelationPathReader();
            ICubeRelationEntityService targetPathEntity = buildTargetRelationPathWriter();
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
        }
    }

    private int getJuniorTableRowCount() throws BITablePathEmptyException {
        ITableKey primaryTableKey = relationPath.getLastRelation().getForeignTable();
        ICubeTableEntityGetterService primaryTable = cube.getCubeTable(primaryTableKey);
        return primaryTable.getRowCount();
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
