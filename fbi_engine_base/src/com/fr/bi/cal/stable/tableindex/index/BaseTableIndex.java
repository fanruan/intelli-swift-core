package com.fr.bi.cal.stable.tableindex.index;

import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.cal.stable.tableindex.AbstractTableIndex;
import com.fr.bi.stable.engine.index.BITableCubeFile;
import com.fr.bi.stable.engine.index.getter.DetailGetter;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.CalculatorTraversalAction;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GUY on 2015/3/18.
 */
public abstract class BaseTableIndex extends AbstractTableIndex {

    protected Map<BIKey, Double> numberMaxValueMap = new ConcurrentHashMap<BIKey, Double>();
    protected Map<BIKey, Double> numberMinValueMap = new ConcurrentHashMap<BIKey, Double>();
    protected Map<BIKey, Double> numberSumValueMap = new ConcurrentHashMap<BIKey, Double>();

    protected BaseTableIndex(String path, SingleUserNIOReadManager manager) {
        super( path, manager);
    }

    public BaseTableIndex(BITableCubeFile cube) {
        super(cube);
    }

    @Override
    public BIKey getColumnIndex(String fieldName) {
        return new IndexKey(fieldName);
    }

    @Override
    public BIKey getColumnIndex(BusinessField field) {
        return getColumnIndex(field.getFieldName());
    }

    @Override
    public Object[] getRow(BIKey columnIndex, int[] rows) {
        Object[] res = new Object[rows.length];
        for (int i = 0, ilen = rows.length; i < ilen; i++) {
            res[i] = getRow(columnIndex, rows[i]);
        }
        return res;
    }

    @Override
    public double getMAXValue(GroupValueIndex gvi, BIKey summaryIndex) {
        final DetailGetter list = getDetailGetter(summaryIndex);
        CalculatorTraversalAction ss = new CalculatorTraversalAction() {
            boolean firstValue = true;

            @Override
            public void actionPerformed(int row) {
                Object v = list.getValueObject(row);
                if (v != null) {
                    double res = ((Number) v).doubleValue();
                    if (firstValue) {
                        firstValue = false;
                        sum = res;
                    } else {
                        sum = Math.max(sum, res);
                    }
                }
            }

            @Override
            public double getCalculatorValue() {
                return sum;
            }
        };
        gvi.Traversal(ss);
        return ss.getCalculatorValue();
    }

    @Override
    public double getMAXValue(BIKey summaryIndex) {
        if (numberMaxValueMap.get(summaryIndex) == null) {
            numberMaxValueMap.put(summaryIndex, getMAXValue(allShowIndex, summaryIndex));
        }
        return numberMaxValueMap.get(summaryIndex);
    }

    @Override
    public double getMINValue(GroupValueIndex gvi, BIKey summaryIndex) {
        final DetailGetter list = getDetailGetter(summaryIndex);
        CalculatorTraversalAction ss = new CalculatorTraversalAction() {
            boolean firstValue = true;

            @Override
            public void actionPerformed(int row) {
                Object v = list.getValueObject(row);
                if (v != null) {
                    double res = ((Number) v).doubleValue();
                    if (firstValue) {
                        firstValue = false;
                        sum = res;
                    } else {
                        sum = Math.min(sum, res);
                    }
                }
            }

            @Override
            public double getCalculatorValue() {
                return sum;
            }
        };
        gvi.Traversal(ss);
        return ss.getCalculatorValue();
    }

    @Override
    public double getMINValue(BIKey summaryIndex) {
        if (numberMinValueMap.get(summaryIndex) == null) {
            numberMinValueMap.put(summaryIndex, getMINValue(allShowIndex, summaryIndex));
        }
        return numberMinValueMap.get(summaryIndex);
    }

    @Override
    public double getSUMValue(GroupValueIndex gvi, BIKey summaryIndex) {
        final DetailGetter list = getDetailGetter(summaryIndex);
        CalculatorTraversalAction ss = new CalculatorTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                Object v = list.getValueObject(row);
                if (v != null) {
                    double res = ((Number) v).doubleValue();
                    sum += res;
                }
            }

            @Override
            public double getCalculatorValue() {
                return sum;
            }
        };
        gvi.Traversal(ss);
        return ss.getCalculatorValue();
    }

    @Override
    public double getSUMValue(BIKey summaryIndex) {
        if (numberSumValueMap.get(summaryIndex) == null) {
            numberSumValueMap.put(summaryIndex, getSUMValue(allShowIndex, summaryIndex));
        }
        return numberSumValueMap.get(summaryIndex);
    }

    @Override
    public double getDistinctCountValue(GroupValueIndex gvi, final BIKey distinct_field) {
        final Set<Object> resMap = new HashSet<Object>();
        SingleRowTraversalAction ss = new SingleRowTraversalAction() {
            @Override
            public void actionPerformed(int row) {
                Object v = getRow(distinct_field, row);
                //D:null值不做统计
                if (v != null) {
                    resMap.add(v);
                }
            }
        };
        gvi.Traversal(ss);
        return resMap.size();
    }

    @Override
    public Object getRow(BIKey key, int row) {
        DetailGetter getter = getDetailGetter(key);
        if (getter != null) {
            return getter.getValue(row);
        }
        return null;
    }

    @Override
    public Object getRowValue(BIKey key, int row) {
        DetailGetter getter = getDetailGetter(key);
        if (getter != null) {
            return getter.getValueObject(row);
        }
        return null;
    }

    private DetailGetter getDetailGetter(BIKey key) {
        try {
            return cube.createDetailGetter(key, manager);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void clear() {
        cube.clear();
    }
}