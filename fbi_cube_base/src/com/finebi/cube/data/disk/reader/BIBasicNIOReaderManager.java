package com.finebi.cube.data.disk.reader;

import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.data.input.ICubeReaderBuilder;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.general.ComparatorUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/3/16.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBasicNIOReaderManager<T> {
    protected Map<String, BIBasicNIOReaderBuilder> tag2Builder;

    public BIBasicNIOReaderManager() {
        tag2Builder = new HashMap<String, BIBasicNIOReaderBuilder>();
    }

    public T buildCubeReader(ICubeResourceLocation resourceLocation)
            throws IllegalCubeResourceLocationException, BIBuildReaderException {
        if (ComparatorUtils.equals(resourceLocation.getQuery(), ICubeReaderBuilder.QUERY_TAG)) {
            BINonValueUtils.checkNull(resourceLocation.getFragment());
            if (tag2Builder.containsKey(resourceLocation.getFragment())) {
                return (T) tag2Builder.get(resourceLocation.getFragment()).buildReader(resourceLocation);
            } else {
                throw new IllegalCubeResourceLocationException();
            }
        } else {
            throw new IllegalCubeResourceLocationException();
        }
    }
}
