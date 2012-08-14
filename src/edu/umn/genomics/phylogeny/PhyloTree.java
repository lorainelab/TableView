package edu.umn.genomics.phylogeny;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import edu.umn.genomics.graph.*;
import edu.umn.genomics.graph.swing.*;
import edu.umn.genomics.component.FontChooser;
import edu.umn.genomics.table.ExceptionHandler;

public class PhyloTree extends JPanel {  
  TreeModel treeModel;
  TreeSelectionModel tsm;
  NodeShapes nodeShapes = null;
  boolean showLabels = false;
  boolean showInset = false;

  Vector phylogeNodes = null;
  
  SimpleGraph graph;
  GraphSegments gs;
  GraphPoints gp;
  GraphRects  gr; // inset rectangle
  PhyloTree inset = null;

  ListSelectionModel lsm = null;

  ListSelectionListener lsl = new ListSelectionListener() {
    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting() && graph != null) {
        graph.repaint(); 
      }
    }
  };

  MouseInputAdapter mia = new MouseInputAdapter() {
    Point p1 = null;
    public void mousePressed(MouseEvent e) {
      p1 = e.getPoint();
    }
    public void mouseReleased(MouseEvent e) {
      Point p2 = e.getPoint();
      ListSelectionModel lsm = getSelectionModel();
      if (graph != null && gs != null && lsm != null) {
        int[] ia = gs.getIndicesAt(
                     new Rectangle(p1.x,p1.y,p2.x-p1.x, p2.y-p1.y), 
                     graph.getAxisDisplay(BorderLayout.SOUTH).getAxis(),
                     graph.getAxisDisplay(BorderLayout.WEST).getAxis());
                            
         lsm.setValueIsAdjusting(true);
         lsm.clearSelection();
         if (ia != null) {
           for(int i = 0; i < ia.length; i++) {
             lsm.addSelectionInterval(ia[i],ia[i]);
           }
         }
         lsm.setValueIsAdjusting(false);
      }
    }
  };


  AxisListener insetListener = new AxisListener() {
    public void axisChanged(AxisEvent e) {
      setInsetData();
    }
  };

  IndexedDefaultDrawable nodeLabels  = new IndexedDefaultDrawable() {
    int getSize() {
      return nodeShapes.getSize();
    }
    public Drawable get(int index) {
      return new DrawableText(nodeShapes.getLabel(index)); 
    }
    public void draw(Graphics g, int x, int y, int index, Drawable defaultDrawable) {
      if (nodeShapes != null) {
        String s = nodeShapes.getLabel(index);  
        if (s != null && s.length() > 0) {
          double seg[] = nodeShapes.getDataPoints();
          double dx = seg[index*4+2] - seg[index*4+0];
          double dy = seg[index*4+3] - seg[index*4+1];
          FontMetrics fm = g.getFontMetrics();
          int nx = dx < 0 ? x - fm.stringWidth(s) - 1 : x + 1;
          int ny = dy < 0 ? y + fm.getAscent() : y - fm.getDescent();
          g.drawString(s,nx,ny);
        }
      }
    }
  };

  // int leafCount;
  // 
  // Style phylodiagram, phenogram, cladogram, eurogram, curvogram, swoopogram
  // 
  // JButton	// Background
  // JButton	// Foreground
  // JButton	// Selected Color
  // JButton	// Font
  // JButton	// Selected Font
  // JCheckBox	// Show Node Labels
  // 
  /*
   JPanel viewPrefs 
   */
  /*
   move labels
   select nodes
   MouseAdapter
   */
  public PhyloTree() {
    setLayout(new BorderLayout());
    graph = new SimpleGraph();
    //graph.setAspectRatio(1.);
    graph.getGraphDisplay().setOpaque(true);
    graph.getGraphDisplay().setBackground(Color.white);
    graph.showGrid(false);
    graph.getAxisDisplay(BorderLayout.SOUTH).setZoomable(true);
    graph.getAxisDisplay(BorderLayout.WEST).setZoomable(true);
    graph.getGraphDisplay().addMouseListener(mia);
    gs = new GraphSegments();
    gs.setColor(Color.black);
    gp = new GraphPoints();
    gp.setColor(Color.black);
    gr = new GraphRects();
    gr.setFillRects(false);
    gr.setColor(Color.lightGray);
    showLabels(true);
    showInset(showInset);
    graph.addGraphItem(gs);
    add(graph);
    JToolBar tb = new JToolBar();

    JCheckBox lblBtn = new JCheckBox("Show Labels",showLabels);
    lblBtn.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            showLabels(e.getStateChange() == ItemEvent.SELECTED);
          }
        }
      );
    tb.add(lblBtn);

    JCheckBox insBtn = new JCheckBox("Show Inset",showInset);
    insBtn.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            showInset(e.getStateChange() == ItemEvent.SELECTED);
          }
        }
      );
    tb.add(insBtn);

    JButton fontBtn = new JButton("Font");
    fontBtn.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
            FontChooser fc = new FontChooser((Frame)((JComponent)ae.getSource()).getTopLevelAncestor(),
                               "Set Node Label Font", graph.getGraphDisplay().getFont(), null);
            fc.setVisible(true);
          System.err.println("old font: " + graph.getGraphDisplay().getFont());
            graph.getGraphDisplay().setFont(fc.getNewFont());
          System.err.println("new font: " + graph.getGraphDisplay().getFont());
          }
        }
      );
    tb.add(fontBtn);
    

    add(tb,BorderLayout.NORTH);
    Font f = getFont();
    if (f != null) {
      setFont(new Font(f.getFontName(),f.getStyle(),8));
    } else {
      setFont(new Font("Helvetica", Font.PLAIN,8));
    }
    setSelectionModel(new DefaultListSelectionModel());
  }

  public void setTreeModel(TreeModel model) {
    this.treeModel = model;
    makeShapes();
  }  
  public TreeModel getTreeModel() {
    return treeModel;
  }  

  
  /**
   * Sets the row selection model for this table to newModel and registers
   * with for listener notifications from the new selection model.
   * @param newModel the new selection model
   */
  public void setSelectionModel(ListSelectionModel newModel) {
    lsm = newModel;
    lsm.addListSelectionListener(lsl); 
    gs.setIndexedColor(new IndexSelectColor(Color.cyan, null, lsm));
    gp.setIndexedColor(new IndexSelectColor(Color.blue, null, lsm));
  }

  /**
   * Returns the ListSelectionModel that is used to maintain row
   * selection state.
   * @return the object that provides row selection state.
   */
  public ListSelectionModel getSelectionModel() {
    return lsm;
  } 

  public java.util.List getPhylogeNodes() {
    return phylogeNodes;
  }

  SimpleGraph getGraph() {
    return graph;
  }

  public void showLabels(boolean show) {
    if (showLabels != show) {
      showLabels = show;
      if (showLabels) {
        graph.addGraphItem(gp);
      } else {
        graph.removeGraphItem(gp);
      }
    }
  }

  private void setInsetData() {
    if (inset != null) {
        double[] r = new double[4];
        double xmin = inset.getGraph().getAxisDisplay(BorderLayout.SOUTH).getAxis().getMin();
        double xmax = inset.getGraph().getAxisDisplay(BorderLayout.SOUTH).getAxis().getMax();
        double ymin = inset.getGraph().getAxisDisplay(BorderLayout.WEST).getAxis().getMin();
        double ymax = inset.getGraph().getAxisDisplay(BorderLayout.WEST).getAxis().getMax();
        r[0] = xmin;
        r[1] = ymin;
        r[2] = xmax;
        r[3] = ymax;
        gr.setData(r,GraphDataModel.FORMAT_XY);
     }
  }

  public void showInset(boolean show) {
    if (showInset != show) {
      showInset = show;
      if (showInset) {
        double[] r = new double[4];
        double xmin = graph.getAxisDisplay(BorderLayout.SOUTH).getAxis().getMin();
        double xmax = graph.getAxisDisplay(BorderLayout.SOUTH).getAxis().getMax();
        double ymin = graph.getAxisDisplay(BorderLayout.WEST).getAxis().getMin();
        double ymax = graph.getAxisDisplay(BorderLayout.WEST).getAxis().getMax();
        r[0] = xmin + .4 * (xmax - xmin);
        r[1] = ymin + .4 * (ymax - ymin);
        r[2] = xmin + .6 * (xmax - xmin);
        r[3] = ymin + .6 * (ymax - ymin);
        gr.setData(r,GraphDataModel.FORMAT_XY);
        graph.addGraphItem(gr);
        inset = new PhyloTree();
        inset.showLabels(showLabels);
        inset.setTreeModel(getTreeModel());
        inset.setSelectionModel(getSelectionModel());
        inset.getGraph().getAxisDisplay(BorderLayout.SOUTH).getAxis().setMin(r[0]);
        inset.getGraph().getAxisDisplay(BorderLayout.SOUTH).getAxis().setMax(r[2]);
        inset.getGraph().getAxisDisplay(BorderLayout.WEST).getAxis().setMin(r[1]);
        inset.getGraph().getAxisDisplay(BorderLayout.WEST).getAxis().setMax(r[3]);
        JFrame jf = new JFrame("Inset of PhyloTree");
        jf.getContentPane().add(inset);
        try {
          jf.setSize(getTopLevelAncestor().getSize());
          Point loc = getTopLevelAncestor().getLocation();
          loc.x += 5;
          loc.y += 5;
          jf.setLocation(loc);
        } catch (Exception ex) {
            ExceptionHandler.popupException(""+ex);
        }
        PhyloGenTree.setViewToolBar(jf,inset.getGraph().getGraphDisplay(),null);
        jf.setVisible(true);
        inset.getGraph().getAxisDisplay(BorderLayout.SOUTH).getAxis().addAxisListener(insetListener);
        inset.getGraph().getAxisDisplay(BorderLayout.WEST).getAxis().addAxisListener(insetListener);
      } else {
        graph.removeGraphItem(gr);
        try {
          ((Window)inset.getTopLevelAncestor()).dispose();
        } catch (Exception ex) {
            ExceptionHandler.popupException(""+ex);
        }
      }
    }
  }

  TreeModelListener tml = new TreeModelListener() { 
     // Called when one or more sibling nodes have changed in some way. 
     public void treeNodesChanged(TreeModelEvent e) {
     }
     // Called after nodes have been inserted into the tree. 
     public void treeNodesInserted(TreeModelEvent e) {
     }
     // Called after nodes have been removed from the tree. 
     public void treeNodesRemoved(TreeModelEvent e) {
     }
     // Called after the tree's structure has drastically changed. 
     public void treeStructureChanged(TreeModelEvent e) {
     }
  };

  // Convert the tree into shapes: line segment[s], nodes, labels
  public void makeShapes() {
    // java.
    // java.awt.geom.Ellipse2D.Double
    // java.awt.geom.Line2D.Double
    // Get leaf children number
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)getTreeModel().getRoot();
    int leafcnt = root.getLeafCount();
    int nodeCnt = 0;
    for (Enumeration e = root.preorderEnumeration();  e.hasMoreElements(); ) {
      e.nextElement();
      nodeCnt++;
    }
    NodeShapes shapes = new NodeShapes(nodeCnt);
    phylogeNodes = new Vector(nodeCnt);
    calcNode(root, 0., 0., 0., Math.PI,  Math.PI*2 / (leafcnt + 1), shapes);
    if (showLabels) {
      shapes.positionLabels();
    }
    nodeShapes = shapes;
    Rectangle2D bnds = nodeShapes.getNodeBounds();
    bnds.setRect(bnds.getX() - bnds.getWidth()*.1, bnds.getY() - bnds.getHeight()*.1, 
                 bnds.getWidth()*1.2, bnds.getHeight()*1.2);
    graph.getAxisDisplay(BorderLayout.SOUTH).getAxis().setMin(bnds.getMinX());
    graph.getAxisDisplay(BorderLayout.SOUTH).getAxis().setMax(bnds.getMaxX());
    graph.getAxisDisplay(BorderLayout.WEST).getAxis().setMin(bnds.getMinY());
    graph.getAxisDisplay(BorderLayout.WEST).getAxis().setMax(bnds.getMaxY());
    gs.setData(nodeShapes.getDataPoints(),GraphDataModel.FORMAT_XY);
    gp.setData(nodeShapes.getLabelPoints(),GraphDataModel.FORMAT_XY);
    gp.setIndexedDrawable(nodeLabels);
    // set gr rectangle
  }


  public double calcNode(DefaultMutableTreeNode node, double px, double py, double rootDist,
                         double theta, double thetaIncr, NodeShapes shapes) {
    double a = theta;
    int leafCnt = node.getLeafCount();
    PhylogeNode pn = (PhylogeNode)node.getUserObject(); 
    phylogeNodes.add(pn);
    double len = pn.getDistance();
    if (Double.isNaN(len))
      len = 0.;
    double curDist = rootDist + len;
    pn.setDistanceFromRoot(curDist);
    double na = a + thetaIncr*leafCnt*.5;
    double nx = px + Math.cos(na) * len;
    double ny = py + Math.sin(na) * len;
    shapes.setNode(nx, ny, px, py, pn.getName());
// System.err.println(" a= " + a + "\tl= " + len);
    if (node.isLeaf()) {
      a += thetaIncr;
    } else {
      for (Enumeration e = node.children() ; e.hasMoreElements(); ) {
        DefaultMutableTreeNode cn = (DefaultMutableTreeNode)e.nextElement(); 
        a = calcNode(cn, nx, ny, curDist, a, thetaIncr, shapes);
      }
    }
    return a;
  }

  class NodeShapes {
    int size = 0;
    int nodeIdx = 0;
    double maxLen = 0.;
    double minX = 0.;
    double minY = 0.;
    double maxX = 0.;
    double maxY = 0.;
    double minlX = 0.;
    double minlY = 0.;
    double maxlX = 0.;
    double maxlY = 0.;
    double[] data = null;
    double[] labelPos = null;
    String[] labels = null;
    Shape[] lines = null;
    Shape[] lptr = null; 
    NodeShapes(int nodeCount) {
      size = nodeCount;
      lines = new Shape[size];
      labels = new String[size];
      lptr = new Line2D.Double[size];
      data = new double[size*4];
      labelPos = new double[size*2];
    }
    public int getSize() {
      return size;
    }
    public double[] getDataPoints() {
      return data;
    }
    public double[] getLabelPoints() {
      return labelPos;
    }
    public String getLabel(int i) {
      if (labels != null && i >= 0 && i < labels.length)
        return labels[i];
      return null;
    }
    public Rectangle2D getNodeBounds() {
      return new Rectangle2D.Double(minX, minY, maxX-minX, maxY-minY);
    }
    public Rectangle2D getLabelBounds() {
      return new Rectangle2D.Double(minlX, minlY, maxlX-minlX, maxlY-minlY);
    }
    public void setNode(double x, double y, double px, double py, String label) {
      data[nodeIdx*4+0] = px; 
      data[nodeIdx*4+1] = -py; 
      data[nodeIdx*4+2] = x; 
      data[nodeIdx*4+3] = -y; 
      labelPos[nodeIdx*2+0] = x;
      labelPos[nodeIdx*2+1] = -y;
      Line2D.Double nL = new Line2D.Double(px, py, x, y);
      lines[nodeIdx] = nL;
      double len = nL.getP1().distance(nL.getP2());
      if (len > maxLen)
        maxLen = len;
      lptr[nodeIdx] = new Line2D.Double(x,y,x,y);
      labels[nodeIdx] = label; 
      //nx[nodeIdx] = x;
      //ny[nodeIdx] = y;
      if (nodeIdx == 0) {
        minX = x;
        maxX = x;
        minY = -y;
        maxY = -y;
      } else {
        if (px < minX)
          minX = px;
        else if (px > maxX) 
          maxX = px;
        if (-py < minY)
          minY = -py;
        else if (-py > maxY)
          maxY = -py;
        if (x < minX)
          minX = x;
        else if (x > maxX) 
          maxX = x;
        if (-y < minY)
          minY = -y;
        else if (-y > maxY)
          maxY = -y;
      }
// System.err.println(nodeIdx + "\t" + label + "\t" + px + "\t" + py + "\t" + x + "\t" + y);
      nodeIdx++;
    }

    public void positionLabels() {
      int lw = 0;
      minlX = minX;
      minlY = minY;
      maxlX = maxX;
      maxlY = maxY;
      for (int i = 0; i < size; i++) {
        if (labels[i] != null) {
          int l = labels[i].length();
          if (l > 0) {
            if (l > lw) {
              lw = l;
            }
            Line2D.Double nLine = (Line2D.Double)lines[i];
            double nLen = nLine.getP1().distance(nLine.getP2());
            double lLen = maxLen - nLen;
            if (lLen > 0.) {
              double dx = nLine.getX2() - nLine.getX1();
              double dy = nLine.getY2() - nLine.getY1();
              double m = dy/dx;
              Line2D.Double lLine = (Line2D.Double)lptr[i];
              double x = nLine.getX2() + maxLen/nLen * dx;
              double y = nLine.getY2() + maxLen/nLen * dy;
              //labelPos[i*2+0] = x;
              //labelPos[i*2+1] = y;
              if (x < minlX)
                minlX = x;
              else if (x > maxlX)
                maxlX = x;
              if (y < minlY)
                minlY = y;
              else if (y > maxlY)
                maxlY = y;
              lLine.setLine(x, y, nLine.getX2(), nLine.getY2());
            }
          }
        }
      }
    }
  }
}
