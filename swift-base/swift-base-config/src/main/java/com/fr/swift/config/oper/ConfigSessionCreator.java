package com.fr.swift.config.oper;

/**
 * @author yee
 * @date 2018-11-27
 */
public interface ConfigSessionCreator {
    /**
     * 创建session
     *
     * @return
     * @throws ClassNotFoundException
     */
    ConfigSession createSession() throws ClassNotFoundException;

}
