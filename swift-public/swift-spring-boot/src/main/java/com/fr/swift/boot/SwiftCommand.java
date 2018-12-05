package com.fr.swift.boot;


import com.fr.swift.util.Util;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
class SwiftCommand {
    public static final String START_SWIFT_SERVICE = "-ServiceStart";

    public static final String START_ALL_SWIFT_SERVICE = "-ServiceStartAll";

    public static final String START_SERVER_SERVICE = "-ServerStart";

    public static boolean matchCommand(String command) {
        return Util.equals(command, START_SWIFT_SERVICE) ||
                Util.equals(command, START_ALL_SWIFT_SERVICE) ||
                Util.equals(command, START_SERVER_SERVICE);
    }
}
