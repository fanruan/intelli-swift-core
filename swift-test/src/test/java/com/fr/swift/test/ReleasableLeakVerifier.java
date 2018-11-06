package com.fr.swift.test;

import com.fr.swift.cube.io.impl.mem.MemIo;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.log.SwiftLoggers;
import com.fr.third.net.bytebuddy.agent.ByteBuddyAgent;
import com.fr.third.net.bytebuddy.agent.builder.AgentBuilder;
import com.fr.third.net.bytebuddy.implementation.MethodDelegation;
import com.fr.third.net.bytebuddy.implementation.SuperMethodCall;
import com.fr.third.net.bytebuddy.implementation.bind.annotation.SuperCall;
import com.fr.third.net.bytebuddy.matcher.ElementMatchers;
import org.junit.rules.Verifier;

import java.util.concurrent.Callable;

import static com.fr.third.net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static com.fr.third.net.bytebuddy.matcher.ElementMatchers.isInterface;
import static com.fr.third.net.bytebuddy.matcher.ElementMatchers.isSubTypeOf;
import static com.fr.third.net.bytebuddy.matcher.ElementMatchers.not;

/**
 * @author anchore
 * @date 2018/11/5
 */
public class ReleasableLeakVerifier extends Verifier {

    private static ThreadLocal<Integer> handleCount = ThreadLocal.withInitial(() -> 0);

    public static class ReleaseInterceptor {
        public static void intercept(@SuperCall Callable<?> superCall) {
            try {
                superCall.call();
                handleCount.set(handleCount.get() - 1);
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    public static class ConstructInterceptor {
        public static void intercept() {
            handleCount.set(handleCount.get() + 1);
        }
    }

    @Override
    protected void verify() throws Throwable {
        int count = handleCount.get();
        handleCount.remove();
        if (count > 0) {
            throw new Exception(String.format("%s releasable(s) leaked", count));
        }
    }

    static {
        installAgent();
    }

    private static void installAgent() {
        ByteBuddyAgent.install();
        AgentBuilder agentBuilder = new AgentBuilder.Default()
                .type(isSubTypeOf(Reader.class)
                        .and(isSubTypeOf(Writer.class))
                        .and(not(isSubTypeOf(MemIo.class)))
                        .and(not(isAbstract()))
                        .and(not(isInterface())))
                .transform((builder, typeDescription, classLoader, javaModule) ->
                        builder.constructor(ElementMatchers.any())
                                .intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.to(ConstructInterceptor.class)))
                                .method(ElementMatchers.named("release"))
                                .intercept(MethodDelegation.to(ReleaseInterceptor.class))
                );
        agentBuilder.installOnByteBuddyAgent();
    }
}