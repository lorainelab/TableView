/*
 * @(#) $RCSfile: SQueeLer.java,v $ $Revision: 1.15 $ $Date: 2004/08/02 20:23:39 $ $Name: TableView1_3 $
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

import java.util.*;
import java.sql.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import edu.umn.genomics.bi.dbutil.*;
import edu.umn.genomics.component.DoSpinner;

/*
DBAccts - 
  dbaccnt -  String acctName
    AccountInfo
    QueryHistory
      date rows query 
    ConnectNode - Connection
      metadata DatabaseMetaData
      schema/catalog
        table
          info
            indices 
            pKey
            fKey
          rowcount
          columns
            column

  VisualQueryBuilder
     
    +------------------+
         TableName 
    +------------------+
     PK  COLUMNNAME 
     IFK ColumnName  
         ColumnName EFK
         ColumnName UKey
         ColumnName 
         ColumnName 
        Min Name Max Null 
    +------------------+

UsrDesignatedForeignKey
Field Comment
FieldLink
IndexInfo

*/

/**
 * Browse the contents of a database, and supply a TabelModel 
 * for the table selected.
 *
 * @author       J Johnson
 * @version $Revision: 1.15 $ $Date: 2004/08/02 20:23:39 $  $Name: TableView1_3 $
 * @since        1.0
 */
public class SQueeLer extends JPanel {
  class ConnectNode extends DefaultMutableTreeNode {
    ConnectNode() {
      setAllowsChildren(true);
    }
    public boolean isLeaf() {
      return false;
    }
    public String toString() {
      Object o = getUserObject();
      if (o != null) {
        if (o instanceof Connection) {
          try {
            if ( ((Connection)o).isClosed()) {
              return "Connection Closed";
            } else {
              return "Connected       ";
            }
          } catch (Exception ex) {
            return ex.toString();
          }
        } else {
          return o.toString();
        }
      }
      return "Connect          ";
    }
  }
/*
  class DBAcctNode extends DefaultMutableTreeNode {
    Connection = null;
    DBAcctNode(String acctName) throws Exception {
      setUserObject("acctName");
    }
  }
*/
  class DBAccts extends DefaultMutableTreeNode {
    DBAccts(DBAccountListModel dbmodel) throws Exception {
      setUserObject("Database Accounts");
      String[] accts = dbmodel.getAccountNames();
      for (int i = 0; i < accts.length; i++) {
        DefaultMutableTreeNode acctNode = new DefaultMutableTreeNode(accts[i],true);
        acctNode.add(new ConnectNode());
        add(acctNode);
      }
    }
  }

  class ConnectThread extends Thread {
    ConnectNode cn;
    ConnectThread(ConnectNode node) {
      cn = node;
    }
    public void run() {
System.err.println("connect start");
      connect(cn);
System.err.println("connect done");
    }
  }

class CBRenderer extends DefaultTreeCellRenderer {

    public CBRenderer() {
    }

    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                        tree, value, sel,
                        expanded, leaf, row,
                        hasFocus);
        return this;
    }
}


  // 
  int loginTimeout = 10;
  // known database accounts
  DBAccountListModel dbmodel;
  DBConnectParams dbuser;

  public void addTableNodes(DefaultMutableTreeNode tNode, DatabaseMetaData dbmd) {
    try {
      java.util.List tblList = DBTable.getDBTables(dbmd,null,null,null,new String[] {"TABLE", "VIEW"});
      if (tblList != null) {
        Hashtable catalogMap = new Hashtable();
        Hashtable schemaMap = new Hashtable();
        for (Iterator i = tblList.listIterator(); i.hasNext();) {
          DBTable tbl = (DBTable)i.next();
          String cName = tbl.getCatalogName();
          if (tbl.getCatalogName() != null) {
            DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)catalogMap.get(tbl.getCatalogName());
            if (cNode == null) {
              cNode = new DefaultMutableTreeNode(tbl.getCatalogName());
              tNode.add(cNode);
              catalogMap.put(tbl.getCatalogName(),cNode);
            }
            if (tbl.getSchemaName() != null) {
              DefaultMutableTreeNode sNode = (DefaultMutableTreeNode)schemaMap.get(tbl.getSchemaName());
              if (sNode == null) {
                sNode = new DefaultMutableTreeNode(tbl.getSchemaName());
                cNode.add(sNode);
                schemaMap.put(tbl.getSchemaName(),sNode);
              }
              sNode.add(new DefaultMutableTreeNode(tbl));
            } else {
              cNode.add(new DefaultMutableTreeNode(tbl));
            }
          } else if (tbl.getSchemaName() != null) {
            DefaultMutableTreeNode sNode = (DefaultMutableTreeNode)schemaMap.get(tbl.getSchemaName());
            if (sNode == null) {
              sNode = new DefaultMutableTreeNode(tbl.getSchemaName());
              tNode.add(sNode);
              schemaMap.put(tbl.getSchemaName(),sNode);
            }
            sNode.add(new DefaultMutableTreeNode(tbl));
          } else {
            tNode.add(new DefaultMutableTreeNode(tbl));
          }
        }
      }
    } catch (Exception ex) {
      tNode.setUserObject("Tables" + ex);
      ExceptionHandler.popupException(""+ex);
    }
  }

  public void connect(ConnectNode tn) {
        String acctName = (String)((DefaultMutableTreeNode)tn.getParent()).getUserObject();
        try {
          repaint();
System.err.println("conn");
          Connection conn = dbmodel.getConnection(acctName);
          tn.setUserObject(conn);
          repaint();
System.err.println("dbmd");
          DatabaseMetaData dbmd = conn.getMetaData();
System.err.println("tbls");
          // tn.add(new DefaultMutableTreeNode(dbmd));
          DefaultMutableTreeNode tNode = new DefaultMutableTreeNode("Tables");
          java.util.List tblList = DBTable.getDBTables(dbmd,null,null,null,new String[] {"TABLE", "VIEW"});
System.err.println("tbln");
          if (tblList != null) {
            Hashtable catalogMap = new Hashtable();
            Hashtable schemaMap = new Hashtable();
            for (Iterator i = tblList.listIterator(); i.hasNext();) {
              DBTable tbl = (DBTable)i.next();
              String cName = tbl.getCatalogName();
              if (tbl.getCatalogName() != null) {
                DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)catalogMap.get(tbl.getCatalogName());
                if (cNode == null) {
                  cNode = new DefaultMutableTreeNode(tbl.getCatalogName());
                  tNode.add(cNode);
                  catalogMap.put(tbl.getCatalogName(),cNode);
                }
                if (tbl.getSchemaName() != null) {
                  DefaultMutableTreeNode sNode = (DefaultMutableTreeNode)schemaMap.get(tbl.getSchemaName());
                  if (sNode == null) {
                    sNode = new DefaultMutableTreeNode(tbl.getSchemaName());
                    cNode.add(sNode);
                    schemaMap.put(tbl.getSchemaName(),sNode);
                  }
                  sNode.add(new DefaultMutableTreeNode(tbl));
                } else {
                  cNode.add(new DefaultMutableTreeNode(tbl));
                }
              } else if (tbl.getSchemaName() != null) {
                DefaultMutableTreeNode sNode = (DefaultMutableTreeNode)schemaMap.get(tbl.getSchemaName());
                if (sNode == null) {
                  sNode = new DefaultMutableTreeNode(tbl.getSchemaName());
                  tNode.add(sNode);
                  schemaMap.put(tbl.getSchemaName(),sNode);
                }
                sNode.add(new DefaultMutableTreeNode(tbl));
              } else {
                tNode.add(new DefaultMutableTreeNode(tbl));
              }
            }
          }
          treeModel.insertNodeInto(tNode,tn,0);
System.err.println("done");
          // tn.add(tNode);
          repaint();
        } catch (Exception ex) {
          tn.setUserObject(ex);
          ExceptionHandler.popupException(""+ex);
        }
      }


  TreeWillExpandListener twel = new TreeWillExpandListener() {
    // If Connect node, open connection
    public void treeWillExpand(TreeExpansionEvent e) 
                    throws ExpandVetoException {
            saySomething("Tree-will-expand event detected", e);
      DefaultMutableTreeNode tn = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
      if (tn instanceof ConnectNode) {
        new ConnectThread((ConnectNode) tn).start();
        // connect((ConnectNode) tn);
/*
        String acctName = (String)((DefaultMutableTreeNode)tn.getParent()).getUserObject();
        try {
          Connection conn = dbmodel.getConnection(acctName);
          tn.setUserObject(conn);
          DatabaseMetaData dbmd = conn.getMetaData();
          // tn.add(new DefaultMutableTreeNode(dbmd));
          DefaultMutableTreeNode tNode = new DefaultMutableTreeNode("Tables");
          tn.add(tNode);
          java.util.List tblList = DBTable.getDBTables(dbmd,null,null,null,new String[] {"TABLE", "VIEW"});
          if (tblList != null) {
            Hashtable catalogMap = new Hashtable();
            Hashtable schemaMap = new Hashtable();
            for (Iterator i = tblList.listIterator(); i.hasNext();) {
              DBTable tbl = (DBTable)i.next();
              String cName = tbl.getCatalogName();
              if (tbl.getCatalogName() != null) {
                DefaultMutableTreeNode cNode = (DefaultMutableTreeNode)catalogMap.get(tbl.getCatalogName());
                if (cNode == null) {
                  cNode = new DefaultMutableTreeNode(tbl.getCatalogName());
                  tNode.add(cNode);
                  catalogMap.put(tbl.getCatalogName(),cNode);
                }
                if (tbl.getSchemaName() != null) {
                  DefaultMutableTreeNode sNode = (DefaultMutableTreeNode)schemaMap.get(tbl.getSchemaName());
                  if (sNode == null) {
                    sNode = new DefaultMutableTreeNode(tbl.getSchemaName());
                    cNode.add(sNode);
                    schemaMap.put(tbl.getSchemaName(),sNode);
                  }
                  sNode.add(new DefaultMutableTreeNode(tbl));
                } else {
                  cNode.add(new DefaultMutableTreeNode(tbl));
                }
              } else if (tbl.getSchemaName() != null) {
                DefaultMutableTreeNode sNode = (DefaultMutableTreeNode)schemaMap.get(tbl.getSchemaName());
                if (sNode == null) {
                  sNode = new DefaultMutableTreeNode(tbl.getSchemaName());
                  tNode.add(sNode);
                  schemaMap.put(tbl.getSchemaName(),sNode);
                }
                sNode.add(new DefaultMutableTreeNode(tbl));
              } else {
                tNode.add(new DefaultMutableTreeNode(tbl));
              }
            }
          }
        } catch (Exception ex) {
          tn.setUserObject(ex);
        }
*/
      }
    }

    // If Connect node, close connection
    public void treeWillCollapse(TreeExpansionEvent e) {
      DefaultMutableTreeNode tn = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
      if (tn instanceof ConnectNode) {
        ConnectNode cn = (ConnectNode)tn;
        Object o = cn.getUserObject();
        if (o instanceof Connection) {
          closeConnection((Connection)o);
        }
        cn.setUserObject(null);
        cn.removeAllChildren();
        repaint();
      }
      saySomething("Tree-will-collapse event detected", e);
    }
    public void saySomething(String s, TreeExpansionEvent e) {
      System.err.println(s + e);
    }
  };

  TreeSelectionListener tsl = new TreeSelectionListener() {
    public void valueChanged(TreeSelectionEvent e) {
      try {
        Object o = ((DefaultMutableTreeNode)e.getPath().getLastPathComponent()).getUserObject();
        // treeSelect(o);
      } catch(Exception ex) {
          ExceptionHandler.popupException(""+ex);
      }
    }
  };

  JTree dbtree;
  DefaultTreeModel treeModel;

  public void closeConnection(Connection conn) {
    try {
      // cancel Statements
      conn.close();
    } catch (Exception ex) {
        ExceptionHandler.popupException(""+ex);
    }
  }

  public SQueeLer() {
    setLayout(new BorderLayout());
    try {
      dbmodel = new DBAccountListModel();
      treeModel = new DefaultTreeModel(new DBAccts(dbmodel),true);
      dbtree = new JTree(treeModel);
      // dbtree = new JTree(new DBAccts(dbmodel));
      dbtree.addTreeSelectionListener(tsl);
      dbtree.addTreeWillExpandListener(twel);
      JScrollPane jsp = new JScrollPane(dbtree);
      add(jsp);
    } catch (Exception ex) {
        ExceptionHandler.popupException(""+ex);
    }
  }

  public static void main(String args[]) {
    SQueeLer sqlr = new SQueeLer();
    JFrame frame = new JFrame("Browse Database Tables");
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {System.exit(0);}
      public void windowClosed(WindowEvent e) {System.exit(0);}
    });
    frame.getContentPane().add(sqlr);
    JButton closeBtn = new JButton("Close");
    closeBtn.setToolTipText("Close this window");
    closeBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            ((Window)((JComponent)e.getSource()).getTopLevelAncestor()).dispose();
          } catch (Exception ex) {
              ExceptionHandler.popupException(""+ex);
          }
        }
      }
    );
    JToolBar tb = new JToolBar();
    tb.add(closeBtn);
    frame.getContentPane().add(tb,BorderLayout.NORTH);

    frame.pack();
    Dimension dim = frame.getSize();
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    dim.width = dim.width < screen.width-50 ? dim.width : screen.width-50;
    dim.height = dim.height < screen.height-50 ? dim.height : screen.height-50;
    frame.setSize(dim);
    frame.setVisible(true);
  }

}
