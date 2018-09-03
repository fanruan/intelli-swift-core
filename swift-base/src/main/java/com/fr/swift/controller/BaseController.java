package com.fr.swift.controller;

/**
 * This class created on 2018/8/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class BaseController {

    //ServiceController
    public final static String SWIFT_SERVICE_START = "swift/service/start";
    public final static String SWIFT_SERVICE_STOP = "swift/service/stop";

    public final static String SERVER_SERVICE_START = "server/service/start";
    public final static String SERVER_SERVICE_STOP = "server/service/stop";

    //RealtimeController
    public final static String REALTIME_INSERT = "realtime/insert/{tableName}";

}

