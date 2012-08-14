/*
 * @(#) $RCSfile: BshPanel.java,v $ $Revision: 1.1 $ $Date: 2004/05/19 20:21:48 $ $Name: TableView1_3 $
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;

/**
 * 
 * @author       J Johnson
 * @version $Revision: 1.1 $ $Date: 2004/05/19 20:21:48 $  $Name: TableView1_3 $ 
 * @since        1.0
 * @see  javax.swing.table.TableModel 
 * @see  javax.swing.ListSelectionModel
 * @see  Cells
 */
public class BshPanel extends ScriptPanel {

  public BshPanel() throws IOException {
    this(null);
  }
  
  public BshPanel(Map vars) throws IOException {
    super(new ScriptBsh(), vars);
  }

  public static void main( String[] args) {
    try {
      Map vars = new Hashtable();
      vars.put("me",vars);
      BshPanel jsPnl = new BshPanel(vars);
      JFrame frame = new JFrame("JavaScript");
      frame.addWindowListener(new WindowAdapter() {
        private void doClose(WindowEvent e) {
          System.exit(0);
        }
        public void windowClosing(WindowEvent e) {
          doClose(e);
        }
        public void windowClosed(WindowEvent e) {
          doClose(e);
        }
      });
      frame.getContentPane().add(jsPnl,BorderLayout.CENTER);
      frame.pack();
      frame.setVisible(true);
    } catch (Exception ex) {
            ExceptionHandler.popupException(""+ex);
    }
  }
}
