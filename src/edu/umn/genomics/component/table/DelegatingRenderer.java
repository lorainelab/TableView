package edu.umn.genomics.component.table;

import java.util.Map;
import java.util.HashMap;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * 
 */
public class DelegatingRenderer extends DefaultTableCellRenderer {
  Map<Class,TableCellRenderer> map = new HashMap<Class,TableCellRenderer>();
  public DelegatingRenderer () {
    super();
  }
  public DelegatingRenderer (Map<Class,TableCellRenderer> map) {
    super();
    setMap(map);
  }
  public void setMap(Map<Class,TableCellRenderer> map) {
    this.map = map;
  }
  public Map<Class,TableCellRenderer> getMap() {
    return map;
  }
  public void add(Class cellClass, TableCellRenderer renderer) {
    map.put(cellClass,renderer);
  }
  public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus, int row, int column) {
    if (value != null) {
      for (Class cellClass : map.keySet()) {
        if (cellClass.isInstance(value)) {
          return map.get(cellClass).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
      }
    }
    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}
