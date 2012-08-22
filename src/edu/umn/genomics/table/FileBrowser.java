/*
 * @(#) $RCSfile: FileBrowser.java,v $ $Revision: 1.7 $ $Date: 2003/08/04 18:57:00 $ $Name: TableView1_3 $
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * A Graphical User Interface for selecting a table from a file or URL, 
 * and providing a TableModel interface to the datasource.
 * @author       J Johnson
 * @version $Revision: 1.7 $ $Date: 2003/08/04 18:57:00 $  $Name: TableView1_3 $ 
 * @since        1.0
 * @see  javax.swing.table.TableModel 
 * @see  javax.swing.ListSelectionModel
 */
public class FileBrowser extends AbstractTableSource implements OpenTableSource {

  TableModel tm = null;
  JFileChooser fc;
  JTextField path;
  JTable jtable = new JTable();

  /**
   * Creates a FileBrowser Component for selecting a table from a file or URL.
   */
  public FileBrowser() {
    // file
    String fs = "\t";
    int colHeadersRows = -1;

    fc = new JFileChooser();

    JPanel flp = new JPanel(new GridLayout(0,1));
    JPanel fip = new JPanel(new GridLayout(0,1));
    
    
    JLabel pthLbl = new JLabel("File or URL:",JLabel.RIGHT); 
    path = new JTextField(40);
    path.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
              openTableSource();
            } catch (Exception ex) {
                            ExceptionHandler.popupException(""+ex);
            }
        }
      });
    JButton browseBtn = new JButton("browse");
    browseBtn.setToolTipText("Open a file chooser");
    browseBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          int returnVal = fc.showOpenDialog(path);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
              path.setText(file.getAbsolutePath());
              openTableSource();
            } catch (Exception ex) {
                                ExceptionHandler.popupException(""+ex);
            }
          } else {
                System.err.println("Open command cancelled by user.");
          }
        }
      });
    JPanel fsp = new JPanel(new BorderLayout());
    JPanel fsbp = new JPanel(new FlowLayout(1));
    final JTextField fsFld = new JTextField(3);
    fsFld.setToolTipText("fields separated by your tpyed in characters");
        ActionListener al = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            String command = ae.getActionCommand();
            if(command.equals("default"))
                FileTableModel.setFS(null);
            else if(command.equals("tab"))
                FileTableModel.setFS("	");
            else if(command.equals("comma"))
                FileTableModel.setFS(",");
            else if(command.equals("pipe"))
                FileTableModel.setFS("|");
            else
                FileTableModel.setFS(fsFld.getText());
        }
    };
    JRadioButton fsDefaultBtn = new JRadioButton("TableView's best guess");
    fsDefaultBtn.setSelected(true);
    fsDefaultBtn.setActionCommand("default");
    fsDefaultBtn.setToolTipText("TableView guesses the delimiter based on the input source");
    fsDefaultBtn.addActionListener(al);
    JRadioButton fsTabBtn = new JRadioButton("TAB");
    fsTabBtn.setToolTipText("fields separated by a single TAB charater");
    fsTabBtn.setActionCommand("tab");
    fsTabBtn.addActionListener(al);
    JRadioButton fsCommaBtn = new JRadioButton(",");
    fsCommaBtn.setToolTipText("fields separated by a single comma charater");
    fsCommaBtn.setActionCommand("comma");
    fsCommaBtn.addActionListener(al);
    JRadioButton fsPipeBtn = new JRadioButton("|");
    fsPipeBtn.setToolTipText("fields separated by a single pipe charater");
    fsPipeBtn.setActionCommand("pipe");
    fsPipeBtn.addActionListener(al);
    final JRadioButton fsOtherBtn = new JRadioButton("other:");
    fsOtherBtn.setActionCommand("other");
    fsOtherBtn.addActionListener(al);
    fsOtherBtn.setToolTipText("fields separated by your typed in characters");
    // Group the radio buttons.
    ButtonGroup fsGrp = new ButtonGroup();
    fsGrp.add(fsDefaultBtn);
    fsGrp.add(fsTabBtn);
    fsGrp.add(fsCommaBtn);
    fsGrp.add(fsPipeBtn);
    fsGrp.add(fsOtherBtn);
    // Put fs into panel
    fsbp.add(fsDefaultBtn);
    fsbp.add(fsTabBtn);
    fsbp.add(fsCommaBtn);
    fsbp.add(fsPipeBtn);
    fsbp.add(fsOtherBtn);
    fsp.add(fsbp,BorderLayout.WEST);
    fsp.add(fsFld,BorderLayout.CENTER);
    JButton openBtn = new JButton("open");
    openBtn.setToolTipText("Open a file chooser");
    openBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if(fsOtherBtn.isSelected())
                FileTableModel.setFS(fsFld.getText());
          openTableSource();
        }
      });
   
    JPanel pathp = new JPanel(new BorderLayout());
    pathp.add(path,BorderLayout.CENTER);
    pathp.add(browseBtn,BorderLayout.WEST);
    pathp.add(openBtn,BorderLayout.EAST);
     //Choose Field separator
    JLabel fsLbl = new JLabel("Field Separator:",JLabel.RIGHT); 
    

    flp.add(pthLbl);
    fip.add(pathp);
    flp.add(fsLbl);
    fip.add(fsp);

    JPanel fileLoc = new JPanel(new BorderLayout());
    fileLoc.add(flp,BorderLayout.WEST);
    fileLoc.add(fip,BorderLayout.CENTER);

    setLayout(new BorderLayout());
    add(fileLoc,BorderLayout.NORTH);
    jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    JScrollPane jsp = new JScrollPane(jtable);
    add(jsp);
  }

  private void openTableSource() {
    try {
      openTableSource(path.getText());
    } catch (Exception ex) {
    }
  }

  /**
   * Open the data source and create a TableModel.
   * @param tableSource The URL or file path for the table data.
   */
  public void openTableSource(String tableSource) throws IOException {
    FileTableModel ftm = new FileTableModel(tableSource);
    jtable.setModel(ftm);
    setTableSource(ftm, tableSource);
  }
}
