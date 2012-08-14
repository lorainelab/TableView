package edu.umn.genomics.component.table;


import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * Renders a Number
 */
public class ClassNameRenderer extends DefaultTableCellRenderer {
  /**
   * Sets the <code>String</code> object for the cell being rendered to
   * <code>value</code>.
   *
   * @param value  the string value for this cell; if value is
   *          <code>null</code> it sets the text value to an empty string
   * @see JLabel#setText
   */
  protected void setValue(Object value) {
    setText(value == null ? "" : value instanceof Class ? ((Class)value).getSimpleName() : value.toString());
  }
}
