package com.fr.bi.report.store;

import java.util.Comparator;

public interface BISortKey {

    Comparator getComparator();

    void setComparator(Comparator comparator);

}