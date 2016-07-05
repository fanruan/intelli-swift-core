package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeValueEntryGetter;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.structure.ICubeIndexDataGetterService;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.finebi.cube.utils.BICubePathUtils;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.structure.object.CubeValueEntry;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.List;

/**
 * Created by 小灰灰 on 2016/7/5.
 */
public class BCubeValueEntryGetter<T> implements ICubeValueEntryGetter {
    private CubeColumnReaderService<T> columnReaderService;
    private ICubeIndexDataGetterService indexDataGetterService;
    private CubeRelationEntityGetterService reverseRowGetter;

    public BCubeValueEntryGetter(CubeColumnReaderService<T> columnReaderService, List<BITableSourceRelation> relationList) {
        this.columnReaderService = columnReaderService;
        if (isRelationIndex(relationList)) {
            try {
                reverseRowGetter = columnReaderService.getRelationIndexGetter(BICubePathUtils.convert(relationList));
                indexDataGetterService = reverseRowGetter;
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e);
            }
        } else {
            indexDataGetterService = columnReaderService;
        }
    }

    private Integer getReverseRow(int row){
        try {
            return reverseRowGetter == null ? row : reverseRowGetter.getReverseIndex(row);
        } catch (BIResourceInvalidException e) {
            BINonValueUtils.beyondControl(e);
        }
        return null;
    }

    private boolean isRelationIndex(List<BITableSourceRelation> relationList) {
        return relationList != null && !relationList.isEmpty();
    }

    @Override
    public GroupValueIndex getIndexByRow(int row) {
        try {
            Integer reverseRow = getReverseRow(row);
            Integer groupRow = reverseRow == null ? null : columnReaderService.getPositionOfGroupByRow(reverseRow);
            return getGroupValueIndex(groupRow);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return GVIFactory.createAllEmptyIndexGVI();
    }

    private GroupValueIndex getGroupValueIndex(Integer row) throws BICubeIndexException {
        return row == null ? indexDataGetterService.getNULLIndex(0) : indexDataGetterService.getBitmapIndex(row);
    }

    private T getGroupValue(Integer row) throws BICubeIndexException {
        return row == null ? null : columnReaderService.getGroupValue(row);
    }

    @Override
    public CubeValueEntry getEntryByRow(int row) {
        Integer groupRow = null;
        GroupValueIndex gvi = null;
        T value = null;
        try {
            Integer reverseRow = getReverseRow(row);
            groupRow = reverseRow == null ? null : columnReaderService.getPositionOfGroupByRow(reverseRow);
            gvi = getGroupValueIndex(groupRow);
            value = getGroupValue(groupRow);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return new CubeValueEntry(value, gvi, groupRow);
    }

    @Override
    public int getGroupSize() {
        return columnReaderService.sizeOfGroup();
    }
}
