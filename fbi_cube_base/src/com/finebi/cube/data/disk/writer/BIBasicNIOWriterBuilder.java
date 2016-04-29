package com.finebi.cube.data.disk.writer;

import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.data.output.ICubeWriterBuilder;
import com.fr.general.ComparatorUtils;

import java.io.File;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIBasicNIOWriterBuilder<T> implements ICubeWriterBuilder<T> {
    @Override
    public T buildWriter(ICubeResourceLocation resourceLocation) throws BIBuildWriterException {
        String path = resourceLocation.getAbsolutePath();
        String query = resourceLocation.getQuery();
        String fragment = resourceLocation.getFragment();
        if (ComparatorUtils.equals(query, ICubeWriterBuilder.QUERY_TAG) &&
                ComparatorUtils.equals(fragment, getFragmentTag())) {
            File target = new File(path);
            return createNIOWriter(target, resourceLocation);
        } else {
            throw new BIBuildWriterException("please check the Resource location:" + resourceLocation.toString());
        }
    }

    protected abstract String getFragmentTag();

    protected abstract T createNIOWriter(File target, ICubeResourceLocation location);
}
