package com.fr.swift.repository;

import java.io.IOException;
import java.net.URI;

/**
 * @author yee
 * @date 2018/5/28
 */
public interface SwiftRepository {
    URI copyFromRemote(URI remote, URI local) throws IOException;

    boolean copyToRemote(URI local, URI remote) throws IOException;

    boolean zipToRemote(URI local, URI remote) throws IOException;
}
