/*
 * @(#) $RCSfile: TableView.java,v $ $Revision: 1.51 $ $Date: 2004/08/02 20:23:46 $ $Name: TableView1_3 $
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
// import java.io.*;
// import java.net.*;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.prefs.*;
import java.io.*;

public class PreferenceTableModel extends DefaultTableModel {
  static String[] columns = {"Item","Attribute","Value"};
  Class[] classes = {java.lang.Class.class, java.lang.String.class, java.lang.Object.class};
  Preferences prefs;
  public PreferenceTableModel() throws BackingStoreException {
    super(columns,0);
    loadTable();
  }
  private void loadTable() throws BackingStoreException {
    ClassLoader cl = this.getClass().getClassLoader();
    // Initialize TableViewPreferences 
    try {
      cl.loadClass("edu.umn.genomics.table.TableViewPreferences");
    } catch (Exception ex) {
      ExceptionHandler.popupException(""+ex);
    }
    Vector dataVector = new Vector();
    prefs = Preferences.userNodeForPackage(this.getClass());
    for (String nodeName : prefs.childrenNames()) {
      Preferences pref = prefs.node(nodeName);
      String className = pref.absolutePath().substring(1).replace('/','.');
      System.err.println("ClassName: " + nodeName + " " + className);
      try {
        Class nodeClass = cl.loadClass(className);
        for (String key : pref.keys()) {
          System.err.println("key: " + nodeName + " " + className + " " + key);
          Vector row = new Vector(3);
          row.add(nodeClass);
          row.add(key);
          row.add(TableViewPreferences.getAttributePreference(nodeClass,key));
          dataVector.add(row); 
        } 
      } catch (Exception ex) {
        ExceptionHandler.popupException(""+ex);
      }
    }
    setDataVector(dataVector,new Vector(Arrays.asList(columns)));
  }
  public void useDefaultValues() throws IOException,BackingStoreException,InvalidPreferencesFormatException {
    TableViewPreferences.setDefaults();
    loadTable();
  }
  public Class getColumnClass(int column) {
    return column >= 0 && column < classes.length ? classes[column] : java.lang.Object.class;
  }
  public boolean isCellEditable(int row, int column) {
    return column == 2 ? true : false;
  }
  public void setValueAt(Object value, int row, int col) {
    super.setValueAt(value, row, col);
    System.err.println("setValueAt " + getValueAt(row,0) + " " + getValueAt(row,1) + " " + value);
    TableViewPreferences.savePreference((Class)getValueAt(row,0), (String)getValueAt(row,1), value);
  }

}
