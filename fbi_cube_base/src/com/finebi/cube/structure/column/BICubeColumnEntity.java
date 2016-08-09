package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.group.ICubeGroupDataService;
import com.finebi.cube.structure.property.BICubeColumnPositionOfGroupService;
import com.finebi.cube.structure.property.BICubeVersion;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;

import java.util.Comparator;

/**
 * This class created on 2016/3/3.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeColumnEntity<T> implements ICubeColumnEntityService<T> {
    protected ICubeResourceLocation currentLocation;
    protected ICubeDetailDataService<T> detailDataService;
    protected ICubeIndexDataService indexDataService;
    protected ICubeGroupDataService<T> groupDataService;
    protected ICubeVersion cubeVersion;
    protected ICubeColumnPositionOfGroupService cubeColumnPositionOfGroupService;
    protected ICubeRelationManagerService relationManagerService;
    protected ICubeResourceDiscovery discovery;

    public BICubeColumnEntity(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        this.discovery = discovery;
        this.currentLocation = currentLocation;
        indexDataService = new BICubeIndexData(discovery, currentLocation);
        cubeVersion = new BICubeVersion(currentLocation, discovery);
        cubeColumnPositionOfGroupService = new BICubeColumnPositionOfGroupService(currentLocation, discovery);
        initial();
    }

    protected abstract void initial();

    @Override
    public void setRelationManagerService(ICubeRelationManagerService relationManagerService) {
        this.relationManagerService = relationManagerService;
    }

    @Override
    public Comparator<T> getGroupComparator() {
        return groupDataService.getGroupComparator();
    }

    public void setGroupComparator(Comparator groupComparator) {
        groupDataService.setGroupComparator(groupComparator);
    }


    @Override
    public void addOriginalDataValue(int rowNumber, T originalValue) {
        detailDataService.addDetailDataValue(rowNumber, originalValue);
    }

    @Override
    public void addGroupValue(int position, T groupValue) {
        groupDataService.addGroupDataValue(position, groupValue);
    }

    @Override
    public void addGroupIndex(int position, GroupValueIndex index) {
        indexDataService.addIndex(position, index);
    }

    @Override
    public void addPositionOfGroup(int position, Integer groupPosition) {
        cubeColumnPositionOfGroupService.addPositionOfGroup(position, groupPosition);
    }

    @Override
    public int getPositionOfGroupByGroupValue(T groupValues) throws BIResourceInvalidException {

        return groupDataService.getPositionOfGroupValue(convert(groupValues));
    }

    @Override
    public int getPositionOfGroupByRow(int row) throws BIResourceInvalidException {
        return cubeColumnPositionOfGroupService.getPositionOfGroup(row);
    }

    private T convert(Object value) {
        if (BITypeUtils.isAssignable(Long.class, value.getClass()) &&
                getClassType() == DBConstant.CLASS.DOUBLE) {
            return convertDouble(value);
        } else if (BITypeUtils.isAssignable(Double.class, value.getClass()) &&
                getClassType() == DBConstant.CLASS.LONG) {
            return convertLong(value);
        }
        return (T) value;
    }

    private T convertLong(Object value) {
        return (T) BITypeUtils.convert2Long((Double) value);
    }

    private T convertDouble(Object value) {
        return (T) BITypeUtils.convert2Double((Long) value);
    }

    @Override
    public int sizeOfGroup() {
        return groupDataService.sizeOfGroup();
    }

    @Override
    public void recordSizeOfGroup(int size) {
        groupDataService.writeSizeOfGroup(size);
    }

    @Override
    public void copyDetailValue(ICubeColumnEntityService columnEntityService, long rowCount) {

    }

    @Override
    public GroupValueIndex getBitmapIndex(int position) throws BICubeIndexException {
        return indexDataService.getBitmapIndex(position);
    }

    @Override
    public GroupValueIndex getNULLIndex(int position) throws BICubeIndexException {
        return indexDataService.getNULLIndex(position);
    }

    @Override
    public GroupValueIndex getIndexByGroupValue(T groupValues) throws BIResourceInvalidException, BICubeIndexException {
        int position = getPositionOfGroupByGroupValue(groupValues);
        if (position >= 0) {
            return getBitmapIndex(position);
        }
        return new RoaringGroupValueIndex();
    }


    @Override
    public void addNULLIndex(int position, GroupValueIndex groupValueIndex) {
        indexDataService.addNULLIndex(position, groupValueIndex);
    }

    public long getCubeVersion() {
        return cubeVersion.getCubeVersion();
    }

    @Override
    public void addVersion(long version) {
        cubeVersion.addVersion(version);
    }

    @Override
    public T getGroupObjectValue(int position) {
        return groupDataService.getGroupObjectValueByPosition(position);
    }

    @Override
    public CubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, IllegalRelationPathException {
        if (path.isEmptyPath()) {
            /**
             * 如果路径是空的，说明没有关联
             * 那么返回自身的Index。
             */
            return new BICubeRelationEntity(indexDataService);
        }
        return relationManagerService.getRelationService(path);
    }

    @Override
    public boolean existRelationPath(BICubeTablePath path) {
        try {
            return !getRelationIndexGetter(path).isEmpty();
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void clear() {
        detailDataService.clear();
        indexDataService.clear();
        groupDataService.clear();
        cubeVersion.clear();
        cubeColumnPositionOfGroupService.clear();
    }

    @Override
    public boolean isEmpty() {
        return indexDataService.isEmpty();
    }

    @Override
    public int getClassType() {
        return detailDataService.getClassType();
    }

    @Override
    public ICubeResourceLocation getResourceLocation() {
        return currentLocation.copy();
    }

    @Override
    public void setOwner(ITableKey owner) {
        relationManagerService.setOwner(owner);
    }

    @Override
    public Boolean isVersionAvailable() {
        return cubeVersion.isVersionAvailable();
    }
}
