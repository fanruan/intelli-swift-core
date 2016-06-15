package com.finebi.cube.gen.oper;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.pubsub.BIProcessor;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.stable.bridge.StableFactory;

import java.util.*;

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
        BILogManager biLogManager = StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManager.class);
        biLogManager.logRelationStart(UserControl.getInstance().getSuperManagerID());
        long t = System.currentTimeMillis();
        try {
            buildRelationIndex();
            long costTime = System.currentTimeMillis() - t;
            biLogManager.infoRelation(getRelationColumnKeyInfo(), costTime, UserControl.getInstance().getSuperManagerID());
            return null;
        } catch (Exception e) {
            biLogManager.errorRelation(getRelationColumnKeyInfo(), e.getMessage(), UserControl.getInstance().getSuperManagerID());
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            return null;
        }
    }

    public RelationColumnKey getRelationColumnKeyInfo() {
        BITableSourceRelation tableRelation = getTableRelation(this.relation);
        ICubeFieldSource field = tableRelation.getPrimaryField();
        List<BITableSourceRelation> relations = new ArrayList<BITableSourceRelation>();
        relations.add(tableRelation);
        return new RelationColumnKey(field, relations);
    }

    private BITableSourceRelation getTableRelation(BICubeRelation relation) {
        ICubeFieldSource primaryField = null;
        ICubeFieldSource foreignField = null;
        CubeTableSource primaryTable = null;
        CubeTableSource foreignTable = null;
        Set<CubeTableSource> allTableSource = getAllTableSource();
        for (CubeTableSource cubeTableSource : allTableSource) {
            if (ComparatorUtils.equals(relation.getPrimaryTable().getSourceID(), cubeTableSource.getSourceID())) {
                primaryTable = cubeTableSource;
                Set<CubeTableSource> primarySources = new HashSet<CubeTableSource>();
                primarySources.add(cubeTableSource);
                for (ICubeFieldSource iCubeFieldSource : primaryTable.getFieldsArray(primarySources)) {
                    if (ComparatorUtils.equals(iCubeFieldSource.getFieldName(),relation.getPrimaryField().getColumnName())) {
                        primaryField = iCubeFieldSource;
                    }
                }
                break;
            }
        }
        for (CubeTableSource cubeTableSource : allTableSource) {
            if (ComparatorUtils.equals(relation.getForeignTable().getSourceID(), cubeTableSource.getSourceID())) {
                foreignTable = cubeTableSource;
                Set<CubeTableSource> foreignSource = new HashSet<CubeTableSource>();
                foreignSource.add(cubeTableSource);
                for (ICubeFieldSource iCubeFieldSource : foreignTable.getFieldsArray(foreignSource)) {
                    if (ComparatorUtils.equals(iCubeFieldSource.getFieldName(), relation.getForeignField().getColumnName())) {
                        foreignField = iCubeFieldSource;
                    }
                }
                break;
            }
        }
        BITableSourceRelation biTableSourceRelation=new BITableSourceRelation(primaryField,foreignField,primaryTable,foreignTable);
        return  biTableSourceRelation;
    }

    private Set<CubeTableSource> getAllTableSource() {
        Set<CubeTableSource> cubeTableSourceSet = new HashSet<CubeTableSource>();
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(UserControl.getInstance().getSuperManagerID());
        for (IBusinessPackageGetterService pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                cubeTableSourceSet.add(table.getTableSource());
            }
        }
        return cubeTableSourceSet;
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
                primaryTable.clear();
            }
            if (foreignTable != null) {
                foreignTable.clear();
            }
            if (primaryColumn != null) {
                primaryColumn.clear();
            }
            if (foreignColumn != null) {
                foreignColumn.clear();
            }
            if (tableRelation != null) {
                tableRelation.clear();
            }

        }

    }

}
