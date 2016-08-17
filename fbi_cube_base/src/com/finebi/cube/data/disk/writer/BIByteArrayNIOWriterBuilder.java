package com.finebi.cube.data.disk.writer;

import com.finebi.cube.BICubeFileConstant;
import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.output.ICubeByteArrayWriter;
import com.finebi.cube.data.output.ICubeByteArrayWriterBuilder;
import com.finebi.cube.data.output.primitive.ICubeByteWriter;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.data.output.primitive.ICubeLongWriter;
import com.finebi.cube.location.ICubeResourceLocation;

import java.io.File;

/**
 * This class created on 2016/3/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BIByteArrayNIOWriterBuilder extends BINIOWriterBuilder<ICubeByteArrayWriter> implements ICubeByteArrayWriterBuilder {


    @Override
    protected String getFragmentTag() {
        return this.FRAGMENT_TAG;
    }

    @Override
    protected ICubeByteArrayWriter createNIOWriter(File target, ICubeResourceLocation location) {
        try {
            ICubePrimitiveResourceDiscovery cubeDiscovery = BICubeDiskPrimitiveDiscovery.getInstance();
            /**
             * 获得内容部分的Byte类型Writer
             */
            ICubeResourceLocation contentLocation = location.copy();
            contentLocation.setWriterSourceLocation();
            contentLocation.setByteType();
            ICubeByteWriter contentWriter = (ICubeByteWriter) cubeDiscovery.getCubeWriter(contentLocation);
            /**
             * 获得位置部分的Long类型Writer
             */
            ICubeResourceLocation startPositionPath = location.generateWithSuffix(BICubeFileConstant.arrayPositionSuffix);
            startPositionPath.setWriterSourceLocation();
            startPositionPath.setLongType();
            ICubeLongWriter positionWriter = (ICubeLongWriter) cubeDiscovery.getCubeWriter(startPositionPath);
            /**
             * 获得长度部分的Integer类型Writer
             */
            ICubeResourceLocation lengthPath = location.generateWithSuffix(BICubeFileConstant.arrayLengthSuffix);
            lengthPath.setWriterSourceLocation();
            lengthPath.setIntegerType();
            ICubeIntegerWriter lengthWriter = (ICubeIntegerWriter) cubeDiscovery.getCubeWriter(lengthPath);

            return new BIByteArrayNIOWriter(positionWriter, lengthWriter, contentWriter);
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }
}
