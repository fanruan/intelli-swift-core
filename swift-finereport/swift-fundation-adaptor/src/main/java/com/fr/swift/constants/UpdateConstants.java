package com.fr.swift.constants;

/**
 * This class created on 2018/5/15
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class UpdateConstants {

    public static final String GLOBAL_KEY = "SWIFT_GLOBAL_UPDATE_SETTING_KEY";

    public static class TableUpdateType {
        public static final int ALL = 1;
        public static final int INCREMENT = 2;
        public static final int NEVER = 3;
    }

    public static class PackageUpdateType {
        public static final int UPDATE = 1;
        public static final int NOT_UPDATE = 2;
    }

}
