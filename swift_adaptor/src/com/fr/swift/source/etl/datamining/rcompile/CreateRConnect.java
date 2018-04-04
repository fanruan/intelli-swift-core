package com.fr.swift.source.etl.datamining.rcompile;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * Created by Handsome on 2018/3/28 0028 14:45
 */
public class CreateRConnect {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(CreateRConnect.class);

    public static RConnection conn;

    public CreateRConnect(String host, int port) {
        try {
            conn = new RConnection(host, port);
        } catch(RserveException e) {
            LOGGER.error("Connecting Rserve failed", e);
        }
    }

    public CreateRConnect(String host) {
        try {
            conn = new RConnection(host);
        } catch(RserveException e) {
            LOGGER.error("Connecting Rserve failed", e);
        }
    }

    public CreateRConnect() {
        try {
            conn = new RConnection();
        } catch(RserveException e) {
            LOGGER.error("Connecting Rserve failed", e);
        }
    }

    public RConnection getConnection() {
        if(null == conn) {
            new CreateRConnect();
        }
        return conn;
    }
}
