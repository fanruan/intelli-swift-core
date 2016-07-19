package com.finebi.cube.structure.table;

import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.CubeRelationEntityGetterService;
import com.finebi.cube.structure.CubeTableEntityService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class created on 2016/5/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeOccupiedTable implements CubeTableEntityService {
    @Override
    public void recordTableStructure(List<ICubeFieldSource> fields) {

    }



    @Override
    public void recordRowCount(long rowCount) {

    }

    @Override
    public void recordLastTime() {

    }

    @Override
    public void recordRemovedLine(TreeSet<Integer> removedLine) {

    }

    @Override
    public void addDataValue(BIDataValue originalDataValue) throws BICubeColumnAbsentException {

    }

    @Override
    public boolean checkRelationVersion(List<BITableSourceRelation> relations, int relation_version) {
        return false;
    }

    @Override
    public boolean checkRelationVersion(BIKey key, List<BITableSourceRelation> relations, int relation_version) {
        return false;
    }

    @Override
    public boolean checkCubeVersion() {
        return false;
    }

    @Override
    public void copyDetailValue(CubeTableEntityService cube, long rowCount) {

    }

    @Override
    public void recordParentsTable(List<ITableKey> parents) {

    }

    @Override
    public List<ITableKey> getParentsTable() {
        return null;
    }


    @Override
    public List<ICubeFieldSource> getFieldInfo() {
        return null;
    }

    @Override
    public Set<BIColumnKey> getCubeColumnInfo() {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public IntList getRemovedList() {
        return null;
    }

    @Override
    public ICubeFieldSource getSpecificColumn(String fieldName) throws BICubeColumnAbsentException {
        return null;
    }

    @Override
    public Date getCubeLastTime() {
        return null;
    }

    @Override
    public CubeColumnReaderService getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException {
        return null;
    }

    @Override
    public CubeColumnReaderService getColumnDataGetter(String columnName) throws BICubeColumnAbsentException {
        return null;
    }

    @Override
    public CubeRelationEntityGetterService getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException {
        return null;
    }

    @Override
    public boolean tableDataAvailable() {
        return false;
    }

    @Override
    public boolean isRowCountAvailable() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void recordFieldNamesFromParent(Set<String> fieldNames) {

    }

    @Override
    public Set<String> getFieldNamesFromParent() {
        return null;
    }

    public long getCubeVersion() {
        return 0;
    }

    @Override
    public void addVersion(long version) {

    }

    @Override
    public void setTableOwner(ITableKey owner) {

    }

    @Override
    public boolean isRemovedListAvailable() {
        return false;
    }

    @Override
    public Boolean isVersionAvailable() {
        return false;
    }
}
