package com.fr.swift.repository.impl;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepository;

import java.io.IOException;

/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-09-05
 */
public class MuteRepositoryImpl implements SwiftRepository {

    private MuteRepositoryImpl() {
    }

    public static MuteRepositoryImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public String copyFromRemote(String remote, String local) throws IOException {
        SwiftLoggers.getLogger().warn("Use MuteRepository.copyFromRemote return {}", local);
        return local;
    }

    @Override
    public boolean copyToRemote(String local, String remote) throws IOException {
        SwiftLoggers.getLogger().warn("Use MuteRepository.copyToRemote return {}", true);
        return true;
    }

    @Override
    public boolean zipToRemote(String local, String remote) throws IOException {
        SwiftLoggers.getLogger().warn("Use MuteRepository.zipToRemote return {}", true);
        return true;
    }

    @Override
    public boolean delete(String remote) throws IOException {
        SwiftLoggers.getLogger().warn("Use MuteRepository.delete return {}", true);
        return true;
    }

    @Override
    public long getSize(String path) throws IOException {
        SwiftLoggers.getLogger().warn("Use MuteRepository.getSize return {}", true);
        return 0;
    }

    @Override
    public boolean exists(String path) {
        SwiftLoggers.getLogger().warn("Use MuteRepository.exists return {}", true);
        return true;
    }

    private static class SingletonHolder {
        private static final MuteRepositoryImpl INSTANCE = new MuteRepositoryImpl();
    }
}
