package com.finebi.cube.structure.column.date;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.column.BICubeLongColumn;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2016/3/30.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeDateColumn extends BICubeLongColumn {
    private Set<BICubeDateSubColumn> subColumns;


    public void addSubColumns(BICubeDateSubColumn subColumn) {
        subColumns.add(subColumn);
    }

    public BICubeDateColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        super(discovery, currentLocation);
        subColumns = new HashSet<BICubeDateSubColumn>();
    }

    @Override
    public void addGroupValue(int position, Long groupValue) {
        super.addGroupValue(position, groupValue);
    }

    @Override
    public void addGroupIndex(int position, GroupValueIndex index) {
        super.addGroupIndex(position, index);
    }

    @Override
    public void addNULLIndex(int position, GroupValueIndex groupValueIndex) {
        super.addNULLIndex(position, groupValueIndex);
    }

    @Override
    public void addVersion(int version) {
        super.addVersion(version);
    }

}
