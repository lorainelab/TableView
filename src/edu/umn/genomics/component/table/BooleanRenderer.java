package edu.umn.genomics.component.table;



import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.JCheckBox;

public class BooleanRenderer extends JCheckBox implements TableCellRenderer {
  public BooleanRenderer() {
    super();
    setHorizontalAlignment(CENTER);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }
    setSelected((value != null && ((Boolean)value).booleanValue()));
    setEnabled(value != null);  
    return this;
  }
}

