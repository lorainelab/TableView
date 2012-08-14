/*
 * @(#) $RCSfile: DataView.java,v $ $Revision: 1.10 $ $Date: 2003/05/15 18:23:43 $ $Name: TableView1_0b2 $
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


package edu.umn.genomics.table.dv;  //DataViewer

import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Observer;
import java.util.Observable;
import edu.umn.genomics.table.SetOperator;

/**
 * @author       J Johnson
 * @version $Revision: 1.10 $ $Date: 2003/05/15 18:23:43 $  $Name: TableView1_0b2 $
 * @since        1.0
 */
public class DataMapGUI extends JPanel  {
  // Tabs Global Selection Coordinate Color
  // Global
  // PerIndex
  // Default Selection 
  //  Select Shape
  //  Select Color
  //  NonSelect Color
/*
  protected JComponent makeToolBar() {
  if (false) { // start of new menulook
   // ToolBar action buttons
     // view all
     // mode: navigate, selection 
     // show axes
   // dataset
     // list
       // multiple selection
     // delete
     // show/hide
     // properties editor
   // property menu both per dataset and as a whole
     // show axes
     // inertial rotation
     // navigation speed << < + > >> 
     // point scale << < + > >>
     // point rep point line sphere, cube, cone, cylinder 
     // mapping x,y,z, rgb, hsb
       // colIdx, offset, scale
     // axis scaling x,y,z
     // color fg, bg, select
     // selection as: color, size, shape
     // non selection as: color, size, shape, hidden
     JCheckBox showAxes;
     JComboBox representation;
     JComboBox selected;
     RowVertexMap
       public int getRowIndexAt(int coordinateIndex);
       public int getCoordinateIndex(int rowIndex);
       public float[] getCoordinateAt(int rowIndex, float coordinate[]);
       public float[] getCoordinates(float coordinate[], int incr);
     CoordinateMapping
       x  col  axis
       y  
       z
       xyz
     ColorMapping
       h/r
       s/g
       b/b
       hsb/rgb 
     TransparencyMapping
     TextureMapping
     ShapeMapping
       shape map 
     SelectionMapping
  }
*/

  public DataMapGUI() {
  }

  public static void main( String[] args) {
  }

}
