package com.fr.swift.cal.info;

/**
 * Created by 小灰灰 on 2014/4/1.
 */
public class AllExpander extends Expander {
    private boolean allLevel;
    private int level;
    private int baseLevel;

    public AllExpander() {
        allLevel = true;
    }

    public AllExpander(int level) {
        this.level = level;
        this.baseLevel = level;
        allLevel = false;
    }

    protected AllExpander(int level, int baseLevel) {
        this.level = level;
        this.baseLevel = baseLevel;
        allLevel = false;
    }

    @Override
    public Expander getParent() {
        if (allLevel) {
            return this;
        }
        return parent;
    }

    @Override
    public boolean isChildExpand(String name) {
        return true;
    }

    @Override
    public Expander getChildExpander(String name) {
        if (allLevel) {
            return this;
        } else if (level < 1) {
            return null;
        } else {
            AllExpander expander = new AllExpander(level - 1, baseLevel);
            expander.setParent(this);
            return expander;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        AllExpander that = (AllExpander) o;

        if (allLevel != that.allLevel) {
            return false;
        }
        if (level != that.level) {
            return false;
        }
        return baseLevel == that.baseLevel;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (allLevel ? 1 : 0);
        result = 31 * result + level;
        result = 31 * result + baseLevel;
        return result;
    }
}