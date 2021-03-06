/*
 * @(#) $RCSfile: CompositeCluster.java,v $ $Revision: 1.3 $ $Date: 2003/04/15 15:47:57 $ $Name: TableView1_2 $
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


package edu.umn.genomics.table.cluster;

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;

/**
 * A CompositeCluster is composed of other Clusters.
 * @author       J Johnson
 * @version $Revision: 1.3 $ $Date: 2003/04/15 15:47:57 $  $Name: TableView1_2 $ 
 * @since        1.0
 */
public class CompositeCluster extends Cluster {
  double[] values = null;  
  public CompositeCluster() {
    setAllowsChildren(true);
  }
  public CompositeCluster(Cluster c1, Cluster c2) {
    this();
    add(c1);
    add(c2);
  }
  public double[] getValues() {
    return values;
  }
  public void setValues(double[] values) {
      this.values = values;
  }
  public String toString() {
    StringBuilder s = new StringBuilder("[");
    int n = 0;
    for (Enumeration e = children();  e.hasMoreElements();) {
      Cluster c = (Cluster)e.nextElement();
      if (c != null) {
          if (n++ > 0) {
              s.append(","); 
          } 
        s.append(c.toString());
          
      }
    }
    s.append("]");
    return s.toString();
  }
}

