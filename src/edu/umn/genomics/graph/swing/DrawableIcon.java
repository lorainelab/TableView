/*
 * @(#) $RCSfile: Drawable.java,v $ $Revision: 1.2 $ $Date: 2002/07/30 19:44:49 $ $Name: TableView1_2 $
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


package edu.umn.genomics.graph.swing;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import edu.umn.genomics.graph.*;

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
public class DrawableIcon implements Icon {
  int iw;
  int ih;
  Drawable drawable;
  public DrawableIcon(int w, int h, Drawable drawable) {
    iw = w;
    ih = h;
    setDrawable(drawable);
  }
  public void setDrawable(Drawable drawable) {
    this.drawable = drawable;
  }
  public Drawable getDrawable() {
    return drawable;
  }
  public int getIconWidth() {
    return iw;
  }
  public int getIconHeight() {
    return ih;
  }
  public void paintIcon(Component c, Graphics g, int ix, int iy) {
    if (drawable != null) {
      drawable.draw(g,iw/2,ih/2);
    }
  }
}
