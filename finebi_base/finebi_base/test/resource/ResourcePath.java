package resource;

import java.io.File;

/**
 * Created by andrew_asa on 2017/10/11.
 */
public class ResourcePath {

    public static File getResourceFile(String path) {

        File resource =  new File(ResourcePath.class.getResource("/").getFile(), "resource");
        return new File(resource, path);
    }
}
