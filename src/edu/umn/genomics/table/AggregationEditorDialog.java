/*
 * @(#) $RCSfile: BinModelEditorDialog.java,v $ $Revision: 1.1 $ $Date: 2004/08/02 20:23:38 $ $Name: TableView1_3 $
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
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * BinModel categorizes an indexed list of items into a number of bins.
 * This can be used to generate histograms on the list.
 * @author       J Johnson
 * @version      %I%, %G%
 * @since        1.0
 */
public class AggregationEditorDialog extends JDialog {
  Box editorPanel = new Box(BoxLayout.Y_AXIS);
//  HistogramModel hgm = null;
//  HistogramListener hl = new HistogramListener() {
//   /**
//    * The method called when a HistogramModel is changed.
//    * @param e the HistogramEvent.
//    */
//    public void histogramChanged(HistogramEvent e) {
//      if (e.binModelsChanged()) {
//        updateEditor();
//      }
//    }
//  };

  public AggregationEditorDialog(Frame owner, TableContext ctx, TableModel tm) {
    super(owner);
    DefaultListModel dlm = new DefaultListModel();
    for (int c = 0; c < tm.getColumnCount(); c++) {
        dlm.addElement(tm.getColumnName(c));
    }
    JList colList = new JList(dlm);
    
    getContentPane().add(new JScrollPane(editorPanel));
    JButton closeBtn = new JButton("Close");
    closeBtn.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          setVisible(false);
        }
      }
    );
    JPanel closePnl = new JPanel(new BorderLayout());
    closePnl.add(closeBtn,BorderLayout.WEST);
    getContentPane().add(closePnl,BorderLayout.NORTH);
    setLocationRelativeTo(owner);
//    updateEditor();
    pack();
  }

//  public void updateEditor() {
//    java.util.List binModelList = hgm.getBinModelList();
//    Component[] comp = editorPanel.getComponents();
//    for (int i = 0; i < comp.length; i++) {
//      BinModel bm = ((BinModelEditor)comp[i]).getBinModel();
//      if (binModelList.contains(bm)) {
//        binModelList.remove(bm);
//      } else {
//        editorPanel.remove(comp[i]);
//      }
//    }
//    for (int i = 0; i < binModelList.size(); i++) {
//      BinModel bm = (BinModel)binModelList.get(i);
//      if (bm instanceof ColumnMapBinModel) {
//        ColumnMapBinModel cbm = (ColumnMapBinModel)bm;
//        BinModelEditor bme = new BinModelEditor(cbm);
//        editorPanel.add(bme);
//      }
//    }
//    validate();
//    repaint();
//  }
}
