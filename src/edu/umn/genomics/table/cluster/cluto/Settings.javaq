/*
 * @(#) $RCSfile: ClutoMatrixView.java,v $ $Revision: 1.1 $ $Date: 2003/07/28 19:30:20 $ $Name: TableView1_2 $
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


package edu.umn.genomics.table.cluster.cluto;

import java.io.Serializable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import edu.umn.genomics.component.*;
import edu.umn.genomics.table.*;
import edu.umn.genomics.graph.*;
import edu.umn.genomics.graph.swing.*;
import jcluto.*;

/**
 * ClutoMatrixView displays a hierarchical clustering of rows from a table.
 * The clustering is displayed as a Dendogram which is drawn as line segments 
 * on a graph widget.  The axes of the graph are zoomable.
 * The row selection of the table is displayed on the dendogram.  
 * The user can trace out a rectangle on the dendogram to edit the 
 * row selection set for the table. 
 * @author       J Johnson
 * @version $Revision: 1.1 $ $Date: 2003/07/28 19:30:20 $  $Name: TableView1_2 $ 
 * @since        1.0
 * @see  ColumnMap
 * @see  TableContext
 * @see  javax.swing.table.TableModel
 * @see  javax.swing.ListSelectionModel
 */
public class ClutoMatrixView extends JPanel implements CleanUp, Serializable {
  class Settings extends JPanel {
    int rowHeight;
    int colWidth;
    float minValue;
    float midValue;
    float maxValue;
    boolean doubleGradient = true;
    Color minColor = Color.GREEN;
    Color midColor = Color.BLACK;
    Color maxColor = Color.RED;
    public Settings() {
      super();
    }
    public Color getColor(float value) {
      return value <= minValue 
             ? minColor 
             : value >= maxValue 
               ? maxColor
               : doubleGradient 
                 ? value == midValue
                   ? midColor
                   : value < midValue
                     ? interpolate(minColor,midColor,minValue,midValue,value) 
                     : interpolate(midColor,maxColor,midValue,maxValue,value)
                 : interpolate(midColor,maxColor,minValue,maxValue,value);
    }
    public Color interpolate(Color c1, Color c2, float v1, float v2, float v) {
      int rgb1 = c1.getRGB();
      int rgb2 = c2.getRGB();
      int a1 = (rgb1 >> 24) & 0xff;
      int r1 = (rgb1 >> 16) & 0xff;
      int g1 = (rgb1 >>  8) & 0xff;
      int b1 = (rgb1      ) & 0xff;
      int da = ((rgb2 >> 24) & 0xff) - a1;
      int dr = ((rgb2 >> 16) & 0xff) - r1;
      int dg = ((rgb2 >>  8) & 0xff) - g1;
      int db = ((rgb2      ) & 0xff) - b1;
      float rel = (v - v1) / (v2 - v1);
      int rgb = (((int) (a1 + da * rel)) << 24) |
                (((int) (r1 + dr * rel)) << 16) |
                (((int) (g1 + dg * rel)) <<  8) |
                (((int) (b1 + db * rel))      );
      return new Color(rgb);
    }
  }

/*
  class TableStats() {
    TableModel tm;
    public TableStats(TableModel tm) {
      this.tm = tm;
    }
    public Number getMin() {
      return null;
    }
    public Number getMax() {
      return null;
    }
  }
*/

  class ColorScale extends JComponent {
    BufferedImage negImg = createGradientImage(Color.GREEN, Color.BLACK);
    BufferedImage posImg = createGradientImage(Color.BLACK, Color.RED);
    /**
     * Creates a gradient image given specified <CODE>Color</CODE>(s)
     * @param color1 <CODE>Color</CODE> to display at left side of gradient
     * @param color2 <CODE>Color</CODE> to display at right side of gradient
     * @return returns a gradient image
     */
    private BufferedImage createGradientImage(Color color1, Color color2) {
        BufferedImage image = (BufferedImage)java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(256,1);       
        Graphics2D graphics = image.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, color1, 255, 0, color2);
        graphics.setPaint(gp);
        graphics.drawRect(0, 0, 255, 1);
        return image;
    }
    public void paintComponent(Graphics g) {
      // g.drawImage(negImage, 0, insets.top, negImage.getWidth(), GRAD_HEIGHT, null);^M
      // g.drawImage(posImage, insets.left+negImage.getWidth()+1, insets.top, posImage.getWidth(), GRAD_HEIGHT, null);^M
      super.paintComponent(g);
      int mid = getWidth()/2;
      Graphics2D g2 = (Graphics2D)g;
      GradientPaint gp = new GradientPaint(0, 0, Color.GREEN, mid, 0, Color.BLACK);
      g2.setPaint(gp);
      g2.fillRect(0, 0, mid+1, getHeight());
      gp = new GradientPaint(mid, 0, Color.BLACK, getWidth(), 0, Color.RED);
      g2.setPaint(gp);
      g2.fillRect(mid+1, 0, mid+1, getHeight());
    }
  }
  class MatRenderer extends javax.swing.table.DefaultTableCellRenderer {
    float scaleFactor = 1.f;
    float minValue = -1f;
    float maxValue = 1f;
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {
        Color bg = Color.lightGray;
        try {
          if (value != null && value instanceof Integer) {
            float fval = ((Number)value).floatValue() / (float)nClusters;
            bg = Color.getHSBColor(fval,.6f,.8f);
          } else if (value != null && value instanceof Number) {
            float fval = ((Number)value).floatValue();
            if (!Float.isNaN(fval)) {
              fval *= scaleFactor;
              float red = fval < 0f ? 0 : fval > 1f ? 1f : fval;
              float grn = fval < -1f ? 1f : fval >= 0f ? 0f : - fval;
              bg = new Color(red,grn,0f);
            }
          }
          setToolTipText(value != null ? value.toString() : null);
          setValue(null);
        } catch (Exception ex) {
          System.err.println(value + "  " + ex);
          ex.printStackTrace();
          setValue(value);
        }
        super.setBackground(bg);
        //setValue(value);
        return this;
    }
    public void setScaleFactor(float scaleFactor) {
      this.scaleFactor = scaleFactor;
    }
  }
  class ToolTipListCellRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list,
                                              Object value,
                                              int index,
                                              boolean isSelected,
                                              boolean cellHasFocus) {
      JComponent comp = (JComponent)super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
      comp.setToolTipText(value != null ? value.toString() : null);
      return comp;
      //setToolTipText(value != null ? value.toString() : null);
      //return super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
    }
  }
  class ToolTipTableCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
                          boolean isSelected, boolean hasFocus, int row, int column) {
      JComponent comp = (JComponent)super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
      comp.setToolTipText(value != null ? value.toString() : null);
      return comp;
    }
  }

  class RowListModel extends AbstractListModel {
    int ci = 0;
    IndexMap map = null;
    public void setColumn(int columnIndex) {
      this.ci = columnIndex;
    }
    public void setIndexMap(IndexMap indexMap) {
      map = indexMap;
    }
    public int getSize() {
      return tm != null ? tm.getRowCount() : 0;
    }
    public Object getElementAt(int index) {
      int ri = index;
      if (map != null) {
        ri = map.getSrc(ri);
      }
      return tm != null ? tm.getValueAt(ri, ci) : null;
    }
    public Object getPrototypeValue() {
      Object val = " ";
      for (int i = Math.min(getSize()-1,20); i >= 0; i--) {
        Object v = getElementAt(i);
        if (v != null && v.toString().length() > val.toString().length()) {
          val = v;
        }
      }
      return val;
    }
  }
  int nClusters = 10;
  ClutoSolution clutoSolution;
  DefaultTableCellRenderer tcr = new ToolTipTableCellRenderer();
  ClutoTree rowTree;
  JTable jt = null; 
  VirtualTableModelProxy tbl;
  TableContext ctx = null;
  TableModel tm = null;
  ListSelectionModel lsm = null;
  DefaultComboBoxModel labelModel = new DefaultComboBoxModel();
  JComboBox labelChoice = new JComboBox(labelModel);
  RowListModel rowLabelModel = new RowListModel();
  JList rowLabel = new JList(rowLabelModel);
  JScrollPane jsp = null;
  JPanel treeP = new JPanel(new BorderLayout());
  JPanel leftP = new JPanel(new BorderLayout());
  IndexMapSelection ims = null;
  IndexMapSelection imst = null;

  public ClutoMatrixView(ClutoSolution clutoSolution, TableModel clutoMatrix,
                         TableContext tableContext, 
                         TableModel tableModel, FontMetrics fm) {
    this(clutoSolution, clutoMatrix, tableContext, tableModel, fm, true, 1.f); 
  }

  public ClutoMatrixView(ClutoSolution clutoSolution, TableModel clutoMatrix,
                         TableContext tableContext, 
                         TableModel tableModel, FontMetrics fm, 
                         boolean showColor, float scaleFactor) {
    this.clutoSolution = clutoSolution;
    ctx = tableContext;
    tm = tableModel;
    lsm = ctx.getRowSelectionModel(tm);

    nClusters = clutoSolution.getParams().getNumClusters();
    // labelModel
    int ncol = tm.getColumnCount();
    for (int i = 0; i < ncol; i++) {
      labelModel.addElement(tm.getColumnName(i));
    }

    setLayout(new BorderLayout());

    rowTree = new ClutoTree(clutoSolution,tableContext,tableModel);
    rowTree.setToolbar(false);
    rowTree.setShowLabels(false);
    rowTree.setShowScale(false);

    rowLabelModel.setIndexMap(rowTree.getIndexMap());

    ims = new IndexMapSelection(lsm, rowLabel.getSelectionModel(), rowTree.getIndexMap());

    tbl = new VirtualTableModelProxy(clutoMatrix);


/*
    int[] colOrder = clutoSolution.getColTreeOrder();
    if (colOrder != null) {
      for (int i = tbl.getColumnCount()-1; i >= 0; i--) {
        tbl.removeColumn(i);
      }
      for (int i = 0; i < colOrder.length; i++) {
        tbl.addColumn(colOrder[i]);
      }
    }
*/

    ArrayRefColumn blank = new ArrayRefColumn(new Object[0]);
    tbl.addColumn(blank);

    ArrayRefColumn part = new ArrayRefColumn(clutoSolution.getParts());
    part.setName("Cluster ID");
    tbl.addColumn(part);

    tbl.setIndexMap(rowTree.getIndexMap());

    jt = new JTable(tbl);
    imst = new IndexMapSelection(lsm, jt.getSelectionModel(), rowTree.getIndexMap());
    jt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    if (showColor) {
      MatRenderer matRenderer = new MatRenderer();
      matRenderer.setScaleFactor(scaleFactor);
      jt.setDefaultRenderer(Float.class, matRenderer);
      jt.setDefaultRenderer(Integer.class, new MatRenderer());
      jt.setShowGrid(false);
      jt.setIntercellSpacing(new Dimension(0,0));
    }
    tcr.setUI(new VerticalLabelUI(true));
    jt.getTableHeader().setDefaultRenderer(tcr);
    jt.getTableHeader().setReorderingAllowed(false);
    jt.getTableHeader().setFont(jt.getFont());
    for (Enumeration e = jt.getColumnModel().getColumns(); e.hasMoreElements();) {
      ((TableColumn)e.nextElement()).setMinWidth(1);
    }

    setHeader(fm);

    // Label the rows 
    rowLabel.setFixedCellHeight(jt.getRowHeight());
    rowLabel.setCellRenderer(new ToolTipListCellRenderer());

    labelChoice.setToolTipText("Choose row label");
    labelChoice.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED ) {
            int labelColumn = 0;
            String ln = (String)e.getItem();
            if (ln != null) {
              for (int c = 0; c < tm.getColumnCount(); c++) {
                if (ln.equals(tm.getColumnName(c))) {
                  labelColumn = c;
                  break;
                }
              }
            }
            rowLabelModel.setColumn(labelColumn);
            // Reset the cell renderer to force a resize
            rowLabel.setCellRenderer(new ToolTipListCellRenderer());
            rowLabel.setFixedCellHeight(jt.getRowHeight());
            rowLabel.revalidate();
            validateThis();
          }
        }
      });

    JPanel ctrl = new JPanel(new BorderLayout()); 
    try {
      int rh = jt.getRowHeight();
      Class.forName("javax.swing.JSpinner");
      JComponent fontSize = DoSpinner.getComponent(rh, 1, 60, 1);
      fontSize.setToolTipText("Set the font size");
      ChangeListener changeListener = new ChangeListener () {
          public void stateChanged(ChangeEvent e) {
            try {
             setRowHeight( ((Number)DoSpinner.getValue(e.getSource())).intValue() );
            } catch (Exception ex) {
            }
          }
        };
      DoSpinner.addChangeListener(fontSize, changeListener);
      JComponent colSize = DoSpinner.getComponent(rh, 1, 60, 1);
      colSize.setToolTipText("Set the font size");
      ChangeListener colChangeListener = new ChangeListener () {
          public void stateChanged(ChangeEvent e) {
            try {
             setColumnWidth( ((Number)DoSpinner.getValue(e.getSource())).intValue() );
            } catch (Exception ex) {
            }
          }
        };
      DoSpinner.addChangeListener(colSize, colChangeListener);
      JPanel sCtrl = new JPanel(new BorderLayout());
      sCtrl.add(fontSize,BorderLayout.WEST);
      sCtrl.add(colSize,BorderLayout.EAST);
      sCtrl.setToolTipText("Set the cell size");
      if (showColor) {
        ColorScale colorScale = new ColorScale();
        colorScale.setBackground(Color.YELLOW);
        sCtrl.add(colorScale);
      }

      ctrl.add(sCtrl, BorderLayout.NORTH);
    } catch (Exception ex) {
    }

    ctrl.add(labelChoice,BorderLayout.EAST);
    JPanel ctrlP = new JPanel(new BorderLayout());
    ctrlP.add(ctrl,BorderLayout.SOUTH);
    
    jsp = new JScrollPane(jt);

    jsp.setCorner(JScrollPane.UPPER_LEFT_CORNER,ctrlP);

    rowTree.setPreferredSize(new Dimension(100 + (jt.getRowCount()/20),
                                           jt.getPreferredSize().height));
    
    treeP.setBackground(Color.white);
    treeP.add(rowTree,BorderLayout.WEST);
    treeP.add(rowLabel,BorderLayout.EAST);

    leftP.add(treeP,BorderLayout.NORTH);

    jsp.setRowHeaderView(leftP);
    tcr.setBackground(Color.white);
    //jt.setBackground(Color.white);
    //jt.getTableHeader().setBackground(Color.white);
    add(jsp);
  }

/*
  int[] colTreeOrder = clutoSolution.getColTreeOrder();
  public boolean getColumnsInTreeOrder() {
    return columnsAreInTreeOrder;
  }
  public void setColumnsInTreeOrder(boolean useTreeOrder) {
    if (useTreeOrder && !columnsAreInTreeOrder) {
      if (colTreeOrder != null) {
        for (int i = colTreeOrder.length-1; i >= 0; i--) {
          tbl.removeColumn(i);
        }
        for (int i = colTreeOrder.length-1; i >= 0; i--) {
          tbl.addColumn(i);
        }
      }
    } else if (!useTreeOrder && columnsAreInTreeOrder) {
      if (colTreeOrder != null) {
        for (int i = colTreeOrder.length-1; i >= 0; i--) {
          tbl.removeColumn(i);
        }
      }
    }
    columnsAreInTreeOrder = useTreeOrder;
  }
*/

  public void setCellSize() {
    rowLabel.setFixedCellHeight(jt.getRowHeight());
    rowTree.setPreferredSize(new Dimension(100 + (jt.getRowCount()/20),
                                           jt.getPreferredSize().height));
  }

  public void setFontSize(int size) {
    try {
      Font font = getFont();
      Graphics g = getGraphics();
      font = font.deriveFont((float)(size < 1 ? 1 : size));
      for (int s = size; s > 0 && g.getFontMetrics(font).getHeight() > size; s--) {
        font = font.deriveFont((float)(s));
      }
      setFont(font);
      jt.setFont(getFont());
      rowLabel.setFont(getFont());
      jt.getTableHeader().invalidate();
      jt.invalidate();
      validate();
      repaint();
    } catch (Exception ex) {
    }
  }

  private void validateThis() {
    revalidate();
    repaint();
  }

  public void setRowHeight(int size) {
    int rh = size < 1 ? 1 : size;
    jt.setRowHeight(rh);
    setCellSize();
    setFontSize(size);
    validate();
    repaint();
  }

  public void setColumnWidth(int size) {
    for (Enumeration e = jt.getColumnModel().getColumns(); e.hasMoreElements();) {
      ((TableColumn)e.nextElement()).setPreferredWidth(size);
    }
    setCellSize();
    validate();
    repaint();
  }

  private void setHeader(FontMetrics fm) {
    // Find the width in pixels for the longest column name and set header
    int w = jt.getRowHeight();
    int top = w/2;
    if (fm != null) {
      int h = w;
      for (Enumeration e = jt.getColumnModel().getColumns(); e.hasMoreElements();) {
        TableColumn tc = (TableColumn)e.nextElement();
        tc.setPreferredWidth(w);
        Object hv = tc.getHeaderValue();
        if (fm != null && hv != null) {
          int hw = fm.stringWidth(hv.toString());
          if (hw > h) {
            h = hw;
          }
        }
      }
      h += tcr.getInsets().top + tcr.getInsets().bottom;
      h += jt.getTableHeader().getInsets().top + jt.getTableHeader().getInsets().bottom;
      jt.getTableHeader().setPreferredSize(new Dimension(h,h));
    }
  }

  public void cleanUp() {
    if (rowTree != null) {
      rowTree.cleanUp();
    }
    if (ims != null) {
      ims.cleanUp();
    }
    if (imst != null) {
      imst.cleanUp();
    }
  }

}
