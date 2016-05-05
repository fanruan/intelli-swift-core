package com.finebi.cube.gen.oper;

import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BIRelationIndexGenerator extends BIProcessor {
    protected ICube cube;
    protected BICubeRelation relation;

    public BIRelationIndexGenerator(ICube cube, BICubeRelation relation) {
        this.cube = cube;
        this.relation = relation;
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        buildRelationIndex();
        return null;
    }

    @Override
    public void release() {

    }

    private void buildRelationIndex() {
        ICubeTableEntityGetterService primaryTable = null;
        ICubeTableEntityGetterService foreignTable = null;
        ICubeColumnEntityService primaryColumn = null;
        ICubeColumnEntityService foreignColumn = null;
        BICubeRelationEntity tableRelation = null;
        try {
            BIColumnKey primaryKey = relation.getPrimaryField();
            BIColumnKey foreignKey = relation.getForeignField();
            ITableKey primaryTableKey = relation.getPrimaryTable();
            ITableKey foreignTableKey = relation.getForeignTable();
            primaryTable = cube.getCubeTable(primaryTableKey);
            foreignTable = cube.getCubeTable(foreignTableKey);
            /**
             * 关联的主字段对象
             */
            primaryColumn = (ICubeColumnEntityService) cube.getCubeColumn(primaryTableKey, primaryKey);
            /**
             * 关联的子字段对象
             */
            foreignColumn = (ICubeColumnEntityService) cube.getCubeColumn(foreignTableKey, foreignKey);
            /**
             * 表间关联对象
             */
            tableRelation = (BICubeRelationEntity) cube.getCubeRelation(primaryTableKey, relation);

            final GroupValueIndex appearPrimaryValue = GVIFactory.createAllEmptyIndexGVI();
            /**
             * 主表的行数
             */
            int primaryRowCount = primaryTable.getRowCount();
            for (int row = 0; row < primaryRowCount; row++) {
                /**
                 * 关联主字段的value值
                 */
                Object primaryColumnValue = primaryColumn.getOriginalValueByRow(row);
                /**
                 * value值在子字段中的索引位置
                 */
                int position = foreignColumn.getPositionOfGroup(primaryColumnValue);
                /**
                 * 依据索引位置，取出索引
                 */
                GroupValueIndex foreignGroupValueIndex;
                if (position != -1) {
                    foreignGroupValueIndex = foreignColumn.getBitmapIndex(position);
                } else {
                    foreignGroupValueIndex = GVIFactory.createAllEmptyIndexGVI();
                }
                appearPrimaryValue.or(foreignGroupValueIndex);
                tableRelation.addRelationIndex(row, foreignGroupValueIndex);
            }
            tableRelation.addRelationNULLIndex(0, appearPrimaryValue.NOT(foreignTable.getRowCount()));
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        } finally {
            if (primaryTable != null) {
                primaryTable.releaseResource();
            }
            if (foreignTable != null) {
                foreignTable.releaseResource();
            }
            if (primaryColumn != null) {
                primaryColumn.releaseResource();
            }
            if (foreignColumn != null) {
                foreignColumn.releaseResource();
            }
            if (tableRelation != null) {
                tableRelation.releaseResource();
            }

        }

    }

}
