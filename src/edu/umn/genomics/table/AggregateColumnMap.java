/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umn.genomics.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

/**
 *
 * @author James E Johnson <jj@umn.edu>
 * @version 
 */
public class AggregateColumnMap extends CacheColumnMap {
    public enum AggregateFunction {
        COUNT, MIN, MAX, MEDIAN, Q1, Q3, AVG, STDDEV, VARIANCE, LIST
    };
    PartitionIndexMap pim;
    AggregateFunction func;
    Comparator<Object> comparator = new Comparator<Object>() {
        @Override
        public int compare(Object t, Object t1) {
            if (t == t1) { // also covers case of both being null
                return 0;
            } else if (t == null) {  // sort nulls to the end
                return 1;
            } else if (t1 == null) { // sort nulls to the end
                return -1;
            } else if (t instanceof Comparable) {
                return ((Comparable)t).compareTo(t1);
            } 
            return t1.hashCode() - t.hashCode();
        }
    };
   // TableModel tm, ListSelectionModel lsm, String name, int colIndex, Class colClass

    public AggregateColumnMap(PartitionIndexMap pim, AggregateFunction func, TableModel tm, ListSelectionModel lsm, String name, int colIndex, Class colClass) {
        super(tm, lsm, name, colIndex, func == AggregateFunction.COUNT ? Integer.class : func == AggregateFunction.AVG || func == AggregateFunction.STDDEV || func == AggregateFunction.VARIANCE ? Double.class : colClass);
        this.pim = pim;
        this.func = func;
    }

    private void calculateValues(TableModel tm, int col, PartitionIndexMap pim, AggregateFunction func) {
        setArraySize(pim.getDstSize());
        switch (func) {
                    case MIN:                        
                    case MAX:
                    case MEDIAN:
                    case Q1:
                    case Q3:                        
                        break;
                    case AVG:
                    case STDDEV:
                    case VARIANCE:                        
                        break;
                    case LIST:
        }
        for (int i = 0; i < pim.getDstSize(); i++) {
            int size =  pim.getSrcs(i).length;
            if (func == AggregateFunction.COUNT) {
                setValueAt(size, i);
            } else {
                int[] ia = pim.getSrcs(i);
                switch (func) {
                    case MIN:                        
                    case MAX:
                        Object ref = ia.length > 0 ? tm.getValueAt(ia[0], col) : null;
                        for (int j = 1; j < ia.length; j++) {
                            Object val = tm.getValueAt(ia[0], col);
                            int comp = comparator.compare(ref, val);
                            switch (func) {
                                case MIN:
                                    if (comp < 0)
                                        ref = val;
                                    break;
                                case MAX:
                                    if (comp > 0)
                                        ref = val;
                                    break;
                            }
                        }
                        setValueAt(ref, i);
                        break;
                    case MEDIAN:
                    case Q1:
                    case Q3:
                    case LIST:
                        List v = new ArrayList();;
                        for (int j = 1; j < ia.length; j++) {
                            v.add(tm.getValueAt(ia[0], col));
                        }
                        Collections.sort(v, comparator);
                        switch (func) {
                            case MEDIAN:
                                setValueAt(v.get(size/2),i);
                                break;
                            case Q1:
                                setValueAt(v.get(size/4),i);
                                break;
                            case Q3:
                                setValueAt(v.get(3*size/4),i);
                                break;
                            case LIST:
                                setValueAt(v,i);
                                break;
                        }
                        break;
                    case AVG:
                    case STDDEV:
                    case VARIANCE:
                        double _avg = 0.;
                        int n = 0;
                        for (int j = 0; j < ia.length; j++) {
                            Object obj = tm.getValueAt(ia[0], col);
                            Number num = obj != null && obj instanceof Number ? (Number) obj : null;
                            double val = num == null ? NULL_VALUE : num.doubleValue();
                            if (val != NULL_VALUE) {
                                _avg += val / size;
                                n++;
                            }
                        }
                        if (n == 0) {
                            _avg = Double.NaN;
                        } else if (n < size) {
                            _avg *= size / n;
                        }
                        switch (func) {
                            case AVG:
                                setValueAt(_avg, i);
                                break;
                            default:
                            case STDDEV:
                            case VARIANCE:
                                double _variance = 0.;
                                double n1 = n - 1;
                                if (n1 > 0) {
                                    for (int j = 0; j < ia.length; j++) {
                                        if (!Double.isNaN(ia[i]) && !Double.isInfinite(ia[i])) {
                                            _variance += Math.pow(ia[i] - _avg, 2.) / n1;
                                        }
                                    }
                                }
                                if (func == AggregateFunction.STDDEV) {
                                    setValueAt(Math.sqrt(_variance),i);
                                } else {
                                    setValueAt(_variance,i);
                                }
                                break;
                        }
                        break;
                }
            }
        }
    }

}
