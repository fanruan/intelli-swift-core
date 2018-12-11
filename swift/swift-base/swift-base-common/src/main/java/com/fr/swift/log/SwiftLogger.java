package com.fr.swift.log;

import com.fineio.logger.FineIOLogger;
import org.slf4j.Logger;

/**
 * @author anchore
 * @date 2018/7/4
 */
public interface SwiftLogger extends Logger, FineIOLogger {

    void debug(Throwable t);

    void warn(Throwable t);

    void error(Throwable t);
}