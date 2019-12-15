package com.fr.swift.executor.task.rule;


import com.fr.swift.executor.type.SwiftTaskType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

/**
 * @author Heng.J
 * @date 2019/12/13
 * @description
 * @since swift 1.1
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({TaskRule.class})
public class TaskRuleContainerTest {

    @Mock
    TaskRule taskRule;

    @Test
    public void test() {
        TaskRuleContainer.getInstance().registerRules(SwiftTaskType.COLLATE, taskRule);
        Assert.assertEquals(TaskRuleContainer.getInstance().getRulesByType(SwiftTaskType.COLLATE), taskRule);
    }
}