/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umn.genomics.table;

/**
 *
 * @author James E Johnson <jj@umn.edu>
 * @version 
 */
public class PartitionBinLabeler implements BinLabeler {
    ColumnMap cmap;
    PartitionIndexMap pim;
    PartitionBinLabeler(ColumnMap cmap, PartitionIndexMap pim) {
      this.cmap = cmap;
      this.pim = pim;
    }
    @Override
    public String getLabel(int binIndex) {
      int ri = pim.getSrc(binIndex);
      if (ri >= 0) {
        Object obj = cmap.getValueAt(ri);
        if (obj != null) {
          return obj.toString();
        }
      }
      return "";
    }
}
