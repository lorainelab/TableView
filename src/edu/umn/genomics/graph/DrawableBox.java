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
public class DrawableBox implements Drawable {
   protected int radius = 1;
   protected boolean fill = false;

  public DrawableBox() {
  }

  public DrawableBox(int radius, boolean fill) {
    this.radius = radius;
    this.fill = fill;
  }

  public int getRadius() {
    return radius;
  }
  public void setRadius(int radius) {
    this.radius = radius;
  }

  public boolean getFill() {
    return fill;
  }
  public void setFill(boolean fill) {
    this.fill = fill;
  }



  /*
   * Draw using the given graphics context at the given point.
   * @param g the graphics context.
   * @param xAxis The X axis of the graph.
   * @param yAxis The Y axis of the graph.
   */
  public void draw(Graphics g, int x, int y) {
    int d = radius*2;
    if (fill) {
      g.fillRect(x-radius, y-radius, d, d);
    }
    g.drawRect(x-radius, y-radius, d, d);
  }
  public String toString() {
    return this.getClass().getSimpleName() + ",Radius=" + getRadius() + ",Fill=" + getFill();
  }

}
