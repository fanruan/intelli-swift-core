package com.finebi.cube.structure.column.date;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ICubeRelationEntityGetterService;
import com.finebi.cube.structure.ICubeRelationManagerService;
import com.finebi.cube.structure.column.BICubeColumnEntity;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeDateSubColumn<T> implements ICubeColumnEntityService<T> {
    protected BICubeDateColumn hostDataColumn;
    protected BICubeColumnEntity<T> columnEntity;
    protected ICubeResourceDiscovery discovery;

    public BICubeDateSubColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        this.discovery = discovery;
        initialColumnEntity(currentLocation);
        this.hostDataColumn = hostDataColumn;
    }

    protected abstract void initialColumnEntity( ICubeResourceLocation currentLocation);

    @Override
    public void addOriginalDataValue(int rowNumber, T originalValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRelationManagerService(ICubeRelationManagerService relationManagerService) {
        columnEntity.setRelationManagerService(relationManagerService);
    }

    @Override
    public Comparator<T> getGroupComparator() {
        return columnEntity.getGroupComparator();
    }


    @Override
    public void addGroupValue(int position, T groupValue) {
        columnEntity.addGroupValue(position, groupValue);
    }

    @Override
    public void addGroupIndex(int position, GroupValueIndex index) {
        columnEntity.addGroupIndex(position, index);
    }

    @Override
    public void recordSizeOfGroup(int size) {
        columnEntity.recordSizeOfGroup(size);
    }

    @Override
    public int getVersion() {
        return columnEntity.getVersion();
    }

    @Override
    public void addVersion(int version) {
        columnEntity.addVersion(version);
    }

    @Override
    public void addNULLIndex(int position, GroupValueIndex groupValueIndex) {
        columnEntity.addNULLIndex(position, groupValueIndex);
    }

    @Override
    public void copyDetailValue(ICubeColumnEntityService columnEntityService, long rowCount) {
        columnEntity.copyDetailValue(columnEntityService, rowCount);
    }

    @Override
    public int getPositionOfGroup(T groupValues) throws BIResourceInvalidException {
        return columnEntity.getPositionOfGroup(groupValues);
    }

    @Override
    public int sizeOfGroup() {
        return columnEntity.sizeOfGroup();
    }

    @Override
    public GroupValueIndex getIndexByRow(int rowNumber) throws BIResourceInvalidException, BICubeIndexException {
        return columnEntity.getIndexByRow(rowNumber);
    }

    @Override
    public T getGroupValue(int position) {
        return columnEntity.getGroupValue(position);
    }

    @Override
    public ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, IllegalRelationPathException {
        return columnEntity.getRelationIndexGetter(path);
    }

    @Override
    public GroupValueIndex getBitmapIndex(int position) throws BICubeIndexException {
        return columnEntity.getBitmapIndex(position);
    }

    @Override
    public GroupValueIndex getNULLIndex(int position) throws BICubeIndexException {
        return columnEntity.getNULLIndex(position);
    }

    @Override
    public void clear() {
        columnEntity.clear();
    }

    @Override
    public T getOriginalValueByRow(int rowNumber) {
        return convertDate(hostDataColumn.getOriginalValueByRow(rowNumber));
    }

    protected abstract T convertDate(Long date);

    @Override
    public GroupValueIndex getIndexByGroupValue(T groupValues) throws BIResourceInvalidException, BICubeIndexException {
        return columnEntity.getIndexByGroupValue(groupValues);
    }

    @Override
    public boolean existRelationPath(BICubeTablePath path) {
        return columnEntity.existRelationPath(path);
    }

    @Override
    public boolean isEmpty() {
        return columnEntity.isEmpty();
    }
}
