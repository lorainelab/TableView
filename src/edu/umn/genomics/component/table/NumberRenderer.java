package edu.umn.genomics.component.table;


import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.NumberFormat;
import java.text.DecimalFormat;


/**
 * Renders a Number
 */
public class NumberRenderer extends DefaultTableCellRenderer {
  NumberFormat nf = NumberFormat.getIntegerInstance();
  String pattern = "0.###E0";
  DecimalFormat df = new DecimalFormat(pattern);
  public NumberRenderer() {
    super();
    setHorizontalAlignment(RIGHT);
  }
  /**
   * Sets the <code>String</code> object for the cell being rendered to
   * <code>value</code>.
   *
   * @param value  the string value for this cell; if value is
   *          <code>null</code> it sets the text value to an empty string
   * @see JLabel#setText
   *
   */
  protected void setValue(Object value) {
      Number n = value instanceof Number ? (Number)value : null;
      setText((value == null)
               ? ""
               : value instanceof Integer || value instanceof Short ||
                 value instanceof Character || value instanceof Byte
                 ? nf.format(value)
                 : value instanceof Number
                   ? df.format(value)
                   : value.toString());
  }
}
