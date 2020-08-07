package com.fr.swift.cube.io.location;

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


//    static {
//        initBasePath();
//    }
//
//    private static void initBasePath() {
//        SwiftCubePathService cubePathService = SwiftContext.get().getBean(SwiftCubePathService.class);
//        basePath = cubePathService.getSwiftPath();
//        cubePathService.registerPathChangeListener(path -> basePath = path);
//    }

    private URI uri;
    private StoreType storeType;
    //默认路径不设置为cubePath，有可能用到其他硬盘位置
    private String basePath;
    private String path;

    public ResourceLocation(String path, String basePath) {
        this(path, DEFAULT_STORE_TYPE, basePath);
    }

    public ResourceLocation(String path, StoreType storeType, String basePath) {
        path = Strings.trimSeparator(path, "\\", SEPARATOR);
//        path = SEPARATOR + path;
        this.path = Strings.trimSeparator(path, SEPARATOR);
        uri = URI.create(basePath + SEPARATOR + this.path);
        this.basePath = basePath;
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
        return path;
    }

    @Override
    public String getAbsolutePath() {
        String path = basePath + SEPARATOR + getPath();
        return Strings.trimSeparator(path, SEPARATOR);
    }

    @Override
    public IResourceLocation buildChildLocation(String child) {
        return new ResourceLocation(getPath() + SEPARATOR + child, storeType, basePath);
    }

    @Override
    public IResourceLocation getParent() {
        String parent = new File(uri.getPath()).getParent();
        return new ResourceLocation(parent, storeType, basePath);
    }

    @Override
    public String getName() {
        return new File(uri.getPath()).getName();
    }

    @Override
    public IResourceLocation addSuffix(String suffix) {
        return new ResourceLocation(getPath() + suffix, storeType, basePath);
    }

    @Override
    public String toString() {
        return String.format("{%s, %s}", uri.getPath(), storeType);
    }
}