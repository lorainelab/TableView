/*
 * @(#) $RCSfile: MultiDimIntArray.java,v $ $Revision: 1.1 $ $Date: 2004/08/02 20:23:43 $ $Name: TableView1_3 $
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

package edu.umn.genomics.table;

import java.io.Serializable;
import java.util.*;

/**
 * Stores values for a multidimensional array in a one dimensional array.
 * @author       J Johnson
 * @version      %I%, %G%
 * @since        1.0
 */
public class MultiDimDoubleArray extends MultiDimArray implements Serializable {
  double[] vals;
  /** 
   * Create a MultiDimIntArray for the given dimensions.
   * @param dims The dimensions for a multidimensional array.
   * @exception NullPointerException If dims are null.
   * @exception IllegalArgumentException If any of dims are < 1.
   */
  public MultiDimDoubleArray(int[] dims) throws NullPointerException, IllegalArgumentException {
    super(dims);
    vals = new double[getSize()];
  }

  /**
   * Reset all array values to 0.
   */
  public void reset() {
    Arrays.fill(vals,0);
  }
  /** 
   * Get the value at loc in the array.
   * @param loc The indices of the value in the array.
   * @return The value of the array at the given indices.
   */
  public double get(int[] loc) {
    return vals[getIndex(loc)];
  }
  /** 
   * Set the value at loc in the array.
   * @param loc The indices of the value in the array.
   * @param val The value for the given location in the array.
   */
  public void set(int[] loc, double val) {
    vals[getIndex(loc)] = val;
  }
  /** 
   * Get the value at loc in the array.
   * @param loc The index of the value in the array.
   * @return The value of the array at the given index.
   */
  public double get(int loc) {
    return vals[loc];
  }
  /** 
   * Set the value at loc in the array.
   * @param loc The index of the value in the array.
   * @param val The value for the given location in the array.
   */
  public void set(int loc, int val) {
    vals[loc] = val;
  }
  /** 
   * Get the maximum value of the array.
   * @return The maximum value of the array.
   */
  public double getMax() {
    double max = 0;
    if (vals != null) {
      for (int i = 0; i < vals.length; i++) {
        if (max < vals[i])
          max = vals[i];
      }
    }
    return max;
  }
  /** 
   * Get the values of the array.
   * @return The values of the array.
   */
  public double[] getValues() {
    return vals;
  }
}

