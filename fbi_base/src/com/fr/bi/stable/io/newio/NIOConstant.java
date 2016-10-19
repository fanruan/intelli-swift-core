package com.fr.bi.stable.io.newio;


public class NIOConstant {

    public static long PAGE_STEP = 22L;

    public static long PAGE_SIZE = 1 << PAGE_STEP;

    public static long MAX_SINGLE_FILE_PART_SIZE = 8L;

    public static long MINFILE_SIZE = 12L;

    public static long MAX_SINGLE_FILE_PART_MOVE_ALL = PAGE_STEP + MAX_SINGLE_FILE_PART_SIZE;

    public static final class DOUBLE {

        private static final long OFF_SET = 0x3;

        public static final long PAGE_STEP = NIOConstant.PAGE_STEP - OFF_SET;

        private static final long PAGE_SIZE = 1L << PAGE_STEP;

        public static final long PAGE_MODE_TO_AND_WRITE_VALUE = PAGE_SIZE - 1;

        public static final long PAGE_MODE_TO_AND_READ_VALUE = (PAGE_SIZE << MAX_SINGLE_FILE_PART_SIZE) - 1;

        public static final double NULL_VALUE = Double.NaN;
    }

    public static final class INTEGER {

        private static final long OFF_SET = 0x2;

        public static final long PAGE_STEP = NIOConstant.PAGE_STEP - OFF_SET;

        private static final long PAGE_SIZE = 1L << PAGE_STEP;

        public static final long PAGE_MODE_TO_AND_WRITE_VALUE = PAGE_SIZE - 1;

        public static final long PAGE_MODE_TO_AND_READ_VALUE = (PAGE_SIZE << MAX_SINGLE_FILE_PART_SIZE) - 1;

        public static final int NULL_VALUE = Integer.MIN_VALUE;

    }

    public static final class LONG {

        private static final long OFF_SET = 0x3;

        public static final long PAGE_STEP = NIOConstant.PAGE_STEP - OFF_SET;

        private static final long PAGE_SIZE = 1L << PAGE_STEP;

        public static final long PAGE_MODE_TO_AND_WRITE_VALUE = PAGE_SIZE - 1;

        public static final long PAGE_MODE_TO_AND_READ_VALUE = (PAGE_SIZE << MAX_SINGLE_FILE_PART_SIZE) - 1;

        public static final long NULL_VALUE = Long.MIN_VALUE;
    }

    public static final class BYTE {

        private static final long OFF_SET = 0x0;

        public static final long PAGE_STEP = NIOConstant.PAGE_STEP - OFF_SET;

        private static final long PAGE_SIZE = 1L << PAGE_STEP;

        public static final long PAGE_MODE_TO_AND_WRITE_VALUE = PAGE_SIZE - 1;

        public static final long PAGE_MODE_TO_AND_READ_VALUE = (PAGE_SIZE << MAX_SINGLE_FILE_PART_SIZE) - 1;

    }

}