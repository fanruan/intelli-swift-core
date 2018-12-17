package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.base.json.annotation.JsonProperty;
import com.fr.swift.source.alloter.RowAnalyzer;
import com.fr.swift.source.alloter.impl.BaseAllotRule;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class LineAllotRule extends BaseAllotRule {
    public static final int STEP = 10000000, MEM_STEP = 100000;

    @JsonProperty("step")
    private int step;

    public LineAllotRule() {
        this(STEP);
    }

    public LineAllotRule(int step) {
        super(AllotType.LINE);
        this.step = step;
    }

    /**
     * TODO 这些getStep方法外界应该无需感知，全部通过RowAnalyzer计算
     *
     * @return
     */
    public int getStep() {
        return step;
    }

    /**
     * TODO 实现一个RowAnalyzer，用来计算Hist，RealTime，Collate的seg
     * @return
     */
    @Override
    public RowAnalyzer analyzer() {
        return null;
    }
}