package com.finebi.cube.gen.arrange;

import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.Cube;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBuilderArrange {
    private Cube cube;
    private IRouter router;

//    public void arrangeTable(Set<CubeTableSource> tableSourceSet) {
//        Iterator<CubeTableSource> it = tableSourceSet.iterator();
//        while (it.hasNext()) {
//            try {
//                CubeTableSource tableSource = it.next();
//                BISourceDataTransport dataTransport = new BISourceDataAllTransport(cube, tableSource, tableSourceSet, null, 1);
//                BIBuildCubeSubscriber subscriber = new BIBuildCubeSubscriber(new BISubscribeID(tableSource.getSourceID()), dataTransport);
//                subscriber.subscribe(BICubeBuildTopicTag.START_BUILD_CUBE);
//            } catch (BITopicAbsentException e) {
//                e.printStackTrace();
//            } catch (BIRegisterIsForbiddenException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
