package com.finebi.cube.security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CheckedInputStream;


/**
 * This class created on 2016/7/31.
 *
 * @author Connery
 * @since 4.0
 */
public class SumCRC32 {

    public static long calculate(String fileName) {

        try {

            CheckedInputStream cis = null;
            long fileSize = 0;
            try {
                cis = new CheckedInputStream(
                        new FileInputStream(fileName), new java.util.zip.CRC32());

//                fileSize = new File(fileName).length();

            } catch (FileNotFoundException e) {
                System.err.println("File not found.");
                System.exit(1);
            }

            byte[] buf = new byte[128];
            while (cis.read(buf) >= 0) {
            }

            long checksum = cis.getChecksum().getValue();
//            System.out.println(checksum + " " + fileSize + " " + fileName);
            return checksum;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}