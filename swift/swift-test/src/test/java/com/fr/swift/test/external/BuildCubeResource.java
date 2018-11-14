package com.fr.swift.test.external;

import com.fr.invoke.Reflect;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.test.TestResource;
import com.fr.swift.util.FileUtil;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author anchore
 * @date 2018/10/25
 */
public class BuildCubeResource implements TestRule {

    @Override
    public Statement apply(Statement statement, Description description) {
        return RuleChain.
                outerRule(new FrEnvResource()).
                around(new ContextResource()).
                around(new LoggerResource()).
                around(new ConfDbResource()).
                around(new ConnectionResource()).
                around(new ExternalResource() {

                    String prevPath;

                    @Override
                    protected void before() {
                        Reflect.on(ResourceDiscovery.getInstance()).field("cubeMemIos").call("clear");

                        String runPath = TestResource.getRunPath(description.getTestClass());
                        FileUtil.delete(runPath);

                        SwiftCubePathService pathService = SwiftContext.get().getBean(SwiftCubePathService.class);
                        prevPath = pathService.getSwiftPath();
                        pathService.setSwiftPath(runPath);
                    }

                    @Override
                    protected void after() {
                        SwiftContext.get().getBean(SwiftCubePathService.class).setSwiftPath(prevPath);
                    }
                }).apply(statement, description);
    }
}