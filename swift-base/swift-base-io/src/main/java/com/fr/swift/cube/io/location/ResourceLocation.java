package com.fr.swift.cube.io.location;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftCubePathService;
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
    private static String basePath = SwiftContext.get().getBean(SwiftCubePathService.class).getSwiftPath();

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