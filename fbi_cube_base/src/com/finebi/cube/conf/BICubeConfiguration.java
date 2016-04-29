package com.finebi.cube.conf;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.location.BICubeLocation;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeConfiguration implements ICubeConfiguration {
    @Override
    public URI getRootURI() {
        try {
            return URI.create(new BICubeLocation(BIPathUtils.createBasePath(), "Advanced").getAbsolutePath());
        } catch (URISyntaxException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
