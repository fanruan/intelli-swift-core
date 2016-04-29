package com.finebi.cube.data.disk.reader;

import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.data.input.ICubeReaderBuilder;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.general.ComparatorUtils;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIBasicNIOReaderBuilder<T> implements ICubeReaderBuilder<T> {
    @Override
    public T buildReader(ICubeResourceLocation resourceLocation) throws BIBuildReaderException {
        String path = resourceLocation.getAbsolutePath();
        String query = resourceLocation.getQuery();
        String fragment = resourceLocation.getFragment();
        if (ComparatorUtils.equals(query, ICubeReaderBuilder.QUERY_TAG) &&
                ComparatorUtils.equals(fragment, getFragmentTag())) {
            File target = new File(path);
            return createNIOReader(target, resourceLocation);
        } else {
            throw new BIBuildReaderException("please check the Resource location:" + resourceLocation.toString());
        }
    }

    protected abstract String getFragmentTag();

    protected abstract T createNIOReader(File target, ICubeResourceLocation targetLocation);
}
