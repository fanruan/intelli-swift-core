package com.fr.swift.boot;

import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.ServiceBeanFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class created on 2018/8/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
class SwiftCommandParser {

    public static void parseCommand(String[] args) {
        int commandLength = args.length;
        if (commandLength == 0) {
            return;
        }
        Map<String, Set<String>> parseMap = new HashMap<String, Set<String>>();
        Map<String, Set<String>> commandMap = new HashMap<String, Set<String>>();
        String command = null;
        for (int i = 0; i < commandLength; i++) {
            String arg = args[i];
            if (SwiftCommand.matchCommand(arg)) {
                command = arg;
                parseMap.put(command, new HashSet<String>());
            } else {
                if (command != null) {
                    parseMap.get(command).add(arg);
                }
            }
        }
        if (parseMap.containsKey(SwiftCommand.START_SWIFT_SERVICE)) {
            commandMap.put(SwiftCommand.START_SWIFT_SERVICE, parseMap.get(SwiftCommand.START_SWIFT_SERVICE));
        }
        if (parseMap.containsKey(SwiftCommand.START_ALL_SWIFT_SERVICE)) {
            commandMap.put(SwiftCommand.START_SWIFT_SERVICE, getAllSwiftService());
        }
        if (parseMap.containsKey(SwiftCommand.START_SERVER_SERVICE)) {
            commandMap.put(SwiftCommand.START_SERVER_SERVICE, parseMap.get(SwiftCommand.START_SERVER_SERVICE));
        }

        if (commandMap.containsKey(SwiftCommand.START_SWIFT_SERVICE)) {
            SwiftProperty swiftProperty = SwiftProperty.getProperty();
            Set<String> serviceSet = commandMap.get(SwiftCommand.START_SWIFT_SERVICE);
            swiftProperty.setSwiftServiceNames(serviceSet);
        }
        if (commandMap.containsKey(SwiftCommand.START_SERVER_SERVICE)) {
            SwiftProperty swiftProperty = SwiftProperty.getProperty();
            Set<String> serviceSet = commandMap.get(SwiftCommand.START_SERVER_SERVICE);
            swiftProperty.setServerServiceNames(serviceSet);
        }
    }

    private static Set<String> getAllSwiftService() {
        return new HashSet<String>(ServiceBeanFactory.getAllSwiftServiceNames());
    }

}
