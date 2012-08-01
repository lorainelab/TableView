/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umn.genomics.table;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author auser
 */
public class ExceptionHandler {
    static Icon tvIcon = new ImageIcon("TableView.png");
    public static int popupException(String message){
        return JOptionPane.showConfirmDialog(null, message, "Tableview Exception", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, tvIcon);
    }
}
