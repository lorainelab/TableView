/*
 * @(#) $RCSfile: ClutoColumnMap.java,v $ $Revision: 1.1 $ $Date: 2004/02/20 03:39:01 $ $Name: TableView1_2 $
 *
 * Center for Computational Genomics and Bioinformatics
 * Academic Health Center, University of Minnesota
 * Copyright (c) 2000-2002. The Regents of the University of Minnesota  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * see: http://www.gnu.org/copyleft/gpl.html
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 */


package edu.umn.genomics.table.cluster.cluto;

import java.io.Serializable;
import java.lang.ref.*;
import java.util.*;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import edu.umn.genomics.table.*;
import jcluto.*;

/**
 * ClutoColumnMap maps the values of a TableModel column to a numeric range.
 * If Class type of the column is Number, or if all of the values of the 
 * column are Strings that can be parsed as class Number, the range will 
 * be from the minimum to the maximun of the values in the column. 
 * If any value can not be parsed as class Number, the values will be mapped 
 * to integral values from 0 to the number of distinct values - 1,
 * (distinct values are determined by the value's equals method.)
 * The ClutoColumnMap can also be used to select a range of mapped values 
 * from the column and indicate the selected rows in a ListSelectionModel.  
 * A new selection is be specified as a subrange of values between the
 * the minimum and maximum values of the column.
 * New selections may be combined with the previous selections using standard 
 * set operators.
 * 
 * @author       J Johnson
 * @version $Revision: 1.1 $ $Date: 2004/02/20 03:39:01 $  $Name: TableView1_2 $ 
 * @since        1.0
 * @see  SetOperator 
 * @see  javax.swing.table.TableModel 
 * @see  javax.swing.ListSelectionModel
 */
public class ClutoColumnMap extends AbstractColumnMap 
                              implements Serializable, TableModelListener {
  boolean recalc = true;

  private synchronized void calcStats() {
    if (recalc && mapState == CellMap.MAPPED) {
      recalc = false;
      collectStats(getMapValues());
    }
  }

  private synchronized void statsNeeded() {
    recalc = true;
  }

  /** 
   * Map the rows in the given column of the table to numeric values. 
   * @param tableModel the table containing the column of values
   * @param column the index of the column in the TableModel
   */
  public ClutoColumnMap(TableModel tableModel, int column) {
    super(tableModel, column);
    setState(CellMap.MAPPED);
  }

  /**
   * Map the rows in the given column of the table to numeric values. 
   * If mapNumbers is false, the distinct values of the column will 
   * by mapped as discreet set elements as if they were not numeric.
   * @param tableModel the table containing the column of values
   * @param column the column in the table to map.
   * @param continuous if true map numeric and date values as a real number 
   *       in the range of values from the column, if false treat numbers as 
   *       elements of a set.
   */
  public ClutoColumnMap(ClutoTableMatrix tableModel, int column, 
                          boolean continuous) {
    super(tableModel, column, continuous);
    setState(CellMap.MAPPED);
  }

  /**
   * Sets tableModel as the data model for the column being mapped.
   * @param tableModel the data model
   */
  public void setTableModel(TableModel tableModel) {
    if (!(tableModel instanceof ClutoTableMatrix)) {
      throw new IllegalArgumentException("ClutoColumnMap.setTableModel(TableModel) must be a ClutoTableMatrix");
    }
    if (tm != null) {
      tm.removeTableModelListener(this);
    }
    tm = tableModel;
    tm.addTableModelListener(this);
  }

  /** 
   * Return the num of null values in this column.
   * @return the num of null values in this column. 
   */
  public int getNullCount() {
    calcStats();
    return super.getNullCount();
  }

  /** 
   * Return the num of distinct values in this column.
   * @return the num of distinct values in this column. 
   */
  public int getDistinctCount() {
    calcStats();
    return super.getDistinctCount();
  }

  /** 
   * Return the median value in this column in this column.
   * @return the median value in this column in this column. 
   */
  public double getMedian() { // median value
    calcStats();
    return super.getMedian();
  } 

  /** 
   * Return the mean value in this column in this column.
   * This is only meaningful for continuously mapped  columns.
   * @return the mean value in this column in this column. 
   */
  public double getAvg() {
    calcStats();
    return super.getAvg();
  }

  /** 
   * Return the variance of the values in this column in this column.
   * This is only meaningful for continuously mapped  columns.
   * @return the variance of the  values in this column in this column. 
   */
  public double getVariance() { // statistical variance value
    calcStats();
    return super.getVariance();
  } 

  /** 
   * Return the standard deviation of the values in this column in this column.
   * This is only meaningful for continuously mapped  columns.
   * @return the standard deviation of the values in this column in this column. 
   */
  public double getStdDev() { // standard deviation value
    calcStats();
    return super.getStdDev();
  } 

  /**
   * Return the minimum mapped value of this column.
   * @return the minimum mapped value of this column.
   */
  public double getMin() {
    calcStats();
    return super.getMin();
  }
  /**
   * Return the maximum mapped value of this column.
   * @return the maximum mapped value of this column.
   */
  public double getMax() {
    calcStats();
    return super.getMax();
  }
  /**
   * Return the mapped value for the given table model row.
   * return the mapped value of each row element in the column.
   */
  public double[] getMapValues() {
    double vals[] = new double[tm.getRowCount()];
    for (int rowIndex = 0; rowIndex < vals.length; rowIndex++) {
      vals[rowIndex] = ((ClutoTableMatrix)tm).getValue(rowIndex,colIndex);
    }
    return vals;
  }

  /**
   * Return the mapped value for the given table model row.
   * @param row the row in the table
   * return the mapped value of the row 
   */
  public double getMapValue(int row) {
    if (row < 0 || row >= getCount()) 
      return Double.NaN;
    try {
      return (double)((ClutoTableMatrix)tm).getValue(row,colIndex);
    } catch (Exception ex) {
            ExceptionHandler.popupException(""+ex);
    }
    return Double.NaN;
  }

  /**
   * Return the element that is mapped nearest to the the mapValue in the 
   * given direction.
   * @param mapValue the relative position on the map 
   * @param dir negative means round down, positive mean round up, 
   *            0 rounds to closest..
   * return the element that is mapped nearest to the the mapValue.
   */
  public Object getMappedValue(double mapValue, int dir) {
    return new Float((float)mapValue);
  }

  /** 
   * Construct a histogram returning the count of elements 
   * in each subrange along the ColumnMap.
   * @param buckets the number of divisions of the range of values.  If 
   * the value is zero, the default numbers of subdivisions be returned:
   * 10 if the column is numeric, else the number of discrete elements.
   * @param withSelectCounts 
   * @return an array of count of elements in each range.
   */
  public int[] getCounts(int buckets, boolean withSelectCounts) {
    calcStats();
    if (colIndex >= tm.getColumnCount()) {
      return new int[0];
    }
    boolean selectCount = withSelectCounts && lsm != null;
    int offset = selectCount ? 2 : 1;
    int b[] = null;
      int len = buckets > 0 ? buckets : 10;
      double incr = (max - min) / len;
      b = new int[len*offset];
      int nrows = tm.getRowCount();
      double val = Double.NaN;
      for (int rowIndex = 0; rowIndex < nrows; rowIndex++) {
        val = ((ClutoTableMatrix)tm).getValue(rowIndex,colIndex);
        if (Double.isNaN(val) || Double.isInfinite(val))
          continue;
        int i = (int)((val - min) / incr);
        if (i >= buckets)
          i = buckets-1;
        if (i < 0)
          i = 0;
        b[i*offset]++;
        if (selectCount) {
          if (lsm.isSelectedIndex(rowIndex)) {
            b[i*offset+1]++;
          }
        }
      }
    return b;
  }

  /** 
   * Return whether all values in this column are number values.
   * @return true if all values in this column are number values, else false.
   */
  public boolean isNumber() {
    return true;
  }

  /** 
   * Return whether all values in this column are Dates.
   * @return true if all values in this column are Dates, else false.
   */
  public boolean isDate() {
    return false;
  }

  public void cleanUp() {
    setState(CellMap.INVALID);
    if (tm != null)
      tm.removeTableModelListener(this);
    tm = null;
    lsm = null;
    fireColumnMapChanged(CellMap.INVALID);
  }

  /**
   * The TableModelEvent should be constructed in the coordinate system of
   * the table model.
   * @param e the change to the data model
   */
  public void tableChanged(TableModelEvent e) {
    if (e == null || e.getFirstRow() == TableModelEvent.HEADER_ROW) {
      // The whole thing changed, the class including this should handle this
      // by disposing this ColumnMap and creating new ones.
      //System.err.println("\n" + c + "HEADER_ROW ");
      cleanUp();
      return;
    }
    statsNeeded();
  }

}
