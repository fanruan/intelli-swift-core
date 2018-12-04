package com.fr.swift.beans.factory.classreading.constant;

import com.fr.swift.beans.factory.classreading.ConstantInfo;
import com.fr.swift.beans.factory.classreading.basic.reader.IntReader;
import com.fr.swift.log.SwiftLoggers;

import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ConstantUtf8 extends ConstantInfo<Integer> {

    private String value;

    @Override
    public void read(InputStream inputStream) {
        int length = IntReader.read(inputStream);
        byte[] bytes = new byte[length];
        try {
            inputStream.read(bytes);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
        try {
            value = readUtf8(bytes);
        } catch (UTFDataFormatException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    public String getValue() {
        return value;
    }

    private String readUtf8(byte[] bytearr) throws UTFDataFormatException {
        int c, char2, char3;
        int count = 0;
        int chararr_count = 0;
        char[] chararr = new char[bytearr.length];

        while (count < bytearr.length) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++] = (char) c;
        }

        while (count < bytearr.length) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    count++;
                    chararr[chararr_count++] = (char) c;
                    break;
                case 12:
                case 13:
                    count += 2;
                    if (count > bytearr.length)
                        throw new UTFDataFormatException(
                                "malformed input: partial character at end");
                    char2 = (int) bytearr[count - 1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException(
                                "malformed input around byte " + count);
                    chararr[chararr_count++] = (char) (((c & 0x1F) << 6) |
                            (char2 & 0x3F));
                    break;
                case 14:
                    count += 3;
                    if (count > bytearr.length)
                        throw new UTFDataFormatException(
                                "malformed input: partial character at end");
                    char2 = (int) bytearr[count - 2];
                    char3 = (int) bytearr[count - 1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException(
                                "malformed input around byte " + (count - 1));
                    chararr[chararr_count++] = (char) (((c & 0x0F) << 12) |
                            ((char2 & 0x3F) << 6) |
                            ((char3 & 0x3F) << 0));
                    break;
                default:
                    throw new UTFDataFormatException(
                            "malformed input around byte " + count);
            }
        }
        return new String(chararr, 0, chararr_count);
    }
}
