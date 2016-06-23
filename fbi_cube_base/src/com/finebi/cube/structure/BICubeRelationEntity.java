package com.finebi.cube.structure;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.property.BICubeVersion;
import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * This class created on 2016/3/3.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelationEntity implements ICubeRelationEntityService {


    private ICubeIndexDataService indexDataService;
    private ICubeResourceDiscovery discovery;
    private ICubeVersion version;

    public BICubeRelationEntity(ICubeResourceDiscovery discovery, ICubeResourceLocation cubeResourceLocation) {
        this.discovery = discovery;
        indexDataService = new BICubeIndexData(this.discovery, cubeResourceLocation);
        version = new BICubeVersion(cubeResourceLocation, discovery);
    }

    public BICubeRelationEntity(ICubeIndexDataService indexDataService) {
        this.indexDataService = indexDataService;
    }

    public void setIndexDataService(ICubeIndexDataService indexDataService) {
        this.indexDataService = indexDataService;
    }

    @Override
    public void addRelationIndex(int position, GroupValueIndex groupValueIndex) {
        indexDataService.addIndex(position, groupValueIndex);
    }


    @Override
    public void addRelationNULLIndex(int position, GroupValueIndex groupValueIndex) {
        indexDataService.addNULLIndex(position, groupValueIndex);
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
    public void clear() {
        indexDataService.clear();
        version.clear();
    }

    @Override
    public boolean isEmpty() {
        return indexDataService.isEmpty();
    }

    public long getCubeVersion() {
        return version.getCubeVersion();
    }

    @Override
    public void addVersion(long version) {
        this.version.addVersion(version);
    }

    @Override
    public ICubeResourceLocation getResourceLocation() {
        return indexDataService.getResourceLocation();
    }

    @Override
    public Boolean isVersionAvailable() {
        return version.isVersionAvailable();
    }
}

