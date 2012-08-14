/*
 * @(#) $RCSfile: ScriptPanel.java,v $ $Revision: 1.1 $ $Date: 2004/05/19 20:21:50 $ $Name: TableView1_3 $
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * 
 * @author       J Johnson
 * @version $Revision: 1.1 $ $Date: 2004/05/19 20:21:50 $  $Name: TableView1_3 $ 
 * @since        1.0
 * @see  javax.swing.table.TableModel 
 * @see  javax.swing.ListSelectionModel
 * @see  Cells
 */
public class ScriptPanel extends JPanel {
  PipedInputStream jsIn;
  PrintStream jsOut;
  PrintStream jsErr;
  PrintStream jsTo;
  BufferedReader outRdr;
  PipedOutputStream pipeOut;
  PipedInputStream pipeIn;
  JTextArea outputArea;
  JTextField inputArea;
  JScrollPane jspOut;
  ScriptInterpreter interpreter;
  Map vars = null;

  Thread outThread = new Thread() {
    public void run() {
      displayOutput();   
    }
  };

  public void displayOutput() {
    try {
      for(String line = outRdr.readLine(); line != null; line = outRdr.readLine()) {
        displayOutput(line);
      }
    } catch (Exception ex) {
            ExceptionHandler.popupException(""+ex);
    }
  }

  public void displayOutput(String line) {
    outputArea.append(line);
    outputArea.append("\n");
    JScrollBar sb = jspOut.getVerticalScrollBar(); 
    if (sb != null) {
      sb.setValue(sb.getMaximum());
    }
  }

  private void sendInput() {
    sendInput(inputArea.getText());
  }

  public void sendInput(String input) {
    outputArea.append(input);
    outputArea.append("\n");
    JScrollBar sb = jspOut.getVerticalScrollBar(); 
    if (sb != null) {
      sb.setValue(sb.getMaximum());
    }
    jsTo.println(input);
    jsTo.flush();
    inputArea.setText("");
  }

  public ScriptPanel(ScriptInterpreter scripter) throws IOException {
    this(scripter, null);
  }

  public ScriptPanel(ScriptInterpreter scripter, Map vars) throws IOException {
    interpreter = scripter;
    this.vars = vars;
    PipedOutputStream pipeToIn = new PipedOutputStream();
    jsIn = new PipedInputStream(pipeToIn); 
    jsTo = new PrintStream(pipeToIn); 
    pipeOut = new PipedOutputStream();
    PipedInputStream pipeFrom = new PipedInputStream(pipeOut);
    outRdr = new BufferedReader(new InputStreamReader(pipeFrom));
    jsOut = new PrintStream(pipeOut);
    // jsOut = System.out;
    jsErr = jsOut;
    // jsErr = System.err;

    interpreter.initialize(jsIn, jsOut, jsErr, vars);
    new Thread(interpreter).start();
    outputArea = new JTextArea(30,80);
    outputArea.setBackground(Color.lightGray);
    outputArea.setEditable(false);
    inputArea = new JTextField(80);
    inputArea.addActionListener(
      new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
              sendInput();
            } catch (Exception ex) {
                            ExceptionHandler.popupException(""+ex);
            }
        }
      });
    jspOut = new JScrollPane(outputArea);
    setLayout(new BorderLayout());
    add(jspOut);
    add(inputArea,BorderLayout.SOUTH);
    outThread.start();
  }

  public static void main( String[] args) {
    try {
      Map vars = new Hashtable();
      vars.put("me",vars);
      ScriptPanel jsPnl = new ScriptPanel(new ScriptJS(), vars);
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
