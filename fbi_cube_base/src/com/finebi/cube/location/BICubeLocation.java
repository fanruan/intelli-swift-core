package com.finebi.cube.location;

import com.finebi.cube.data.input.*;
import com.finebi.cube.data.input.primitive.ICubeByteReaderBuilder;
import com.finebi.cube.data.input.primitive.ICubeDoubleReaderBuilder;
import com.finebi.cube.data.input.primitive.ICubeIntegerReaderBuilder;
import com.finebi.cube.data.input.primitive.ICubeLongReaderBuilder;
import com.finebi.cube.data.output.ICubeWriterBuilder;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLocation implements ICubeResourceLocation, Cloneable {
    private URI baseLocation;
    private URI childLocation;

    public BICubeLocation(String baseLocation, String childLocation) throws URISyntaxException {
        BINonValueUtils.checkNull(baseLocation);
//        File file = new File(attachFirstSlash(attachLastSlash(replaceSlash(baseLocation))));
//        this.baseLocation = file.toURI();
        this.baseLocation = new URI(attachFirstSlash(attachLastSlash(replaceSlash(baseLocation))));
        if (childLocation != null) {
            this.childLocation = new URI(BIStringUtils.cutStartSlash(replaceSlash(childLocation)));
        } else {
            this.childLocation = null;
        }
    }


    @Override
    public URI getBaseLocation() {
        return baseLocation;
    }

    @Override
    public URI getChildLocation() {
        return childLocation;
    }

    @Override
    public URI getResolvedURI() {
        if (childLocation != null) {
            return baseLocation.resolve(childLocation);
        } else {
            return baseLocation;
        }
    }

    @Override
    public void setChildLocation(URI childLocation) {
        this.childLocation = childLocation;
    }

    @Override
    public void setBaseLocation(URI baseLocation) {
        this.baseLocation = baseLocation;
    }

    private String replaceSlash(String location) {
        if (location != null) {
            return location.replace('\\', '/');
        }
        return location;
    }

    private String attachLastSlash(String location) {
        return BIStringUtils.specificEndSingleChar(location, "/");
    }

    private String attachFirstSlash(String location) {
        return BIStringUtils.specificStartSingleChar(location, "/");
    }

    @Override
    public String getAbsolutePath() {
        return getResolvedURI().getPath();
    }

    public String getScheme() {
        return this.baseLocation.getScheme();
    }

    @Override
    public ICubeResourceLocation buildChildLocation(String childPath) throws URISyntaxException {
        return new BICubeLocation(getAbsolutePath(), childPath);
    }

    @Override
    public String getFragment() {
        return baseLocation.getFragment();
    }

    @Override
    public String getQuery() {
        return baseLocation.getQuery();
    }

    @Override
    public ICubeResourceLocation setWriterSourceLocation() {
        changeArgument(ICubeWriterBuilder.QUERY_TAG, baseLocation.getFragment());
        return this;
    }

    private void changeArgument(String query, String fragment) {
        try {
            baseLocation = new URI(baseLocation.getScheme(), baseLocation.getUserInfo(),
                    baseLocation.getHost(), baseLocation.getPort(), baseLocation.getPath()
                    , query, fragment);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public ICubeResourceLocation setReaderSourceLocation() {
        changeArgument(ICubeReaderBuilder.QUERY_TAG, baseLocation.getFragment());
        return this;
    }

    @Override
    public ICubeResourceLocation setByteType() {
        changeArgument(baseLocation.getQuery(), ICubeByteReaderBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setLongType() {
        changeArgument(baseLocation.getQuery(), ICubeLongReaderBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setIntegerType() {
        changeArgument(baseLocation.getQuery(), ICubeIntegerReaderBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setDoubleType() {
        changeArgument(baseLocation.getQuery(), ICubeDoubleReaderBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setByteTypeWrapper() {
        changeArgument(baseLocation.getQuery(), ICubeByteReaderWrapperBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setLongTypeWrapper() {
        changeArgument(baseLocation.getQuery(), ICubeLongReaderWrapperBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setIntegerTypeWrapper() {
        changeArgument(baseLocation.getQuery(), ICubeIntegerReaderWrapperBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setDoubleTypeWrapper() {
        changeArgument(baseLocation.getQuery(), ICubeDoubleReaderWrapperBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setByteArrayType() {
        changeArgument(baseLocation.getQuery(), ICubeByteArrayReaderBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setStringType() {
        changeArgument(baseLocation.getQuery(), ICubeStringReaderBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public ICubeResourceLocation setGroupValueIndexType() {
        changeArgument(baseLocation.getQuery(), ICubeGroupValueIndexReaderBuilder.FRAGMENT_TAG);
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BICubeLocation{");
        sb.append("baseLocation=").append(baseLocation.toString());
        sb.append(", childLocation=").append(childLocation);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public ICubeResourceLocation generateWithSuffix(String suffix) throws URISyntaxException {
        ICubeResourceLocation cubeResourceLocation = copy();
        if (cubeResourceLocation.getChildLocation() != null) {
            String child = cubeResourceLocation.getChildLocation().getPath();
            child = BIStringUtils.cutEndSlash(child);
            child = BIStringUtils.specificEndSingleChar(child, suffix);

            cubeResourceLocation.setChildLocation(new URI(child));
        }
        return cubeResourceLocation;
    }

    @Override
    public ICubeResourceLocation copy() {
        try {
            ICubeResourceLocation cubeLocation = (ICubeResourceLocation) clone();
            cubeLocation.setBaseLocation(copyURI(baseLocation));
            cubeLocation.setChildLocation((childLocation == null) ? null : copyURI(childLocation));
            return cubeLocation;
        } catch (CloneNotSupportedException cloneException) {
            throw new RuntimeException(cloneException.getMessage(), cloneException);
        }
    }

    private URI copyURI(URI target) {
        try {
            return (new URI(target.getScheme(), target.getUserInfo(),
                    target.getHost(), target.getPort(), target.getPath()
                    , target.getQuery(), target.getFragment()));

        } catch (URISyntaxException ignore) {
            throw new RuntimeException(ignore.getMessage(), ignore);
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BICubeLocation)) return false;

        BICubeLocation that = (BICubeLocation) o;

        if (baseLocation != null ? !baseLocation.equals(that.baseLocation) : that.baseLocation != null) return false;
        return !(childLocation != null ? !childLocation.equals(that.childLocation) : that.childLocation != null);

    }

    @Override
    public int hashCode() {
        int result = baseLocation != null ? baseLocation.hashCode() : 0;
        result = 31 * result + (childLocation != null ? childLocation.hashCode() : 0);
        return result;
    }
}
