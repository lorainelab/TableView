/*
 * @(#) $RCSfile: DrawableO.java,v $ $Revision: 1.2 $ $Date: 2002/07/30 19:44:49 $ $Name: TableView1_2 $
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


package edu.umn.genomics.graph;

import java.awt.*;
import java.awt.geom.*;
import edu.umn.genomics.table.ExceptionHandler;

/**
 * An item to be drawn. 
 * 
 * @author       J Johnson
 * @version $Revision: 1.2 $ $Date: 2002/07/30 19:44:49 $  $Name: TableView1_2 $ 
 * @since        1.0
 * @see  javax.swing.table.TableModel
 * @see  javax.swing.ListSelectionModel
 * @see  Graph
 */
public class DrawableShape implements Drawable {
  Shape shape = new Line2D.Double(0.,0.,0.,0.);
  public DrawableShape() {
  }
  public DrawableShape(Shape shape) {
    setShape(shape);
  }
  public void setShape(Shape shape) {
    this.shape = shape;
  }
  public Shape getShape() {
    return shape;
  }

  /*
   * Draw using the given graphics context at the given point.
   * @param g the graphics context.
   * @param xAxis The X axis of the graph.
   * @param yAxis The Y axis of the graph.
   */
  public void draw(Graphics g, int x, int y) {
    try {
      Graphics2D g2 = (Graphics2D)g;
      g2.translate(x,y);
      g2.draw(shape);
      g2.translate(-x,-y);
    } catch (Exception ex) {
      ExceptionHandler.popupException(""+ex);
    }
  }
  public String toString() {
    return this.getClass().getSimpleName() + ",Shape=" + getShape();
  }

}
