/*
 * @(#) $RCSfile$ $Revision$ $Date$ $Name$
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

import java.io.*;
import java.util.*;
import java.sql.*;
import java.math.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import edu.umn.genomics.bi.dbutil.*;
import edu.umn.genomics.component.DoSpinner;

/**
 * Upload a table to a database.
 *
 * @author       J Johnson
 * @version $Revision$ $Date$  $Name$
 * @since        1.0
 */
public class DBSaveTable extends JPanel {

  static String[] resWrds = { 
  "ABSOLUTE",
  "ACTION",
  "ADD",
  "ALL",
  "ALLOCATE",
  "ALTER",
  "AND",
  "ANY",
  "ARE",
  "AS",
  "ASC",
  "ASSERTION",
  "AT",
  "AUTHORIZATION",
  "AVG",
  "BEGIN",
  "BETWEEN",
  "BIT",
  "BIT_LENGTH",
  "BOTH",
  "BY",
  "CASCADE",
  "CASCADED",
  "CASE",
  "CAST",
  "CATALOG",
  "CHAR",
  "CHARACTER",
  "CHAR_LENGTH",
  "CHARACTER_LENGTH",
  "CHECK",
  "CLOSE",
  "COALESCE",
  "COLLATE",
  "COLLATION",
  "COLUMN",
  "COMMENT",
  "COMMIT",
  "CONNECT",
  "CONNECTION",
  "CONSTRAINT",
  "CONSTRAINTS",
  "CONTINUE",
  "CONVERT",
  "CORRESPONDING",
  "COUNT",
  "CREATE",
  "CROSS",
  "CURRENT",
  "CURRENT_DATE",
  "CURRENT_TIME",
  "CURRENT_TIMESTAMP",
  "CURRENT_USER",
  "CURSOR",
  "DATE",
  "DAY",
  "DEALLOCATE",
  "DEC",
  "DECIMAL",
  "DECLARE",
  "DEFAULT",
  "DEFERRABLE",
  "DEFERRED",
  "DELETE",
  "DESC",
  "DESCRIBE",
  "DESCRIPTOR",
  "DIAGNOSTICS",
  "DISCONNECT",
  "DISTINCT",
  "DOMAIN",
  "DOUBLE",
  "DROP",
  "ELSE",
  "END",
  "END-EXEC",
  "ESCAPE",
  "EXCEPT",
  "EXCEPTION",
  "EXEC",
  "EXECUTE",
  "EXISTS",
  "EXTERNAL",
  "EXTRACT",
  "FALSE",
  "FETCH",
  "FIRST",
  "FLOAT",
  "FOR",
  "FOREIGN",
  "FOUND",
  "FROM",
  "FULL",
  "GET",
  "GLOBAL",
  "GO",
  "GOTO",
  "GRANT",
  "GROUP",
  "HAVING",
  "HOUR",
  "IDENTITY",
  "IMMEDIATE",
  "IN",
  "INDICATOR",
  "INITIALLY",
  "INNER",
  "INPUT",
  "INSENSITIVE",
  "INSERT",
  "INT",
  "INTEGER",
  "INTERSECT",
  "INTERVAL",
  "INTO",
  "IS",
  "ISOLATION",
  "JOIN",
  "KEY",
  "LANGUAGE",
  "LAST",
  "LEADING",
  "LEFT",
  "LEVEL",
  "LIKE",
  "LOCAL",
  "LOWER",
  "MATCH",
  "MAX",
  "MIN",
  "MINUTE",
  "MODULE",
  "MONTH",
  "NAMES",
  "NATIONAL",
  "NATURAL",
  "NCHAR",
  "NEXT",
  "NO",
  "NOT",
  "NULL",
  "NULLIF",
  "NUMERIC",
  "OCTET_LENGTH",
  "OF",
  "ON",
  "ONLY",
  "OPEN",
  "OPTION",
  "OR",
  "ORDER",
  "OUTER",
  "OUTPUT",
  "OVERLAPS",
  "PAD",
  "PARTIAL",
  "POSITION",
  "PRECISION",
  "PREPARE",
  "PRESERVE",
  "PRIMARY",
  "PRIOR",
  "PRIVILEGES",
  "PROCEDURE",
  "PUBLIC",
  "READ",
  "REAL",
  "REFERENCES",
  "RELATIVE",
  "RESTRICT",
  "REVOKE",
  "RIGHT",
  "ROLLBACK",
  "ROWS",
  "SCHEMA",
  "SCROLL",
  "SECOND",
  "SECTION",
  "SELECT",
  "SESSION",
  "SESSION_USER",
  "SET",
  "SIZE",
  "SMALLINT",
  "SOME",
  "SPACE",
  "SQL",
  "SQLCODE",
  "SQLERROR",
  "SQLSTATE",
  "SUBSTRING",
  "SUM",
  "SYSTEM_USER",
  "TABLE",
  "TARGET",
  "TEMPORARY",
  "THEN",
  "TIME",
  "TIMESTAMP",
  "TIMEZONE_HOUR",
  "TIMEZONE_MINUTE",
  "TO",
  "TRAILING",
  "TRANSACTION",
  "TRANSLATE",
  "TRANSLATION",
  "TRIM",
  "TRUE",
  "UNION",
  "UNIQUE",
  "UNKNOWN",
  "UPDATE",
  "UPPER",
  "USAGE",
  "USER",
  "USING",
  "VALUE",
  "VALUES",
  "VARCHAR",
  "VARYING",
  "VIEW",
  "WHEN",
  "WHENEVER",
  "WHERE",
  "WITH",
  "WORK",
  "WRITE",
  "YEAR",
  "ZONE"
  };
  static Vector reservedWords = new Vector(Arrays.asList(resWrds));

/*
  // known database accounts
  DBComboBoxModel dbmodel;
  JComboBox dbChooser; 
  DBConnectParams dbuser;

  JFormattedTextField dbTableField;

  Connection conn;
  Statement stmt;
  DatabaseMetaData dbmd;


  // displays query row count
  JLabel rowLabel = new JLabel("Rows     ");
  // connection status
  JTextField status;
  // query status
  JTextField queryStatus;
  // split panes
  JSplitPane spltc;
  JSplitPane splts;
  JSplitPane splt;
  JSplitPane spltq;
  JSplitPane spltr;
  TreeSet dbDataTypes = new TreeSet();
  // Catalogs
  DefaultListModel catalogModel;
  JList catalogList;
  JScrollPane gjsp;
  // Schemas
  DefaultListModel schemaModel;
  JList schemaList;
  JScrollPane sjsp;
  // Tables
  DefaultListModel tableModel;
  JList tableList;
  JScrollPane tjsp;
  // columns
  DefaultTableModel colModel;
  JTable colTable;
  JScrollPane cjsp;
  // SQL query
  JTextArea queryText;
  JScrollPane qjsp;
  // query results table
  JDBCTableModel rowModel;
  JTableView rowTable;
  JScrollPane rjsp;
  //
  JButton aboutDB;
  JButton submitBtn = new JButton("submit");
  JButton stopBtn = new JButton("stop");
  // Limit Rows Returned
  JComponent rowLimit = DoSpinner.getComponent(100, 0, 10000000, 1);
  JCheckBox limitRows = new JCheckBox("Limit Rows");
*/


  // Edit Connections  // Choose Connection
  // Connect   // Connect status
  // TableName:  // 
  // Column Selection
  // Row Selection 
  // dbColumnName  // Column Name in Database 
  // dbColumnType  // ColumnType in Database
  // dbColumnFormat // Character Input Format if needed
  // dbColumnSize   // Char length, number precision
  // nullable       // Can a value be null in database
  // tableColumnName  // 
  // tableData        // the data rows in the table
  // Progress bar

  JFormattedTextField dbTableField;

  // known database accounts
  DBComboBoxModel dbmodel;
  JComboBox dbChooser;
  DBConnectParams dbuser;
  TreeSet dbDataTypes = new TreeSet();
  // connection status
  JTextField status;
  // 
  TableModel tm;
  //
  ListSelectionModel lsm;

  JProgressBar progress;
  JTextField dbTableName;
  JTable jtable; 

  JButton aboutDB;

  Connection conn;
  Statement stmt;
  DatabaseMetaData dbmd;

  public DBSaveTable() {
    JLabel label;
    dbmodel = new DBComboBoxModel(); 
    JButton dbServices = new JButton("Edit Connections"); 
    dbServices.setToolTipText("Edit Database Account Preferences");
    dbServices.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          (new DBUserPanel(dbmodel)).show((Window)getTopLevelAncestor());
        }
      }
    );


    dbChooser = new JComboBox(dbmodel); 
    dbChooser.setToolTipText("Select a database account");
    JButton connBtn = new JButton("connect");
    connBtn.setToolTipText("Establish a connection to the selected database");
    connBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          connectToDatabase();
        }
      }
    );

    JPanel connectionPanel = new JPanel(new BorderLayout());

    JPanel connChoicePanel = new JPanel();
    JPanel connBtnPanel = new JPanel(new BorderLayout());
    connChoicePanel.setLayout(new BoxLayout(connChoicePanel, BoxLayout.X_AXIS));

    connChoicePanel.add(dbServices);
    connChoicePanel.add(dbChooser);
    connBtnPanel.add(connBtn,BorderLayout.WEST);
    status = new JTextField();
    status.setBackground(null);
    status.setToolTipText("Status of database connection");
    connBtnPanel.add(status);

    aboutDB = new JButton("About DB");
    aboutDB.setEnabled(false);
    aboutDB.setToolTipText("Select a database account");
    aboutDB.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          aboutDatabase();
        }
      }
    );

    connBtnPanel.add(aboutDB,BorderLayout.EAST);

    connectionPanel.add(connChoicePanel,BorderLayout.NORTH);
    connectionPanel.add(connBtnPanel,BorderLayout.SOUTH);


/*
    // Tables
    tableModel = new DefaultListModel();
    tableList = new JList(tableModel);
    tableList.setToolTipText("Select a database Table");
    tableList.setVisibleRowCount(8);
    tjsp = new JScrollPane(tableList);

    tableList.addListSelectionListener(
      new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting() && tableList.getMinSelectionIndex() >= 0) {
            setColumns(tableList.getSelectedValues());
            setRows( tableList.getSelectedValues(), "*");
          }
        }
      }
    );
*/

    
    dbTableField = new JFormattedTextField();
    JPanel pnl = new JPanel(new BorderLayout());
    label = new JLabel("Table Name: ");
    label.setToolTipText("Create Database Table as: ");
    pnl.add(label, BorderLayout.WEST);
    pnl.add(dbTableField);

    JPanel dbCreatePanel = new JPanel(new BorderLayout());
    dbCreatePanel.add(pnl, BorderLayout.NORTH);

    // columns 
    // column show dbcolname dbtype size 

    DefaultTableModel colModel = new DefaultTableModel();
    JTable colTable = new JTable(colModel);

    dbCreatePanel.add(new JScrollPane(colTable));

    JTextArea createText = new JTextArea( );

    // View of table
    // link to previous row selection 
    // JTable
    

    setLayout(new BorderLayout());
    //status = new JTextField();
    add(connectionPanel,BorderLayout.NORTH);
    add(dbCreatePanel,BorderLayout.CENTER);


  }

/*
  {
    // columns
    colModel = new DefaultTableModel(1,1);
    colTable = new JTable(colModel);
    cjsp = new JScrollPane(colTable);

    // rows
    rowTable = new JTableView();
    //rowTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    //rjsp = new JScrollPane(rowTable);

    // query

    limitRows.addItemListener( 
      new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          rowLimit.setEnabled(limitRows.isSelected());
        }
      }
    );
    rowLimit.setEnabled(limitRows.isSelected());

    submitBtn.setToolTipText("Start the query to view the table");
    submitBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          submitQuery();
          stopBtn.setEnabled(true);
        }
      }
    );

    stopBtn.setEnabled(false);
    stopBtn.setToolTipText("Stop the current query");
    stopBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (rowModel != null) {
            rowModel.stopQuery();
          }
          if (!limitRows.isSelected()) {
            limitRows.doClick();
          }
          if (rowModel != null) {
            DoSpinner.setValue(rowLimit,new Integer(rowModel.getRowCount()));
          }
          ((JComponent)e.getSource()).setEnabled(false);
          try {
            // since this can change the preferredsize of the JTable...
            rowTable.validate();
          } catch (Exception ex) {
          }
        }
      }
    );

    queryStatus = new JTextField();
    queryStatus.setBackground(null);
    queryStatus.setToolTipText("Status of database query");

    queryText = new JTextArea(5,80);
    qjsp = new JScrollPane(queryText);


    JPanel gPnl = new JPanel();
    gPnl.setLayout(new BoxLayout(gPnl, BoxLayout.Y_AXIS));
    label = new JLabel("Catalogs");
    label.setToolTipText("Select a database Catalog");
    gPnl.add(label);
    gPnl.add(gjsp);

    JPanel sPnl = new JPanel();
    sPnl.setLayout(new BoxLayout(sPnl, BoxLayout.Y_AXIS));
    label = new JLabel("Schemas");
    label.setToolTipText("Select a database Schema");
    sPnl.add(label);
    sPnl.add(sjsp);

    JPanel tPnl = new JPanel();
    tPnl.setLayout(new BoxLayout(tPnl, BoxLayout.Y_AXIS));
    label = new JLabel("Tables");
    label.setToolTipText("Select a database Table");
    tPnl.add(label);
    tPnl.add(tjsp);

    JPanel cPnl = new JPanel();
    cPnl.setLayout(new BoxLayout(cPnl, BoxLayout.Y_AXIS));
    label = new JLabel("Columns");
    label.setToolTipText("Select which database Columns to view");
    cPnl.add(label);
    cPnl.add(cjsp);

    JPanel qPnl = new JPanel(new BorderLayout());
    JPanel sbPnl = new JPanel(new BorderLayout()); 
    
    Box sbBox = new Box(BoxLayout.X_AXIS); 
    sbBox.add(limitRows);
    sbBox.add(rowLimit);
    sbBox.add(submitBtn);
    sbBox.add(stopBtn);
    sbPnl.add(sbBox,BorderLayout.WEST);
    sbPnl.add(queryStatus);

    label = new JLabel("Query");
    label.setToolTipText("You may edit this query");
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
    JButton qClearBtn = new JButton("Clear");
    qClearBtn.setToolTipText("Clear the query window");
    qClearBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          queryText.setText("");
        }
      }
    );


    JPanel qlPnl = new JPanel(new BorderLayout());
    qlPnl.add(label,BorderLayout.WEST);
    Box qhBox = new Box(BoxLayout.X_AXIS);
    qhBox.add(qClearBtn);
    qhBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    qlPnl.add(qhBox,BorderLayout.EAST);

    qPnl.add(qlPnl,BorderLayout.NORTH);
    qPnl.add(sbPnl,BorderLayout.SOUTH);
    qPnl.add(qjsp);

    JPanel rPnl = new JPanel();
    rPnl.setLayout(new BoxLayout(rPnl, BoxLayout.Y_AXIS));
    rPnl.add(rowLabel);
    //rPnl.add(rjsp);
    rPnl.add(rowTable);

    spltc = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,tPnl,cPnl);
    spltc.setOneTouchExpandable(true);

    splts = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,sPnl,spltc);
    splts.setOneTouchExpandable(true);

    splt = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,gPnl,splts);
    splt.setOneTouchExpandable(true);

    spltq = new JSplitPane(JSplitPane.VERTICAL_SPLIT,splt,qPnl);
    spltq.setOneTouchExpandable(true);

    spltr = new JSplitPane(JSplitPane.VERTICAL_SPLIT,spltq,rPnl);
    spltr.setOneTouchExpandable(true);

    setLayout(new BorderLayout());
    //status = new JTextField();
    add(connectionPanel,BorderLayout.NORTH);
    add(spltr,BorderLayout.CENTER);
    //add(status,BorderLayout.SOUTH);
    spltr.setDividerLocation(.7);
    spltq.setDividerLocation(.7);
    spltr.setResizeWeight(.5);
    spltq.setResizeWeight(.5);
  }
*/


  private Connection getConnection() {
    try {
      try {
        if (conn != null || !conn.isClosed()) {
          return conn;
        }
      } catch (Exception ex1) {
        conn = null;
        ExceptionHandler.popupException(""+ex1);
      }
      dbuser = (DBConnectParams)dbChooser.getSelectedItem();
      String usr = dbuser.getUser();
      String pw =  dbuser.getPassword();
      String url = dbuser.getURL();
      String driver = dbuser.getDriverName();
      Class.forName(driver);
      conn = DriverManager.getConnection(url,usr,pw);
      int idx = url.indexOf('@');
      status.setText("connected to " + url.substring(idx>0?idx+1:0));
      dbmd = conn.getMetaData();
      // System.err.println("dbmd = " + dbmd);
      ResultSet rs = dbmd.getTypeInfo();
      if (rs != null) {
        dbDataTypes.clear();
        while(rs.next()) {
          DBTypeInfo dbti = new DBTypeInfo(rs);
          dbDataTypes.add(dbti);
        }
      }
    } catch (Exception ex) {
          status.setText("DB connection failed " + ex);
          ExceptionHandler.popupException(""+ex);
    }
    return conn;
  }

  private DatabaseMetaData getDBMetaData() {
    if (dbmd == null) {
      getConnection();
    }
    return dbmd;
  }


  public void connectToDatabase() {
    aboutDB.setEnabled(false);
    dbuser = (DBConnectParams)dbChooser.getSelectedItem();
    String usr = dbuser.getUser();
    String pw =  dbuser.getPassword();
    String url = dbuser.getURL();
    String driver = dbuser.getDriverName();
    try {
      DBTestConnection.testConnection(usr, pw, url, driver);
      dbuser = new DBUser(usr, pw, url, driver);
      if (conn != null) {
        if (!conn.isClosed()) {
          conn.close();
        }
        conn = null;
      }
      dbmd = null;
      status.setText("connected to " + url);
      dbmd = getDBMetaData();
      aboutDB.setEnabled(true);
    } catch (Exception ex) {
      status.setText("DB connection failed " + ex);
      ExceptionHandler.popupException(""+ex);
    }
  }


  public DefaultListModel getTables(DatabaseMetaData dbmd, String catalog, String schema, DefaultListModel dlm)
                         throws SQLException {
    DefaultListModel lm = dlm != null ? dlm : new DefaultListModel();
    String tbltypes[] = {"TABLE", "VIEW"};
    ResultSet rs = dbmd.getTables(catalog,schema,null,tbltypes);
    if (rs != null) {
      lm.clear();
      while(rs.next()) {
        String s = rs.getString(3);
        lm.addElement(s);
      }
    }
    return lm;
  }

  public boolean tableNameIsValid(String tableName) {
    return true;
  }

  public static boolean tableExists(DatabaseMetaData dbmd, String tableName) {
    try {
      ResultSet rs = dbmd.getTables(null,null,tableName,null);
      while (rs.next()) {
        String tn = rs.getString(3);
        System.err.println(" tableExists " + tn + "\t" + tableName);
        if (tn != null && tn.equals(tableName))
          return true;
      }
    } catch (SQLException sqlex) {
      for (SQLException ex = sqlex; ex != null; ex = ex.getNextException()) {
        ExceptionHandler.popupException(""+ex);
      }
    }
    return false;
  }


  public void saveTable(String destination, String tableName, TableModel tm,
                        ListSelectionModel selectedRows, ListSelectionModel selectedColumns,
                        TableContext context) throws SQLException {
  }

  public String getTableCreateString(String tableName, java.util.List columnNames, java.util.List columnTypes, java.util.List columnNullable) {
    String sql = "CREATE TABLE " + tableName + " ( " ;
    for (int i = 0; i < columnNames.size(); i++) {
      sql += i > 0 ? ", " : "";
      sql += columnNames.get(i) + " " + columnTypes.get(i) + " " + columnNullable.get(i);
    }
    sql += " )";
    return sql;
  }

  public void upload() {
    // Get DBConnection
    Connection conn = null;
    DatabaseMetaData dbmd = null;
    DefaultListModel tableList = null;
    int maxTableNameLen = 30;
    int maxColumnNameLen = 30;

    String tableName = null;
    Vector columnNames = null;
    Vector columnTypes = null;
    Vector columnNullable = null;

    Vector types = new Vector();
    
    try {
      conn = getConnection();
      // Get dbmd
      dbmd = conn.getMetaData();
      maxTableNameLen = dbmd.getMaxTableNameLength();
      maxColumnNameLen = dbmd.getMaxColumnNameLength();
    } catch (SQLException sqlex) {
      for (SQLException ex = sqlex; ex != null; ex = ex.getNextException()) {
        ExceptionHandler.popupException(""+ex);
      }
    }

    try {
      // Get table list
      tableList = getTables(dbmd, null, null, tableList);

    } catch (SQLException sqlex) {
      for (SQLException ex = sqlex; ex != null; ex = ex.getNextException()) {
        ExceptionHandler.popupException(""+ex);
      }
    }

    tableName = "";
    // Check if tablename exists
      // cancel
      // replace
      // append
    // Column names
    // Determine DB Columnn types
    // create table statement
    String createSql = getTableCreateString(tableName, columnNames, columnTypes, columnNullable);
    // prepareStatement
    // load columns


       

        int sqltype[] = new int[types.size()];
        String insrt = "INSERT INTO " + tableName + " VALUES(";
        for (int i = 0; i < types.size(); i++) {
          sqltype[i] =  ((Integer)types.get(i)).intValue();
          if (i > 0) insrt += ", ";
          insrt += "?";
        }
        insrt += ")";
        System.err.println(insrt);
        PreparedStatement pstmt = null;
        try {
          pstmt = conn.prepareStatement(insrt);
        } catch (SQLException sqlex) {
            ExceptionHandler.popupException(""+sqlex);
        }

        int nrow = 0;
        int ncol = 0;
    
        for(int row = 0; row < nrow; row++) {
          try {
            pstmt.clearParameters();
            for (int col = 0; col < ncol; col++) {
              Object field = tm.getValueAt(row, col);
              setField(pstmt, field, 1 + col, sqltype[col]);
            }
            pstmt.execute();
          } catch (SQLException sqlex) {
            for (SQLException ex = sqlex; ex != null; ex = ex.getNextException()) {
              ExceptionHandler.popupException(""+ex);
            }
          }
        }

  }


  public static void setField(PreparedStatement pstmt, Object field, int c, int sqltype) {
   System.err.print("setField ");  
   System.err.print("\t" + c);  
   System.err.print("\t" + field);  
   System.err.println("\t" + sqltype);  
   String sfld = null;
   try {
    if (field == null) {
      pstmt.setNull(c+1,sqltype);
      return;
    }
    try {
    sfld = field.toString();
    } catch (Exception ex1) {
      ExceptionHandler.popupException(""+ex1);
    }
    switch (sqltype) {
    case java.sql.Types.CHAR :
    case java.sql.Types.VARCHAR :
      //if (sfld.length() < 1) System.err.println("\t " + (c+1) + "\t sfld < 1" );
      pstmt.setString(c+1,sfld);
      break;
    case java.sql.Types.LONGVARCHAR :
      if (sfld.length() >= 4000) {
        pstmt.setCharacterStream(c+1,new StringReader(sfld),sfld.length());
        break;
      }
      pstmt.setString(c+1,sfld);
      break;
    case java.sql.Types.INTEGER :
      pstmt.setInt(c+1,field instanceof Number ? 
                   ((Number)field).intValue() : Integer.parseInt(sfld));
      break;
    case java.sql.Types.TINYINT :
      pstmt.setByte(c+1,field instanceof Number ? 
                    ((Number)field).byteValue() : Byte.parseByte(sfld));
      break;
    case java.sql.Types.SMALLINT :
      pstmt.setShort(c+1,field instanceof Number ? 
                     ((Number)field).shortValue() : Short.parseShort(sfld));
      break;
    case java.sql.Types.BIGINT :
      pstmt.setLong(c+1,field instanceof Number ? 
                    ((Number)field).longValue() : Long.parseLong(sfld));
      break;
  
    case java.sql.Types.FLOAT :
      pstmt.setFloat(c+1,field instanceof Number ? 
                     ((Number)field).floatValue() : Float.parseFloat(sfld));
      break;
    case java.sql.Types.DOUBLE :
      pstmt.setDouble(c+1,field instanceof Number ? 
                      ((Number)field).doubleValue() : Double.parseDouble(sfld));
      break;
    case java.sql.Types.DECIMAL :
    case java.sql.Types.NUMERIC :
    case java.sql.Types.REAL :
      if (field instanceof Number) {
        try {
          pstmt.setBigDecimal(c+1,new BigDecimal(((Number)field).doubleValue()));
          break;
        } catch (NumberFormatException  numex) {
           ExceptionHandler.popupException(""+ numex);  
        } catch (SQLException sqlex) {
           ExceptionHandler.popupException(""+ sqlex);
        } catch (Exception ex) {
           ExceptionHandler.popupException(""+ ex);  
        }
      }
      try {
        BigDecimal bd = new BigDecimal(sfld);
        pstmt.setBigDecimal(c+1,bd);
      } catch (StringIndexOutOfBoundsException ioob) {
          if (sfld.length() < 1) 
          pstmt.setNull(c+1,sqltype);
      } catch (SQLException numex) {
        if (numex.toString().indexOf("Underflow") >= 0) {
          pstmt.setDouble(c+1,0.); 
          break;
        }
        System.err.println("numex " + sfld);
        for (SQLException ex = numex; ex != null; ex = ex.getNextException()) {
          System.err.println("\t" + ex);
        }
        try {
          double dv = Double.parseDouble(sfld);
          pstmt.setBigDecimal(c+1,new BigDecimal(dv));
        } catch (SQLException nex) {
          System.err.println("nex " + sfld + "\t"+ nex);
          System.err.println("nex " + nex);
          if (sfld.length() < 1) 
            pstmt.setNull(c+1,sqltype);
        } catch (Exception ex) {
          if (sfld.length() < 1) 
            pstmt.setNull(c+1,sqltype);
        }
      }
      break;
    case java.sql.Types.DATE :
    case java.sql.Types.TIME :
    case java.sql.Types.TIMESTAMP :
  
    case java.sql.Types.ARRAY :
    case java.sql.Types.BINARY :
    case java.sql.Types.VARBINARY :
    case java.sql.Types.LONGVARBINARY :
    case java.sql.Types.BIT :
      break;
    case java.sql.Types.BLOB :
      // pstmt.setBinaryStream(c+1,new StringReader(sfld),sfld.length());
      break;
    case java.sql.Types.CLOB :
      pstmt.setCharacterStream(c+1,new StringReader(sfld),sfld.length());
      break;
  
    case java.sql.Types.DISTINCT :
    case java.sql.Types.NULL :
    case java.sql.Types.REF :
    case java.sql.Types.STRUCT :
    case java.sql.Types.JAVA_OBJECT :
    case java.sql.Types.OTHER :
    default:
       System.err.println(" NEED TO DEFINE " + sqltype + " for column " + (c+1));
    }
   } catch (Exception ex) {
    ExceptionHandler.popupException(""+ex);
    System.err.println("setField " + (c+1) + " : " + ex);
    System.err.println("\t" + sfld);
    System.err.println("\t" + field);
    System.err.println("\t" + field.getClass());
   }
    //System.err.println("setField : done " );
  }

/*
  public void loadFile(String schema, String fileName, File file, String fs, String rs, PrintWriter out) {
   try {
    Connection conn = getConnection();
    Statement stmt = conn.createStatement(); 
    BufferedReader rdr = null;
    int fsl = fs.length();
    System.err.println("reading " + fileName);
    rdr = new BufferedReader(new FileReader(file));
    ColStats cs[] = getColStats(rdr, fs, rs, out);
    // get number of cols
    int nc = cs.length;
    // get number of rows
    int nr = 0;
    for (int i = 0; i < cs.length; i++) {
      if (cs[i].valCnt > nr)
         nr = cs[i].valCnt;
    }
    if (true) {
    // headers
          out.print("<table>");
          out.print("<tr> <B>");
          out.print("<TH> name </TH>");
          out.print("<TH> maxLen </TH>");
          out.print("<TH> valCnt </TH>");
          out.print("<TH> nullCnt </TH>");
          out.print("<TH> numCnt </TH>");
          out.print("<TH> dateCnt </TH>");
          out.print("<TH> minNum </TH>");
          out.print("<TH> maxNum </TH>");
          out.print("<TH> minDate </TH>");
          out.print("<TH> maxDate </TH>");
          out.println();
          out.print("</B> </tr>");
          for (int i = 0; i < cs.length; i++) {
            out.print("<tr>");
            out.print("<td> " + cs[i].name + " </td>");
            out.print("<td> " + cs[i].maxLen + " </td>");
            out.print("<td> " + cs[i].valCnt + " </td>");
            out.print("<td> " + cs[i].nullCnt + " </td>");
            out.print("<td> " + cs[i].numCnt + " </td>");
            out.print("<td> " + cs[i].dateCnt + " </td>");
            out.print("<td> " + cs[i].minNum + " </td>");
            out.print("<td> " + cs[i].maxNum + " </td>");
            out.print("<td> " + cs[i].minDate + " </td>");
            out.print("<td> " + cs[i].maxDate + " </td>");
            out.println("<br>");
          out.print("</tr>");
          }
          out.print("</table>");

    }
    out.println(" " + fileName + "  cols: " + nc + "  rows: " + nr);
    // decide java class types;
    Class columnClasses[] = new Class[nc];
    try {
      for (int i = 0; i < cs.length; i++) {
        if (cs[i].numCnt > 0 && cs[i].valCnt - cs[i].nullCnt - cs[i].numCnt <= 1) {
          columnClasses[i] = Class.forName("java.lang.Number");
        } else 
        if (cs[i].dateCnt > 0 && cs[i].valCnt - cs[i].nullCnt - cs[i].dateCnt <= 1) {
          columnClasses[i] = Class.forName("java.util.Date");
        } else {
          columnClasses[i] = Class.forName("java.lang.String");
        }
      }
    } catch (ClassNotFoundException cnfex) {
      out.println("" + cnfex); 
    }
    Vector columnNames = new Vector();
    String line = null;
    try {
      rdr = new BufferedReader(new FileReader(file));
      line = rdr.readLine();
      if (line == null) {
        out.println("Can't read column names"); 
        return;
      } else {
        int len = line.length();
        int c = 0;
        for (int i = 0; i < len; c++) {
          String field = "COLUMN_" + c;
          int fi  = line.indexOf(fs,i);
          if (fi >= i) {
            field = line.substring(i,fi);
            i = fi + fsl;
          } else if (rs != null) {
            int ri = line.indexOf(rs,i);
            if (ri >= i) {
              field = line.substring(i,ri);
              i = ri + rs.length();
            } else {
              field = line.substring(i);
              i = len;
            }
          } else {
            field = line.substring(i);
            i = len;
          }
          columnNames.addElement(field);
          out.println(c + ":\t" + field); 
        }
      }
    } catch (IOException ioex) {
    }
    
    String s = new String(fileName);
    int b = s.lastIndexOf('/');
    if (b > 0) 
       s = s.substring(b+1);
    int e = s.indexOf('.');
    if (e > 0) 
       s = s.substring(0,e);
    String tblname = s.toUpperCase();
  
  
    out.println(tblname + " found: " + tableExists(conn,tblname, out)); 
    //print(tblname + " matches: " + tableMatches(tblname, tm)); 
  
    if (tableExists(conn, tblname, out)) {
      drop(stmt, tblname, out);
    }
  
    String rowidx = "UPLOAD_ROW";
    Vector nv = new Vector(); // column names
    //nv.addElement(rowidx); // reserve this column name
    Vector tv = new Vector(); // column types
    BitSet isquote = new BitSet(nc);
    BitSet isdate = new BitSet(nc);
    BitSet xCol = new BitSet(nc);
    Vector tbls = new Vector();
    Vector bflds = new Vector();
    tbls.addElement(bflds);
    out.println(tblname + " exists: " + tableExists(conn,tblname, out)); 
    if (!tableExists(conn, tblname, out)) {
      for (int j = 0; j < nc; j++) {
        int cw = cs[j].maxLen;
        String tmcn = "" + columnNames.elementAt(j); 
        String cn = "";
        StringTokenizer st = new StringTokenizer(tmcn.toUpperCase()); 
        if (st.hasMoreTokens())
          cn += st.nextToken();
        while (st.hasMoreTokens()) {
          cn += "_" + st.nextToken();
        }
        if (reservedWords.contains(cn)) {
          cn += "_COL";
        }
        
        for(int i = 0; nv.contains(cn); i++) {
          cn = tmcn + "_" + 1; 
        }
        nv.addElement(cn);
        String ct = getSqlTypeFor(columnClasses[j],cw);
  out.println("col " + j + "\t" + columnClasses[j] + "\t" + ct + "\t" + cw);
        tv.addElement(ct);
        if (ct.equals("LONG")) {
          xCol.set(j);
          Vector v = new Vector(1);
          v.addElement(new Integer(j));
          tbls.addElement(v);
        } else {
          bflds.addElement(new Integer(j));
        }
      }

      for (int ti = 0; ti < tbls.size(); ti++) {
        Vector tcv = (Vector)tbls.elementAt(ti);
        String ctn = tblname + ti; 
        if (tableExists(conn, ctn, out)) {
          drop(stmt, ctn, out);
        }
        String ctbl = "CREATE TABLE " + ctn + " ( " + rowidx + " INTEGER NOT NULL ";
        for (int j = 0; j < tcv.size(); j++) {
          int i = ((Integer)tcv.elementAt(j)).intValue();
          ctbl += ", ";
          ctbl += nv.elementAt(i) + " " + tv.elementAt(i);
        }
        ctbl += " )";
        out.println(ctbl);
  out.println("STMT: " + stmt);
        try {
          int rlt = stmt.executeUpdate(ctbl);
          out.println( " stmt.executeUpdate " + rlt);
          String grant = "GRANT SELECT ON " + ctn + " TO PUBLIC";
          rlt = stmt.executeUpdate(grant);
          out.println( " stmt.executeUpdate " + rlt);
        } catch (SQLException sqlex) {
          for (SQLException ex = sqlex; ex != null; ex = ex.getNextException()) {
            out.println(ex);
          }
        }
      }
    }
  
    for (int col = 0; col < nc; col++) {
      Class jc = columnClasses[col];
      try {
        if ( Class.forName("java.lang.Double").isAssignableFrom(jc)
          || Class.forName("java.lang.Integer").isAssignableFrom(jc) 
          || Class.forName("java.lang.Number").isAssignableFrom(jc) 
          ) {
          isquote.clear(col);
        } else {
          if ( Class.forName("java.util.Date").isAssignableFrom(jc)) {
            isdate.set(col);
          }
          isquote.set(col);
        }
      } catch (ClassNotFoundException cnfex) {
        out.println("" + cnfex); 
      }
    }
  
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    for (int ti = 0; ti < tbls.size(); ti++) {
      rdr = new BufferedReader(new FileReader(file));
      // Assume first line is columnNames
      for (int hi = 0; hi < 1; hi++) {
          try {
            line = rdr.readLine();
          } catch (IOException ioex) {
            out.println("At 0 : " + ioex);
          }
      }
      Vector tcv = (Vector)tbls.elementAt(ti);
      String ctn = tblname + ti; 
      String istart = "INSERT INTO " + tblname + " VALUES (";
      Vector types = new Vector();
      try {
        DatabaseMetaData dbmd = conn.getMetaData();
  out.println("dbmd: " + dbmd);
  out.println("schema: " + schema);
  out.println("table: " + ctn);
        out.println(ctn);
        ResultSet crs = dbmd.getColumns(null, schema, ctn, null);
    out.println("rs: " + crs);
        while (crs.next()) {
          types.add(new Integer(crs.getInt(5)));
          out.println(crs.getObject(3)+"\t"+crs.getObject(4)+"\t"+crs.getObject(5));
        }
        int sqltype[] = new int[types.size()];
        String insrt = "INSERT INTO " + ctn + " VALUES(";
        for (int i = 0; i < types.size(); i++) {
          sqltype[i] =  ((Integer)types.get(i)).intValue();
          if (i > 0) insrt += ", ";
          insrt += "?";
        }
        insrt += ")";
        out.println(insrt);
        PreparedStatement pstmt = conn.prepareStatement(insrt);
    
        for(int row = 0; row < nr; row++) {
          pstmt.clearParameters();
          setField(pstmt, new Integer(row), 0, sqltype[0], out);
          try {
            line = rdr.readLine();
          } catch (IOException ioex) {
            out.println("At " + row + ": " + ioex);
          }
          if (line == null) {
            break;
          }
          int len = line.length();
          int ci = 0;
          Vector v = new Vector();
          for (int i = 0; i < len; ci++) {
            String field = null;
            int fi  = line.indexOf(fs,i);
            if (fi >= i) {
              field = line.substring(i,fi);
              i = fi + fsl;
            } else if (rs != null) {
              int ri = line.indexOf(rs,i);
              if (ri >= i) {
                field = line.substring(i,ri);
                i = ri + rs.length();
              } else {
                field = line.substring(i);
                i = len;
              }
            } else {
              field = line.substring(i);
              i = len;
            }
            v.addElement(field);
          }
          for (int j = 0; j < tcv.size(); j++) {
            int c = ((Integer)tcv.elementAt(j)).intValue();
            Object field = c < v.size() ? v.elementAt(c) : null;
            setField(pstmt, field, 1 + j, sqltype[1 + j], out);
          }
          try {
            pstmt.execute();
          } catch (SQLException sqlex) {
            for (SQLException ex = sqlex; ex != null; ex = ex.getNextException()) {
              out.println(ex);
            }
            out.println("Error at row " + row);
            out.println(line);
          }
          //out.print("\r" + row);
        }
        try {
          stmt = conn.createStatement();
          String grant = "GRANT SELECT ON " + ctn + " TO PUBLIC";
          stmt.executeUpdate(grant);
          out.println(grant);
        } catch (SQLException sqlerr) {
          out.println(sqlerr);
        }
      } catch (SQLException sqlex) {
        for (SQLException ex = sqlex; ex != null; ex = ex.getNextException()) {
          out.println(ex);
        }
      }
      // create indices
  
    }
    // create view
    if (true) {
      try {
        stmt = conn.createStatement();
        String view = "CREATE VIEW " + tblname + " as select ";
        if (tbls.size() == 1) {
          String ctn = tblname + "0";
          for (int i = 0; i < nv.size(); i++) {
            if (i > 0) view += ", ";
            view += nv.elementAt(i);
          }
          view += " from " + ctn;
        } else if (tbls.size() > 1) {
          for (int i = 0; i < nv.size(); i++) {
            if (i > 0) view += ", ";
            view += nv.elementAt(i);
          }
          view += " from ";
          for (int j = 0; j < tbls.size(); j++) {
            if (j > 0) view += ", ";
            view += tblname + j;
          }
          view += " where ";
          for (int j = 1; j < tbls.size(); j++) {
            if (j > 1) view += " and ";
            view += tblname + 0 + "." + rowidx + " = " + 
                    tblname + j + "." + rowidx;
          }
        }
        out.println(view);
        stmt.executeUpdate(view);
        out.println(view);
        String grant = "GRANT SELECT ON " + tblname + "0"  + " TO PUBLIC";
        stmt.executeUpdate(grant);
        out.println(grant);
      } catch (SQLException sqlex) {
        for (SQLException ex = sqlex; ex != null; ex = ex.getNextException()) {
          out.println(ex);
        }
      }
  
    }
   } catch (Exception allex) {
     out.println("loadFIle : " + allex);
     allex.printStackTrace(out);
   }
  }
*/
  

    static Hashtable sqlTypeName = new Hashtable();
    static {
      sqlTypeName.put(new Integer(Types.BIT),"BIT");
      sqlTypeName.put(new Integer(Types.TINYINT),"TINYINT");
      sqlTypeName.put(new Integer(Types.SMALLINT),"SMALLINT");
      sqlTypeName.put(new Integer(Types.INTEGER),"INTEGER");
      sqlTypeName.put(new Integer(Types.BIGINT),"BIGINT");
      sqlTypeName.put(new Integer(Types.FLOAT),"FLOAT");
      sqlTypeName.put(new Integer(Types.REAL),"REAL");
      sqlTypeName.put(new Integer(Types.DOUBLE),"DOUBLE");
      sqlTypeName.put(new Integer(Types.NUMERIC),"NUMERIC");
      sqlTypeName.put(new Integer(Types.DECIMAL),"DECIMAL");
      sqlTypeName.put(new Integer(Types.CHAR),"CHAR");
      sqlTypeName.put(new Integer(Types.VARCHAR),"VARCHAR");
      sqlTypeName.put(new Integer(Types.LONGVARCHAR),"LONGVARCHAR");
      sqlTypeName.put(new Integer(Types.DATE),"DATE");
      sqlTypeName.put(new Integer(Types.TIME),"TIME");
      sqlTypeName.put(new Integer(Types.TIMESTAMP),"TIMESTAMP");
      sqlTypeName.put(new Integer(Types.BINARY),"BINARY");
      sqlTypeName.put(new Integer(Types.VARBINARY),"VARBINARY");
      sqlTypeName.put(new Integer(Types.LONGVARBINARY),"LONGVARBINARY");
      sqlTypeName.put(new Integer(Types.NULL),"NULL");
      sqlTypeName.put(new Integer(Types.OTHER),"OTHER");
      sqlTypeName.put(new Integer(Types.JAVA_OBJECT),"JAVA_OBJECT");
      sqlTypeName.put(new Integer(Types.DISTINCT),"DISTINCT");
      sqlTypeName.put(new Integer(Types.STRUCT),"STRUCT");
      sqlTypeName.put(new Integer(Types.ARRAY),"ARRAY");
      sqlTypeName.put(new Integer(Types.BLOB),"BLOB");
      sqlTypeName.put(new Integer(Types.CLOB),"CLOB");
      sqlTypeName.put(new Integer(Types.REF),"REF");
      if (System.getProperty("java.specification.version").compareTo("1.4")>=0) {
        sqlTypeName.put(new Integer(Types.BOOLEAN),"BOOLEAN");
        sqlTypeName.put(new Integer(Types.DATALINK),"DATALINK");
      }
    }

  public static String getSqlTypeFor(Class jc) {
    try {
      if (java.lang.Double.class.isAssignableFrom(jc)) {
        return "NUMBER";
      } else if (java.lang.Integer.class.isAssignableFrom(jc)) {
        return "INTEGER";
      } else if (java.lang.Number.class.isAssignableFrom(jc)) {
        return "NUMBER";
      } else if (java.util.Date.class.isAssignableFrom(jc)) {
        return "DATE";
      } else {
        return "VARCHAR(32)";
      }
    } catch (Exception ex) {
      ExceptionHandler.popupException(""+ ex);
    }
    return null;
  }

  private void setQueryStatus(String msg) {
    // queryStatus.setText(msg);
  }

  public void aboutDatabase() {
    if (dbmd != null) {
      DBInfoPanel dbip = new DBInfoPanel(dbmd);
      JFrame jf = new JFrame("About DataBase " + (dbuser != null ? dbuser.getName() : ""));
      JButton closeBtn = new JButton("Close");
      closeBtn.setToolTipText("Close this window");
      closeBtn.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              ((Window)((JComponent)e.getSource()).getTopLevelAncestor()).dispose();
            } catch (Exception ex) {
                ExceptionHandler.popupException(""+ ex);
            }
          }
        }
      );
      JToolBar tb = new JToolBar();
      tb.add(closeBtn);
      jf.getContentPane().add(tb,BorderLayout.NORTH);
      jf.getContentPane().add(dbip);
      jf.pack();
      jf.setVisible(true);
    }
  }

  private void parseArgs(String args[]) {
    String dbname = null;
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        if (args[i].equals("-preferences")) {
          String source = args[++i];
          try {
            DBUserList.getSharedInstance().importDBUsers(source);
          } catch (Exception ex) {
            ExceptionHandler.popupException(""+ ex);
          }
        } else if (args[i].equals("-dbname")) {
          dbname = args[++i];
        }
      }
    }
    if (dbname != null) {
      setDatabase(dbname);
    }
  }

  public void setDatabase(String dbname) {
    if (dbname != null) {
      for (int i = 0; i < dbmodel.getSize(); i++) {
        DBConnectParams dbc = (DBConnectParams)dbmodel.getElementAt(i);
        if (dbc != null && dbc.getName() != null && dbc.getName().equals(dbname)) {
          dbChooser.setSelectedIndex(i);
          connectToDatabase();
          break;
        }
      }
    }
  }

  public static void main(String args[]) {
    DBSaveTable dbp = new DBSaveTable();
    dbp.parseArgs(args);
    JFrame frame = new JFrame("Browse Database Tables");
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {System.exit(0);}
      public void windowClosed(WindowEvent e) {System.exit(0);}
    });
    frame.getContentPane().add(dbp);
    JButton closeBtn = new JButton("Close");
    closeBtn.setToolTipText("Close this window");
    closeBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            ((Window)((JComponent)e.getSource()).getTopLevelAncestor()).dispose();
          } catch (Exception ex) {
              ExceptionHandler.popupException(""+ ex);
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

/*
  class JDBCSQLTypeListRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(
         JList list,
         Object value,
         int index,
         boolean isSelected,
         boolean cellHasFocus) {
      try {
        String s = sqlTypeName.get(value).toString();
        super.setText(s);
      } catch (Exception ex) {
        System.err.println(this + " " + ex);
      }
      return this;
    }
  }

  class JDBCSQLTypeRenderer extends DefaultTableCellRenderer {
    public void setValue(Object value) {
      super.setValue(sqlTypeName.get(value));
    }
  }
*/

