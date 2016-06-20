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
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BICubeColumnEntity;
import com.finebi.cube.structure.column.ICubeColumnEntityService;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * 日期子类。
 * 详细数据是保存在HostColumn中的，
 * 这里selfColumn主要存储自身的索引。
 * This class created on 2016/4/7.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BICubeDateSubColumn<T> implements ICubeColumnEntityService<T> {
    protected BICubeDateColumn hostDataColumn;
    protected BICubeColumnEntity<T> selfColumnEntity;
    protected ICubeResourceDiscovery discovery;

    public BICubeDateSubColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation, BICubeDateColumn hostDataColumn) {
        this.discovery = discovery;
        initialColumnEntity(currentLocation);
        this.hostDataColumn = hostDataColumn;
    }

    protected abstract void initialColumnEntity(ICubeResourceLocation currentLocation);

    @Override
    public void addOriginalDataValue(int rowNumber, T originalValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRelationManagerService(ICubeRelationManagerService relationManagerService) {
        selfColumnEntity.setRelationManagerService(relationManagerService);
    }

    @Override
    public Comparator<T> getGroupComparator() {
        return selfColumnEntity.getGroupComparator();
    }


    @Override
    public void addGroupValue(int position, T groupValue) {
        selfColumnEntity.addGroupValue(position, groupValue);
    }

    @Override
    public void addGroupIndex(int position, GroupValueIndex index) {
        selfColumnEntity.addGroupIndex(position, index);
    }

    @Override
    public void recordSizeOfGroup(int size) {
        selfColumnEntity.recordSizeOfGroup(size);
    }

    public long getCubeVersion() {
        return selfColumnEntity.getCubeVersion();
    }

    @Override
    public void addVersion(long version) {
        selfColumnEntity.addVersion(version);
    }

    @Override
    public void addNULLIndex(int position, GroupValueIndex groupValueIndex) {
        selfColumnEntity.addNULLIndex(position, groupValueIndex);
    }

    @Override
    public void copyDetailValue(ICubeColumnEntityService columnEntityService, long rowCount) {
        selfColumnEntity.copyDetailValue(columnEntityService, rowCount);
    }

    @Override
    public int getPositionOfGroup(T groupValues) throws BIResourceInvalidException {
        return selfColumnEntity.getPositionOfGroup(groupValues);
    }

    @Override
    public int sizeOfGroup() {
        return selfColumnEntity.sizeOfGroup();
    }

    @Override
    public GroupValueIndex getIndexByRow(int rowNumber) throws BIResourceInvalidException, BICubeIndexException {
        return selfColumnEntity.getIndexByRow(rowNumber);
    }

    @Override
    public T getGroupValue(int position) {
        return selfColumnEntity.getGroupValue(position);
    }

    @Override
    public ICubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, IllegalRelationPathException {
        return hostDataColumn.getRelationIndexGetter(path);
    }

    @Override
    public GroupValueIndex getBitmapIndex(int position) throws BICubeIndexException {
        return selfColumnEntity.getBitmapIndex(position);
    }

    @Override
    public GroupValueIndex getNULLIndex(int position) throws BICubeIndexException {
        return selfColumnEntity.getNULLIndex(position);
    }

    @Override
    public void clear() {
        selfColumnEntity.clear();
    }

    @Override
    public T getOriginalValueByRow(int rowNumber) {
        return convertDate(hostDataColumn.getOriginalValueByRow(rowNumber));
    }

    protected abstract T convertDate(Long date);

    @Override
    public GroupValueIndex getIndexByGroupValue(T groupValues) throws BIResourceInvalidException, BICubeIndexException {
        return selfColumnEntity.getIndexByGroupValue(groupValues);
    }

    @Override
    public void setOwner(ITableKey owner) {
        selfColumnEntity.setOwner(owner);
    }

    @Override
    public boolean existRelationPath(BICubeTablePath path) {
        return selfColumnEntity.existRelationPath(path);
    }

    @Override
    public boolean isEmpty() {
        return selfColumnEntity.isEmpty();
    }

    @Override
    public int getClassType() {
        return selfColumnEntity.getClassType();
    }

    @Override
    public ICubeResourceLocation getResourceLocation() {
        return selfColumnEntity.getResourceLocation();
    }
}
