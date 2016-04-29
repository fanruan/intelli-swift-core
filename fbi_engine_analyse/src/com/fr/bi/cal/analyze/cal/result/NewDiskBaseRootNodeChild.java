/**
 *
 */
package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.io.write.ParseBytes;
import com.fr.bi.stable.report.result.DimensionCalculator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;


public class NewDiskBaseRootNodeChild extends NewRootNodeChild implements ParseBytes {

    /**
     *
     */
    private static final long serialVersionUID = 8705167044403539937L;

    public NewDiskBaseRootNodeChild(DimensionCalculator key, Object data) {
        super(key, data);
    }

    /**
     * @param key
     * @param data
     */
    public NewDiskBaseRootNodeChild(DimensionCalculator key, Object data, GroupValueIndex gvi) {
        super(key, data, gvi);
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.io.write.ParseBytes#getBytes()
     */
    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gz = null;
        ObjectOutputStream oos = null;
        byte[] bytes = null;
        try {
            baos = new ByteArrayOutputStream();
            gz = new GZIPOutputStream(baos);
            oos = new ObjectOutputStream(gz);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
        }
        return bytes;
    }
}