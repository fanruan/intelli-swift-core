package com.finebi.cube.data;

import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;

/**
 * This class created on 2016/5/3.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeSourceReleaseManager {
    void release(ICubePrimitiveWriter writer);

    void release(ICubePrimitiveReader reader);

}
