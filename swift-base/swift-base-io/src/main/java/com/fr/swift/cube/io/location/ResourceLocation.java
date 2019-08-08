package com.fr.swift.cube.io.location;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.context.ContextProvider;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.util.Strings;

import java.io.File;
import java.net.URI;

/**
 * @author anchore
 * @date 17/11/24
 */
public class ResourceLocation implements IResourceLocation {
    private static final String SEPARATOR = "/";
    private static final StoreType DEFAULT_STORE_TYPE = StoreType.FINE_IO;
    private String basePath;

    private URI uri;
    private StoreType storeType;

    public ResourceLocation(String path) {
        this(path, DEFAULT_STORE_TYPE);
    }

    public ResourceLocation(String path, StoreType storeType) {
        path = Strings.trimSeparator(path, "\\", SEPARATOR);
//        path = SEPARATOR + path;
        path = Strings.trimSeparator(path, SEPARATOR);
        uri = URI.create(path);

        this.storeType = storeType;
        initBasePath();
    }

    private void initBasePath() {
        final String contextPath = SwiftContext.get().getBean(ContextProvider.class).getContextPath();
        final SwiftConfigEntityQueryBus query = (SwiftConfigEntityQueryBus) SwiftContext.get().getBean(SwiftConfig.class).query(SwiftConfigEntity.class);
        this.basePath = query.select(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH, String.class, contextPath);
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public StoreType getStoreType() {
        return storeType;
    }

    @Override
    public String getPath() {
        return uri.getPath();
    }

    @Override
    public String getAbsolutePath() {
        String path = basePath + SEPARATOR + getPath();
        return Strings.trimSeparator(path, SEPARATOR);
    }

    @Override
    public IResourceLocation buildChildLocation(String child) {
        return new ResourceLocation(getPath() + SEPARATOR + child, storeType);
    }

    @Override
    public IResourceLocation getParent() {
        String parent = new File(uri.getPath()).getParent();
        return new ResourceLocation(parent, storeType);
    }

    @Override
    public String getName() {
        return new File(uri.getPath()).getName();
    }

    @Override
    public IResourceLocation addSuffix(String suffix) {
        return new ResourceLocation(getPath() + suffix, storeType);
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", uri.getPath(), storeType);
    }
}