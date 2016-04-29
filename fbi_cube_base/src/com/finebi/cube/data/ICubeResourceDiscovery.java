package com.finebi.cube.data;

import com.finebi.cube.data.input.ICubeReader;
import com.finebi.cube.data.output.ICubeWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;

/**
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeResourceDiscovery {
    ICubeReader getCubeReader(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildReaderException;

    ICubeWriter getCubeWriter(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildWriterException;

}
