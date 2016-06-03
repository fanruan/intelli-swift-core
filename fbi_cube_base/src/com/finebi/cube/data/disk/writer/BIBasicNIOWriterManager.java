package com.finebi.cube.data.disk.writer;

import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.data.output.ICubeWriterBuilder;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.ComparatorUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class created on 2016/3/16.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBasicNIOWriterManager<T> {
    protected Map<String, BIBasicNIOWriterBuilder> tag2Builder;

    public BIBasicNIOWriterManager() {
        tag2Builder = new HashMap<String, BIBasicNIOWriterBuilder>();

    }

    public T buildCubeWriter(ICubeResourceLocation resourceLocation)
            throws IllegalCubeResourceLocationException, BIBuildWriterException {
        if (ComparatorUtils.equals(resourceLocation.getQuery(), ICubeWriterBuilder.QUERY_TAG)) {
            BINonValueUtils.checkNull(resourceLocation.getFragment());
            if (tag2Builder.containsKey(resourceLocation.getFragment())) {
                return (T) tag2Builder.get(resourceLocation.getFragment()).buildWriter(resourceLocation);
            } else {
                 throw new IllegalCubeResourceLocationException(BIStringUtils.appendWithSpace("Please check location fragment:",
                        resourceLocation.getFragment(), "which should be wrong fragment tag"));
            }
        } else {
            throw new IllegalCubeResourceLocationException(BIStringUtils.appendWithSpace("Please check location Query:",
                    resourceLocation.getQuery(), "which should be WRITE flag"));
        }
    }
}
