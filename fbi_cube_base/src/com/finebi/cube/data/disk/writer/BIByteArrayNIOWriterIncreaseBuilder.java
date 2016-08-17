package com.finebi.cube.data.disk.writer;


import com.finebi.cube.BICubeFileConstant;
import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.BICubeDiskPrimitiveDiscovery;
import com.finebi.cube.data.input.primitive.ICubeIntegerReader;
import com.finebi.cube.data.input.primitive.ICubeLongReader;
import com.finebi.cube.data.output.ICubeByteArrayWriter;
import com.finebi.cube.data.output.primitive.ICubeByteWriter;
import com.finebi.cube.data.output.primitive.ICubeIntegerWriter;
import com.finebi.cube.data.output.primitive.ICubeLongWriter;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;

/**
 * This class created on 2016/8/6.
 *
 * @author kary
 * @since 4.0
 */
public class BIByteArrayNIOWriterIncreaseBuilder extends BIByteArrayNIOWriterBuilder {



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

            BIByteArrayNIOIncreaseWriter writer = new BIByteArrayNIOIncreaseWriter(positionWriter, lengthWriter, contentWriter);
            writer.setPos(lastPosition(cubeDiscovery, location) + lastLength(cubeDiscovery, location));
            return writer;
        } catch (Exception ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }

    private long lastPosition(ICubePrimitiveResourceDiscovery cubeDiscovery, ICubeResourceLocation location) {
        try {
            /**
             * 获得位置部分的Long类型Writer
             */
            ICubeResourceLocation startPositionPath = location.generateWithSuffix(BICubeFileConstant.arrayPositionSuffix);
            startPositionPath.setReaderSourceLocation();
            startPositionPath.setLongType();
            ICubeLongReader reader = (ICubeLongReader) cubeDiscovery.getCubeReader(startPositionPath);
            if (reader.canReader()) {
                int position = 0;
                while (reader.getSpecificValue(++position) != 0) {

                }
                return reader.getSpecificValue(--position);
            } else {
                return 0;
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private int lastLength(ICubePrimitiveResourceDiscovery cubeDiscovery, ICubeResourceLocation location) {
        try {
            /**
             * 获得长度部分的Integer类型Writer
             */
            ICubeResourceLocation lengthPath = location.generateWithSuffix(BICubeFileConstant.arrayLengthSuffix);
            lengthPath.setReaderSourceLocation();
            lengthPath.setIntegerType();
            ICubeIntegerReader reader = (ICubeIntegerReader) cubeDiscovery.getCubeReader(lengthPath);

            if (reader.canReader()) {
                int position = 0;
                while (reader.getSpecificValue(++position) != 0) {
                }
                return reader.getSpecificValue(--position);
            } else {
                return 0;
            }
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }

    }
}