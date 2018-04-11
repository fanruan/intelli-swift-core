package com.fr.swift.source.etl.datamining.rcompile;

/**
 * Created by Handsome on 2018/4/10 0010 15:40
 */

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Handsome on 2018/3/29 0010 15:34
 */

public class RConnector {

    private static final long START_ATTEMPT_SLEEP = 1000l;

    private static final int START_ATTEMP_COUNT = 5;

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RConnector.class);

    public RConnection getNewConnection(boolean enableBatchStart, String host, int port, String user, String password) throws RserveException {
        RConnection con = null;
        con = getNewConnection(enableBatchStart, host, port);
        if (con != null && con.needLogin()) {
            con.login(user, password);
        }
        return con;
    }

    public RConnection getNewConnection(boolean enableBatchStart) throws RserveException{
        RConnection con = null;
        try {
            con = new RConnection();
        }
        catch (RserveException rse) {
            LOGGER.debug("Could not connect to RServe: " + rse.getMessage());
            if (enableBatchStart) {
                LOGGER.info("Attempting to start RServe.");
                try {
                    con = attemptStarts();
                }
                catch (Exception e) {
                    LOGGER.error("Attempted to start Rserve and establish a connection failed", e);
                }
            }
        }
        return con;
    }

    public RConnection getNewConnection(boolean enableBatchStart, String host, int port) throws RserveException {
        LOGGER.debug("New connection using batch " + enableBatchStart + " at host:port" + host + ":" + port);
        RConnection con = null;
        try {
            con = newConnection(host, port);
        }
        catch (RserveException rse) {
            LOGGER.debug("Could not connect to RServe: " + rse.getMessage());
            if (rse.getMessage().startsWith("Cannot connect") && enableBatchStart) {
                LOGGER.info("Attempting to start RServe.");
                try {
                    con = attemptStarts(host, port);
                }
                catch (Exception e) {
                    LOGGER.error("Attempted to start Rserve and establish a connection failed", e);
                }
            }
            else
                throw rse;
        }

        return con;
    }

    private RConnection attemptStarts() throws InterruptedException, IOException, RserveException {
        startRserve();
        int attempt = 1;
        RConnection con = null;
        while (attempt <= START_ATTEMP_COUNT) {
            try {
                Thread.sleep(START_ATTEMPT_SLEEP); // wait for R to startup, then establish connection
                con = new RConnection();
                break;
            }
            catch (RserveException rse) {
                if (attempt >= 5) {
                    throw rse;
                }
                attempt++;
            }
        }
        return con;
    }

    private RConnection attemptStarts(String host, int port) throws InterruptedException, IOException, RserveException {
        startRserve();

        int attempt = 1;
        RConnection con = null;
        while (attempt <= START_ATTEMP_COUNT) {
            try {
                Thread.sleep(START_ATTEMPT_SLEEP); // wait for R to startup, then establish connection
                con = newConnection(host, port);
                break;
            }
            catch (RserveException rse) {
                if (attempt >= 5) {
                    throw rse;
                }

                attempt++;
            }
        }
        return con;
    }

    private static RConnection newConnection(String host, int port) throws RserveException {
        LOGGER.debug("Creating new RConnection");

        RConnection con;
        con = new RConnection(host, port);

        REXP info = con.eval("capture.output(sessionInfo())");
        try {
            LOGGER.info("NEW CONNECTION >>> sessionInfo:\n" + Arrays.deepToString(info.asStrings()));
        }
        catch (REXPMismatchException e) {
            LOGGER.warn("Error creating session info.", e);
        }
        return con;
    }

    private static void startRServeOnLinux() throws InterruptedException, IOException {
        String rserveStartCMD = "R CMD Rserve --vanilla --slave";
        Runtime.getRuntime().exec(rserveStartCMD).waitFor();
    }

    private static void startRServeOnWindows() throws IOException {
        String rserveStartCMD = "cmd /c start R -e library(Rserve);Rserve() --vanilla --slave";
        Runtime.getRuntime().exec(rserveStartCMD);
    }

    public void startRserve() throws InterruptedException, IOException {
        LOGGER.debug("Starting Rserve locally...");
        if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) {
            startRServeOnLinux();
        }
        else if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
            startRServeOnWindows();
        }
        LOGGER.debug("Started RServe.");
    }
}

