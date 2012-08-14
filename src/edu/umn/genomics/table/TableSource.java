/*
 * @(#) $RCSfile: TableSource.java,v $ $Revision: 1.2 $ $Date: 2002/07/30 19:45:11 $ $Name: TableView1_3 $
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
import javax.swing.table.TableModel;
import javax.swing.event.ChangeListener;

/**
 * Associates a named table data source and the
 * TableModel interface to that data source.
 *
 * @author       J Johnson
 * @version $Revision: 1.2 $ $Date: 2002/07/30 19:45:11 $  $Name: TableView1_3 $ 
 * @since        1.0
 */
public interface TableSource {
  /**
   * Return the name for the source of the data table.
   * @return The source of the data table
   */
  public String getTableSource();
  /**
   * Return a TableModel for the data source.
   * @return The TableModel for the data source, or null if not available.
   */
  public TableModel getTableModel();
  /**
   * Adds the listener to be notified of changes to the data source.
   * @param listener the ChangeListener to add
   */
  public void addChangeListener(ChangeListener listener);
  /**
   * Removes the listener from the notification list.
   * @param listener the ChangeListener to remove
   */
  public void removeChangeListener(ChangeListener listener);
}
