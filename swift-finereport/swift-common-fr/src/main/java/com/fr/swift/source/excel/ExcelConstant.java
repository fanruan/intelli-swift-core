package com.fr.swift.source.excel;

import java.io.File;

/**
 * Excel常量静态值类
 *
 * @author Daniel
 */
public class ExcelConstant {
    public static final int PREVIEW_COUNT = 200;

    public final static String EXCEL_DATA_PATH = File.separator + "assets" + File.separator + "excel_data";

    public static final String ILLEGAL_REG = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\\s]";

    public static final String NUMBER_REG = "^[+-]?([1-9][0-9]*|0)(\\.[0-9]+)?%?$";

    public static final class CSV_CODE_TYPE {
        public static final int UTF8 = 0xefbb;
        public static final int UNICODE = 0xfffe;
        public static final int UTF16BE = 0xfeff;
        public static final int ANSIORASCII = 0x5c75;
    }
}