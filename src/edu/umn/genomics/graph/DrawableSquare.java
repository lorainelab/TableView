package edu.umn.genomics.graph;

import java.awt.Graphics;

/**
 * An item to be drawn.
 *
 * @author J Johnson
 * @version $Revision: 1.2 $ $Date: 2002/07/30 19:44:49 $ $Name: TableView1_3_2
 * $
 * @since 1.0
 * @see javax.swing.table.TableModel
 * @see javax.swing.ListSelectionModel
 * @see Graph
 */
public class DrawableSquare implements Drawable {
    /*
     * Draw using the given graphics context at the given point. @param g the
     * graphics context. @param xAxis The X axis of the graph. @param yAxis The
     * Y axis of the graph.
     */

    public void draw(Graphics g, int x, int y) {
        g.drawRect(x - 2, y - 2, 4, 4);
        g.drawLine(x-2, y, x+2, y);
        g.drawLine(x, y-2, x, y+2);
    }
}
