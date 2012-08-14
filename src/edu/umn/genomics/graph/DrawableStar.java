/*
 * @(#) $RCSfile: DrawableStar.java,v $ $Revision: 1.2 $ $Date: 2002/07/30 19:44:49 $ $Name: TableView1_2 $
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
public class DrawableStar implements Drawable {
  protected int radius = 1;
  protected int points = 6;

  public DrawableStar() {
  }

  public DrawableStar(int radius, int points) {
    this.radius = radius;
    this.points = points;
  }

  public int getRadius() {
    return radius;
  }
  public void setRadius(int radius) {
    this.radius = radius;
  }

  public int getPoints() {
    return points;
  }
  public void setPoints(int points) {
    this.points = points < 1 ? 1 : points > 12 ? 12 : points;
  }

  /*
   * Draw using the given graphics context at the given point.
   * @param g the graphics context.
   * @param xAxis The X axis of the graph.
   * @param yAxis The Y axis of the graph.
   */
  public void draw(Graphics g, int x, int y) {
    Graphics2D g2 = (Graphics2D)g;
    Line2D.Double line = new Line2D.Double();
    for (int i = 0; i < points; i++) { 
      double arc = i * Math.PI * 2. / points;
      line.setLine(x,y, x+Math.sin(arc)*radius, y-Math.cos(arc)*radius);
      g2.draw(line);
    }
  }
  public String toString() {
    return this.getClass().getSimpleName() + ",Radius=" + getRadius() + ",Points=" + getPoints();
  }
}
