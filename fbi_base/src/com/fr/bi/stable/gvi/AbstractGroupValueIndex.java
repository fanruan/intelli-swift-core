package com.fr.bi.stable.gvi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by 小灰灰 on 14-1-7.
 */
public abstract class AbstractGroupValueIndex implements GroupValueIndex {

    /**
     *
     */
    private static final long serialVersionUID = -4380300967976180163L;

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream baos = null;
//        GZIPOutputStream gz = null;
        DataOutputStream oos = null;
        byte[] bytes = null;
        try {
            baos = new ByteArrayOutputStream();
//            gz = new GZIPOutputStream(baos);
            oos = new DataOutputStream(baos);
            oos.writeByte(this.getType());
            this.write(oos);
            oos.flush();
            oos.close();
            bytes = baos.toByteArray();
        } catch (IOException e) {
        } finally {
        }
        return bytes;
    }

    protected abstract byte getType();

    @Override
    public String toString() {
        return rangeString(0, 1000);
    }

    /**
     * 返回表示start~end间的1的值的字符串，如 [1,3,4],方便调试。
     *
     * @param start 开始位置
     * @param end   结束位置(不包括)
     * @return 1的值的字符串
     */
    public String rangeString(int start, int end) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        int count = 0;
        for (int i = start; i < end; i++) {
            //只是调试用，返回最多100个，
            if (count > 100) {
                break;
            }
            if (isOneAt(i)) {
                if (count != 0) {
                    builder.append(',');
                }
                builder.append(i);
                count++;
            }
        }
        builder.append(']');
        return builder.toString();
    }

    @Override
	public GroupValueIndex clone(){
		return this;
    }
}