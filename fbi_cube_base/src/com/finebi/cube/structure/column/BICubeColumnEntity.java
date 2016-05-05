package com.finebi.cube.structure.column;

import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.*;
import com.finebi.cube.structure.group.ICubeGroupDataService;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.utils.program.BINonValueUtils;

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
    protected ICubeRelationManagerService relationManagerService;

    public BICubeColumnEntity(ICubeResourceLocation currentLocation) {
        this.currentLocation = currentLocation;
        indexDataService = new BICubeIndexData(currentLocation);
        cubeVersion = new BICubeVersion(currentLocation);
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
    public int getPositionOfGroup(T groupValues) throws BIResourceInvalidException {
        return groupDataService.getPositionOfGroupValue(groupValues);
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
    public T getOriginalValueByRow(int rowNumber) {
        return detailDataService.getOriginalValueByRow(rowNumber);
    }

    @Override
    public GroupValueIndex getIndexByRow(int rowNumber) throws BIResourceInvalidException, BICubeIndexException {
        T value = getOriginalValueByRow(rowNumber);
        return getIndexByGroupValue(value);
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
        int position = getPositionOfGroup(groupValues);
        return getBitmapIndex(position);
    }

    @Override
    public int getVersion() {
        return cubeVersion.getVersion();
    }


    @Override
    public void addNULLIndex(int position, GroupValueIndex groupValueIndex) {
        indexDataService.addNULLIndex(position, groupValueIndex);
    }

    @Override
    public void addVersion(int version) {
        cubeVersion.addVersion(version);
    }

    @Override
    public T getGroupValue(int position) {
        return groupDataService.getGroupValueByPosition(position);
    }

    @Override
    public ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, IllegalRelationPathException {
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
    }

    @Override
    public boolean isEmpty() {
        return indexDataService.isEmpty();
    }
}
