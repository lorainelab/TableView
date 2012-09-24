package edu.umn.genomics.component.table;

import java.util.Map;
import java.util.HashMap;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;


/**
 * 
 */
public class DelegatingEditor extends DefaultCellEditor {
  Map<Class,TableCellEditor> map = new HashMap<Class,TableCellEditor>();
  public DelegatingEditor() {
    super(new JTextField());
  }
  public DelegatingEditor(Map<Class,TableCellEditor> map) {
    this();
    setMap(map);
  }
  public void setMap(Map<Class,TableCellEditor> map) {
    this.map = map;
  }
  public Map<Class,TableCellEditor> getMap() {
    return map;
  }
  public void add(Class cellClass, TableCellEditor editor) {
    map.put(cellClass,editor);
  }
  public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
    if (value != null) {
      for (Class cellClass : map.keySet()) {
        if (cellClass.isInstance(value)) {
          return map.get(cellClass).getTableCellEditorComponent(table, value, isSelected, row, column);
        }
      }
    }
    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
  }
}
