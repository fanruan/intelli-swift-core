package com.fr.swift.server;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.property.SwiftProperty;

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

    public static Map<String, Set<String>> parseCommand(String[] args) {
        int commandLength = args.length;
        if (commandLength == 0) {
            return new HashMap<String, Set<String>>();
        }
        Map<String, Set<String>> cmdMap = new HashMap<String, Set<String>>();
        Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
        String command = null;
        for (int i = 0; i < commandLength; i++) {
            String arg = args[i];
            if (SwiftCommand.matchCommand(arg)) {
                command = arg;
                cmdMap.put(command, new HashSet<String>());
            } else {
                if (command != null) {
                    cmdMap.get(command).add(arg);
                }
            }
        }
        if (cmdMap.containsKey(SwiftCommand.START_SWIFT_SERVICE)) {
            resultMap.put(SwiftCommand.START_SWIFT_SERVICE, cmdMap.get(SwiftCommand.START_SWIFT_SERVICE));
        }
        if (cmdMap.containsKey(SwiftCommand.START_ALL_SWIFT_SERVICE)) {
            resultMap.put(SwiftCommand.START_SWIFT_SERVICE, getAllSwiftService());
        }
        if (cmdMap.containsKey(SwiftCommand.START_SERVER_SERVICE)) {
            resultMap.put(SwiftCommand.START_SERVER_SERVICE, cmdMap.get(SwiftCommand.START_SERVER_SERVICE));
        }

        if (resultMap.containsKey(SwiftCommand.START_SWIFT_SERVICE)) {
            SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
            Set<String> serviceSet = resultMap.get(SwiftCommand.START_SWIFT_SERVICE);
            String[] serviceArrays = serviceSet.toArray(new String[serviceSet.size()]);
            swiftProperty.setSwiftServiceList(serviceArrays);
        }
        if (resultMap.containsKey(SwiftCommand.START_SERVER_SERVICE)) {
            SwiftProperty swiftProperty = SwiftContext.get().getBean(SwiftProperty.class);
            Set<String> serviceSet = resultMap.get(SwiftCommand.START_SERVER_SERVICE);
            String[] serviceArrays = serviceSet.toArray(new String[serviceSet.size()]);
            swiftProperty.setServerServiceList(serviceArrays);
        }
        return resultMap;
    }

    //todo 注解获得service
    private static Set<String> getAllSwiftService() {
        Set<String> allSwiftService = new HashSet<String>() {{
            add("history");
            add("realtime");
            add("analyse");
            add("collate");
            add("indexing");
        }};
        return allSwiftService;
    }

}
