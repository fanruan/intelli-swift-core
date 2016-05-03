package com.finebi.cube.gen.oper;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.structure.*;
import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/8.
 *
 * @author Connery
 * @since 4.0
 */
public class BITablePathIndexBuilder extends BIProcessor {
    protected ICube cube;
    protected BICubeTablePath relationPath;

    public BITablePathIndexBuilder(ICube cube, BICubeTablePath relationPath) {
        this.cube = cube;
        this.relationPath = relationPath;
    }

    @Override
    public Object mainTask() {
        buildRelationPathIndex();
        return null;
    }

    @Override
    public void release() {

    }

    private void buildRelationPathIndex() {
        if (relationPath.size() >= 2) {
            ICubeRelationEntityGetterService lastRelationEntity = null;
            ICubeRelationEntityGetterService frontRelationPathReader = null;
            ICubeRelationEntityService targetPathEntity = null;
            try {

                int primaryRowCount = getPrimaryTableRowCount();
                lastRelationEntity = buildLastRelationReader();
                frontRelationPathReader = buildFrontRelationPathReader();
                targetPathEntity = buildTargetRelationPathWriter();
                final GroupValueIndex appearPrimaryValue = GVIFactory.createAllEmptyIndexGVI();
                for (int row = 0; row < primaryRowCount; row++) {
                    GroupValueIndex frontGroupValueIndex = frontRelationPathReader.getBitmapIndex(row);
                    GroupValueIndex resultGroupValueIndex = getTableLinkedOrGVI(frontGroupValueIndex, lastRelationEntity);
                    appearPrimaryValue.or(resultGroupValueIndex);
                    targetPathEntity.addRelationIndex(row, resultGroupValueIndex);
                }
                targetPathEntity.addRelationNULLIndex(0, appearPrimaryValue.NOT(getJuniorTableRowCount()));
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            } finally {
                if (lastRelationEntity != null) {
                    lastRelationEntity.clear();
                }
                if (frontRelationPathReader != null) {
                    frontRelationPathReader.clear();
                }
                if (targetPathEntity != null) {
                    targetPathEntity.clear();
                }
            }
        }
    }

    public static GroupValueIndex getTableLinkedOrGVI(GroupValueIndex currentIndex, final ICubeIndexDataGetterService reader) {
        if (currentIndex != null) {

            final GroupValueIndex result = new RoaringGroupValueIndex();
            currentIndex.Traversal(new SingleRowTraversalAction() {
                @Override
                public void actionPerformed(int rowIndices) {
                    try {
                        result.or(reader.getBitmapIndex(rowIndices));
                    } catch (BICubeIndexException e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                }
            });
            return result;
        }
        return null;
    }

    protected int getPrimaryTableRowCount() throws BITablePathEmptyException {
        ITableKey primaryTableKey = relationPath.getFirstRelation().getPrimaryTable();
        ICubeTableEntityGetterService primaryTable = cube.getCubeTable(primaryTableKey);
        int rowCount = primaryTable.getRowCount();
        primaryTable.clear();
        return rowCount;
    }

    protected int getJuniorTableRowCount() throws BITablePathEmptyException {
        ITableKey primaryTableKey = relationPath.getLastRelation().getForeignTable();
        ICubeTableEntityGetterService primaryTable = cube.getCubeTable(primaryTableKey);
        int rowCount = primaryTable.getRowCount();
        primaryTable.clear();
        return rowCount;
    }

    private ICubeRelationEntityGetterService buildLastRelationReader() throws
            BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException, BITablePathEmptyException {
        BICubeRelation lastRelation = relationPath.getLastRelation();
        ITableKey lastPrimaryKey = lastRelation.getPrimaryTable();
        return cube.getCubeRelation(lastPrimaryKey, lastRelation);
    }

    private ICubeRelationEntityService buildTargetRelationPathWriter() throws
            BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException, BITablePathEmptyException {
        BICubeTablePath frontRelation = new BICubeTablePath();
        frontRelation.copyFrom(relationPath);
        ITableKey firstPrimaryKey = relationPath.getFirstRelation().getPrimaryTable();
        return (ICubeRelationEntityService) cube.getCubeRelation(firstPrimaryKey, frontRelation);
    }

    private ICubeRelationEntityGetterService buildFrontRelationPathReader() throws
            BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException, BITablePathEmptyException {
        ITableKey firstPrimaryKey = relationPath.getFirstRelation().getPrimaryTable();
        BICubeTablePath frontRelation = buildFrontRelation();
        return cube.getCubeRelation(firstPrimaryKey, frontRelation);
    }

    private BICubeTablePath buildFrontRelation() throws BITablePathEmptyException {
        BICubeTablePath frontRelation = new BICubeTablePath();
        frontRelation.copyFrom(relationPath);
        frontRelation.removeLastRelation();
        return frontRelation;
    }

}
