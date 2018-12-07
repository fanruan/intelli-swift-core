package com.fr.swift.cube;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class CubeUtil {
    public static boolean isReadable(Segment seg) {
        return seg.isReadable();
    }

    public static int getCurrentDir(SourceKey tableKey) {
        SwiftTablePathService tablePathService = SwiftContext.get().getBean(SwiftTablePathService.class);
        SwiftTablePathBean entity = tablePathService.get(tableKey.getId());
        if (entity == null || entity.getTablePath() == null) {
            return 0;
        }
        return entity.getTablePath();
    }
}