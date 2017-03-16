package com.fr.bi.stable.gvi;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by 小灰灰 on 2016/11/7.
 */
public class BIByteDataInputTest extends TestCase {
    public void testByteArray(){
        byte[] b = new byte[100];
        for (int i = 0; i < b.length; i++){
            b[i] = (byte)(Math.random()*100);
        }
        ByteArrayDataInput input = new ByteArrayDataInput(b);
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(b));
        for (int i = 0; i < b.length; i++){
            try {
                assertEquals(input.readByte(), stream.readByte());
            } catch (IOException e) {
                assert(false);
            }
        }
    }
}
