/*
 * @(#) $RCSfile: JSFormula.java,v $ $Revision: 1.11 $ $Date: 2004/08/02 20:23:42 $ $Name: TableView1_3 $
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

import javax.swing.table.TableModel;
import org.mozilla.javascript.*;

/**
 * JSFormula provides JavaScript formulas from tables.
 * JSFormula uses embedded JavaScript from Mozilla rhino package:
 *   http://www.mozilla.org/rhino/doc.html
 * 
 * A formula is compiled as a JavaScript function.  
 * Three variables are provided to the formula:
 * <UL>
 *  <LI><b> table </b> - the TableModel on which this formula operates </LI>
 *  <LI><b> row   </b> - the row of the cell in the table (row starts from 0) </LI>
 *  <LI><b> col   </b> - the column of the cell in the table (col starts from 0) </LI>
 * </UL>
 * The formula needs to return a value.  
 * Example formulas:
 * <UL>
 * <LI><PRE><CODE>
 *    // return column 1 - column 0
 *    return table.getValueAt(row, 1) - table.getValueAt(row, 0)
 * </CODE></PRE></LI>
 * <LI><PRE><CODE>
 *    // return column 1 - column 0 with max number of decimal places
 *    var dp = 10000; // set number of decimal places
 *    return Number(Math.round(
 *       dp*(table.getValueAt(row,1)-table.getValueAt(row,0)))/dp);
 * </CODE></PRE></LI>
 * <LI><PRE><CODE>
 *    // return the sin of column 0
 *    return Math.sin(table.getValueAt(row, 0))
 * </CODE></PRE></LI>
 * <LI><PRE><CODE>
 *    // return the sum of the preceeding columns
 *    var sum = 0.;
 *    for (var c = 0; c < col; c++) {
 *      var v = table.getValueAt(row,c);
 *      if (v instanceof Number) {
 *        sum += v.doubleValue();
 *      }
 *    }
 *    return sum;
 * </CODE></PRE></LI>
 * </UL>
 * 
 * @author       J Johnson
 * @version $Revision: 1.11 $ $Date: 2004/08/02 20:23:42 $  $Name: TableView1_3 $ 
 * @since        1.0
 * @see  javax.swing.table.TableModel 
 * @see  javax.swing.ListSelectionModel
 * @see  Cells
 */
public class JSFormula extends AbstractColumnFormula{
  static Scriptable sharedScope = null;
  static int refCnt = 0;
  Scriptable myScope = null;
  Function func = null; 

  private Scriptable getSharedScope() {
    if (sharedScope == null) {
      Context cx = Context.enter();
      try {
        cx.setCompileFunctionsWithDynamicScope(true);
        sharedScope = cx.initStandardObjects(null);
        /* This is deprecated in rhino:
        ImporterTopLevel itl = new ImporterTopLevel(cx);
        NativeJavaPackage[] njp = new NativeJavaPackage[1];
        njp[0] = new NativeJavaPackage("edu.umn.genomics.table");
        itl.importPackage(cx, sharedScope, njp,  null);
        */
      } catch (Exception ex1) {
                ExceptionHandler.popupException(""+ex1);
      } finally {
        cx.exit();
      }
    }
    return sharedScope;
  }
  public JSFormula() {
    this("",null,"");
  }
  public JSFormula(TableModel tableModel) {
    this("",tableModel,"");
  }
  public JSFormula(String name, TableModel tableModel) {
    this(name,tableModel,"");
  }
  public JSFormula(String name, TableModel tableModel, String formula) {
    super(name,tableModel,formula);
  }

  public Object calculateValueAt(int rowIndex, int columnIndex) {
    Object result = null;
    Context cx = Context.enter();
    cx.setOptimizationLevel(9);
    Object[] args = new Object[2];
    args[0] = new Integer(rowIndex);
    args[1] = new Integer(columnIndex);
    try {
      if (myScope == null || func == null) {
        myScope = cx.newObject(getSharedScope());
        myScope.setPrototype(getSharedScope());
        myScope.setParentScope(null);
        myScope.put("table", myScope, tm);
        myScope.put("Cells", myScope, new Cells());
        String source = "function calcCell(row,col) { " + script + "}";
        // This no longer works with current rhino release:
        // func = cx.compileFunction(myScope, source, null, 0, null);
        // So now do this instead:
        cx.evaluateString(myScope,source,"function definition",1,null);
        Object f = myScope.get("calcCell",myScope);
        func = (Function)f;
      }
      result = func.call(cx, func.getParentScope(), func, args);
      if (result instanceof NativeJavaObject) {
        result = ((NativeJavaObject)result).unwrap(); 
      }
    } catch (WrappedException we) {
      result = we;
            ExceptionHandler.popupException(""+we);
    } catch (EvaluatorException eve) {
      result = eve;
            ExceptionHandler.popupException(""+eve);
    } catch (JavaScriptException jse) {
      result = jse;
            ExceptionHandler.popupException(""+jse);
    } catch (EcmaError ee) {
      result = ee;
            ExceptionHandler.popupException(""+ee);
    } catch (Exception ex) {
      result = ex;
            ExceptionHandler.popupException(""+ex);
      ex.printStackTrace();
    } finally {
      Context.exit();
    }
    return result;
  }

  protected static String interpreterInfo = "" 
    + " A formula is compiled as a JavaScript function. \n"
    + " For more information see:  http://www.mozilla.org/rhino/doc.html \n"
    + "\n";

  protected static String exampleInfo = ""
    + "\n"
    + " Example formulas: \n"
    + "\n"
    + "//Example 1\n"
    + "   // return column 1 - column 0 \n"
    + "   return Number(table.getValueAt(row, 1) - table.getValueAt(row, 0)) \n"
    + "\n"
    + "//Example 2\n"
    + "   // return column 1 - column 0 with max number of decimal places \n"
    + "   var dp = 10000; // set number of decimal places \n"
    + "   return Number(Math.round( \n"
    + "       dp*(table.getValueAt(row,1)-table.getValueAt(row,0)))/dp); \n"
    + "  Example 3\n"
    + "\n"
    + "   // return the sin of column 0 \n"
    + "   return Math.sin(table.getValueAt(row, 0)) \n"
    + "\n"
    + "//Example 4\n"
    + "   // average the values in a range of rows \n"
    + "   return Cells.average(Cells.getValuesFrom(table,row-1,col-1,row+1,col-1)); \n"
    + "\n"
    + "//Example 5\n"
    + "  // return the sum of the preceeding columns \n"
    + "  var sum = 0.; \n"
    + "  for (var c = 0; c < col; c++) { \n"
    + "    var v = table.getValueAt(row,c); \n"
    + "    if (v instanceof Number) { \n"
    + "      sum += v.doubleValue(); \n"
    + "    } \n"
    + "  } \n"
    + "  return sum; \n"
    + "\n";
  public static String getHelpText() {
    return interpreterInfo + variableInfo + exampleInfo;
  }

  /**
   * Get a type name for this cell.
   * @return The type name for this cell.
   */
  public String getType() {
    return "JavaScript";
  }

}
