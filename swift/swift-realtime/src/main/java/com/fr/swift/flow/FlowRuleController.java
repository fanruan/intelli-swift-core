package com.fr.swift.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-15 15:49:02
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class FlowRuleController implements FlowControlRule {
    private List<FlowControlRule> ruleList;

    public FlowRuleController() {
        ruleList = new ArrayList<FlowControlRule>();
    }

    public FlowRuleController(List<FlowControlRule> ruleList) {
        this.ruleList = ruleList;
    }

    @Override
    public boolean isEnd() {
        if (ruleList == null || ruleList.isEmpty()) {
            return false;
        } else {
            for (FlowControlRule flowControlRule : ruleList) {
                if (flowControlRule.isEnd()) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void reset() {
        if (ruleList != null) {
            for (FlowControlRule rule : ruleList) {
                rule.reset();
            }
        }
    }
}
