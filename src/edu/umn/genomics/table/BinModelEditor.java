/*
 * @(#) $RCSfile: BinModelEditor.java,v $ $Revision: 1.2 $ $Date: 2004/08/12 21:00:02 $ $Name: TableView1_3 $
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.PatternSyntaxException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * BinModel categorizes an indexed list of items into a number of bins.
 * This can be used to generate histograms on the list.
 * @author       J Johnson
 * @version      %I%, %G%
 * @since        1.0
 */
public class BinModelEditor extends JPanel {
  /*
  Range 
  Distinct Values



  
  Date - range:     Year, Quarter, Season, Month, Week, Day, Hour, Minute, Second,
         partition:
         DateFormatSymbols
         Calendar.setTime(Date date)
                  roll(Calendar.DATE, true)
         partition: DayOfWeek, DayOfYear, DayOfMonth,
                    MonthOfYear, SeasonOfYear, QuarterOfYear
                    HourOfDay
         DAY_OF_WEEK
         DAY_OF_MONTH
         DAY_OF_YEAR
         WEEK_OF_MONTH
         WEEK_OF_YEAR
         MONTH_OF_YEAR
         QUARTER_OF_YEAR
         getMinimum(int field)
         getMaximum(int field)
  Number -
           range:  count, start incr, start incr cnt,   func StdDev, log, manual
           partition:
  String - range: Distinct, Starting Letter,
         - partition: regex, ...
  Object

  Bins
  Category Name Category Color
  */

  ColumnMapBinModel model;
  JTableEditor regexTable;
  SpinnerNumberModel numStartModel;
  SpinnerNumberModel numIncrModel;
  JSpinner numStart;
  JSpinner numIncr;
  SpinnerNumberModel numDevModel;
  JSpinner numDev;
  // Bin Count
  // Date Range
  SpinnerDateModel startDateModel; 
  JSpinner dateStart;
  JComboBox sdmIncr;
  JComboBox dateIncr;
  // Date Partition
  JComboBox dateField;
  static String[] dateFields = {
        "Day of the Week", // Calendar.DAY_OF_WEEK
        "Day of the Month", // Calendar.DAY_OF_MONTH
        "Day of the Year", // Calendar.DAY_OF_YEAR
        "Week of the Month", // Calendar.WEEK_OF_MONTH
        "Week of the Year", // Calendar.WEEK_OF_YEAR
        "Month of the Year" // Calendar.MONTH
  };
  static Hashtable dateFieldHt = new Hashtable();

  static {
    dateFieldHt.put(dateFields[0], new Integer(Calendar.DAY_OF_WEEK));
    dateFieldHt.put(dateFields[1], new Integer(Calendar.DAY_OF_MONTH));
    dateFieldHt.put(dateFields[2], new Integer(Calendar.DAY_OF_YEAR));
    dateFieldHt.put(dateFields[3], new Integer(Calendar.WEEK_OF_MONTH));
    dateFieldHt.put(dateFields[4], new Integer(Calendar.WEEK_OF_YEAR));
    dateFieldHt.put(dateFields[5], new Integer(Calendar.MONTH));
  }
/*
  class CalendarField {
    public String name;
    public int calendarField;
    public int increment;
    public CalendarField(String name, int calendarField, int increment) {
      this.name = name;
      this.calendarField = calendarField;
      this.increment = increment;
    }
    public String toString() {
      return name;
    }
  }
  static CalendarField[] calendarField = {
    new CalendarField("Year",Calendar.YEAR,1),
    new CalendarField("Month",Calendar.MONTH,1),
  };
*/
  static String[] calFields = {
		// Millenium
		// Century
		// Decade
    "Year", // Calendar.YEAR
		// Year Quarter
    "Month", // Calendar.MONTH
    "Week", // Calendar.WEEK_OF_YEAR
    "Day", // Calendar.DAY_OF_MONTH
    "AM PM", // Calendar.AM_PM
    "Hour", // Calendar.HOUR
    "Minute", // Calendar.MINUTE
    "Second", // Calendar.SECOND
    "Millisecond", // Calendar.MILLISECOND 
  };
  static Hashtable calFieldHt = new Hashtable();

  static {
    calFieldHt.put(calFields[0], new Integer(Calendar.YEAR));
    calFieldHt.put(calFields[1], new Integer(Calendar.MONTH));
    calFieldHt.put(calFields[2], new Integer(Calendar.WEEK_OF_YEAR));
    calFieldHt.put(calFields[3], new Integer(Calendar.DAY_OF_MONTH));
    calFieldHt.put(calFields[4], new Integer(Calendar.AM_PM));
    calFieldHt.put(calFields[5], new Integer(Calendar.HOUR));
    calFieldHt.put(calFields[6], new Integer(Calendar.MINUTE));
    calFieldHt.put(calFields[7], new Integer(Calendar.SECOND));
    calFieldHt.put(calFields[8], new Integer(Calendar.MILLISECOND));
  }
  JTabbedPane tabs = new JTabbedPane();

  public BinModelEditor(ColumnMapBinModel binModel) {
    model = binModel;
    ColumnMap cmap = model.getColumnMap();
    String info = cmap.getName() + " " + cmap.getCount() +  " values (" + cmap.getDistinctCount() + " distinct) from " 
                  + cmap.getMappedValue(cmap.getMin(),0) + " to " + cmap.getMappedValue(cmap.getMax(),0); 
    JTextField colInfo = new JTextField(info);
    colInfo.setEditable(false);
    setLayout(new BorderLayout());
    add(colInfo,BorderLayout.NORTH);
    // min max num numdistinct

    // Number of Bins
    JLabel nBinLabel = new JLabel("Set the Number of Bins");
    final SpinnerNumberModel nBinModel = new SpinnerNumberModel(
                                       new Integer(Math.min(10,cmap.getDistinctCount())), 
                                       new Integer(1),
                                       cmap.isContinuous() ? null : new Integer(cmap.getDistinctCount()),
                                       new Integer(1));
    JSpinner nBinField = new JSpinner(nBinModel);
    ChangeListener nBinListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          model.setBins(nBinModel.getNumber().intValue());
          model.setBinLabeler(null);
        }
      };
    nBinField.addChangeListener(nBinListener);
    JPanel nBinPanel = new JPanel(new BorderLayout());
    nBinPanel.add(nBinLabel,BorderLayout.WEST);
    nBinPanel.add(nBinField);
    tabs.addTab("Divide Range",null,nBinPanel,"Partition the range into equally sized division");

    // Distinct Values
    if (true) {
      JButton distinctApply = new JButton("Apply");
      distinctApply.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              ColumnMap cmap = model.getColumnMap();
              setDistinctPartition(model, cmap);
            }
          }
        );
    JPanel distinctPanel = new JPanel();
    distinctPanel.add(distinctApply);
    tabs.addTab("Distinct Values",null,distinctPanel,"Partition by Distinct Values");
    }

    // Date
    if (cmap.isDate()) {
      Object minObj = cmap.getMappedValue(cmap.getMin(),0);
      Object maxObj = cmap.getMappedValue(cmap.getMax(),0);
      Date minDate = minObj instanceof Date ? (Date)minObj : new Date();
      Date maxDate = maxObj instanceof Date ? (Date)maxObj : new Date();

      Calendar minCal = Calendar.getInstance();
      minCal.setTime(minDate);
      Calendar maxCal = Calendar.getInstance();
      maxCal.setTime(maxDate);
      long timeDiff = maxDate.getTime() - minDate.getTime();
      long yearDiff = (long)((maxDate.getTime() - minDate.getTime())/(1000 * 60 * 60 * 24 * 365.25));
      Calendar startCal = Calendar.getInstance();
      startCal.setTime(minDate);

      int sdmIncrIndex = calFields.length - 1;
      if (timeDiff > 1000) { // time is in milliseconds
        startCal.add(Calendar.MILLISECOND, -(startCal.get(Calendar.MILLISECOND) - startCal.getActualMinimum(Calendar.MILLISECOND)));
        sdmIncrIndex--;
      }
      if (timeDiff > 1000 * 60) {
        startCal.add(Calendar.SECOND, -(startCal.get(Calendar.SECOND) - startCal.getActualMinimum(Calendar.SECOND)));
        sdmIncrIndex--;
      }
      if (timeDiff > 1000 * 60 * 60) {
        startCal.add(Calendar.MINUTE, -(startCal.get(Calendar.MINUTE) - startCal.getActualMinimum(Calendar.MINUTE)));
        sdmIncrIndex--;
      }
      if (timeDiff > 1000 * 60 * 60 * 24) {
        startCal.add(Calendar.HOUR_OF_DAY, -(startCal.get(Calendar.HOUR_OF_DAY) - startCal.getActualMinimum(Calendar.HOUR_OF_DAY)));
        sdmIncrIndex--;
      }
      if (timeDiff > 1000 * 60 * 60 * 24 * 30) {
        startCal.add(Calendar.DAY_OF_MONTH, -(startCal.get(Calendar.DAY_OF_MONTH) - startCal.getActualMinimum(Calendar.DAY_OF_MONTH)));
        sdmIncrIndex--;
        sdmIncrIndex--;
      }
      if (timeDiff > 1000 * 60 * 60 * 24 * 30 * 12) {
        startCal.add(Calendar.MONTH, -(startCal.get(Calendar.MONTH) - startCal.getActualMinimum(Calendar.MONTH)));
        sdmIncrIndex--;
        sdmIncrIndex--;
      }

      // Range
      startDateModel = new SpinnerDateModel(); 
      startDateModel.setValue(startCal.getTime()); 
      dateStart = new JSpinner(startDateModel);
      sdmIncr = new JComboBox(calFields);
      sdmIncr.setSelectedIndex(sdmIncrIndex > 0 ? sdmIncrIndex : 0);
      sdmIncr.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JComboBox cb = (JComboBox)e.getSource();
              int calFld = ((Integer)calFieldHt.get(cb.getSelectedItem())).intValue();
              startDateModel.setCalendarField(calFld);
            }
          }
        );

      numIncrModel = new SpinnerNumberModel(1,1,100,1);
      numIncrModel.setMaximum(null);
      numIncr = new JSpinner(numIncrModel);
      
      JButton dateApply = new JButton("Apply");
      dateApply.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              ColumnMap cmap = model.getColumnMap();
              double cmin = cmap.getMin();
              double cmax = cmap.getMax();
              Date date = startDateModel.getDate();
              int calFld = ((Integer)calFieldHt.get(sdmIncr.getSelectedItem())).intValue();
              int incr = ((Number)numIncr.getValue()).intValue();
              setDateRange(model,cmap,date,calFld,incr);
            }
          }
        );

      // Start  Increment
      JPanel rangePanel = new JPanel(new BorderLayout());
      JPanel rangePanel2 = new JPanel(new BorderLayout());
      JPanel rangePanel3 = new JPanel(new GridLayout(1,0));
      rangePanel.add(dateStart);
      rangePanel3.add(sdmIncr);
      rangePanel3.add(numIncr);
      rangePanel2.add(rangePanel3,BorderLayout.WEST);
      rangePanel2.add(dateApply,BorderLayout.EAST);
      rangePanel.add(rangePanel2,BorderLayout.EAST);
      tabs.addTab("Set Ranges",null,rangePanel,"Partion the range by Time");

      // Partition
      dateField = new JComboBox(dateFields);
      dateField.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox)e.getSource();
            ColumnMap cmap = model.getColumnMap();
            int calField = ((Integer)dateFieldHt.get(cb.getSelectedItem())).intValue();
            setDatePartition(model, cmap, calField);
          }
        });
      JPanel partPanel = new JPanel(new BorderLayout());
      partPanel.add(dateField,BorderLayout.WEST);
      tabs.addTab("Set by Calendar",null,partPanel,"Partition by Calendar divisions");
    } else if (cmap.isNumber()) {
      double incr = 1.;
      double startVal = cmap.getMin();
      double diff = cmap.getMax() - cmap.getMin();
      if (diff > 0.) {
        int d = (int)Math.floor(Math.log(diff)/Math.log(10.));
        double scale = Math.pow(10.,d);
        if (scale != 0) {
          startVal = Math.floor(startVal / scale) * scale;
          incr = scale;
        }
      }
      // Range
      // Start
      JLabel numStartLabel = new JLabel("Start");
      numStartModel = new SpinnerNumberModel();
      numStartModel.setValue(new Double(startVal));
      numStart = new JSpinner(numStartModel);
      // Increment
      JLabel numIncrLabel = new JLabel("Size");
      numIncrModel = new SpinnerNumberModel();
      numIncrModel.setValue(new Double(incr));
      numIncrModel.setStepSize(new Double(incr/10.));
      numIncrModel.setMinimum(new Double(.000000001));
      numIncr = new JSpinner(numIncrModel);
      JButton numApply = new JButton("Apply");
      numApply.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              ColumnMap cmap = model.getColumnMap();
              double sval = ((Number)numStart.getValue()).doubleValue();
              double incr = ((Number)numIncr.getValue()).doubleValue();
              setNumberRange(model, cmap, sval, incr);
            }
          }
        );

      JPanel rangePanel = new JPanel(new BorderLayout());
      JPanel rangePanel2 = new JPanel(new GridLayout(1,0));
      rangePanel2.add(numStart,BorderLayout.WEST);
      rangePanel2.add(numIncr,BorderLayout.EAST);
      rangePanel.add(numApply,BorderLayout.EAST);
      rangePanel.add(rangePanel2);
      tabs.addTab("Set Ranges",null,rangePanel,"Partition the range by numerical increments");
      // StdDev
      numDevModel = new SpinnerNumberModel(1.,.1,1.,.1);
      numDev = new JSpinner(numDevModel);

      JButton devApply = new JButton("Apply");
      devApply.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              ColumnMap cmap = model.getColumnMap();
              double scale = ((Number)numDev.getValue()).doubleValue();
              setNumberStdDev(model, cmap, scale);
            }
          }
        );
      JPanel devPanel = new JPanel(new BorderLayout());
      devPanel.add(numDev);
      devPanel.add(devApply,BorderLayout.EAST);
      tabs.addTab("Standard Deviation",null,devPanel, "Partition by Standard Deviations from the Mean");
    } else {
      // Distinct Values
      // Range
      // By initial Character
      // Regex
      String[] columnNames = {"Bin Name", "Regular Expression"};
      regexTable = new JTableEditor(false,true);
      regexTable.newColumn(0,"Bin Name", java.lang.String.class);
      regexTable.newColumn(1,"Regular Expression", java.lang.String.class);
      regexTable.setPreferredViewableRows(5);
      JButton regexApply = new JButton("Apply");
      regexApply.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              try {
                Vector regex = new Vector();
                Vector labels = new Vector();
                ColumnMap cmap = model.getColumnMap();
                String[] regexArray = new String[regexTable.getTableModel().getRowCount()];
                for (int i = 0; i < regexArray.length; i++) {
                  regex.add((String)regexTable.getTableModel().getValueAt(i,1));
                  labels.add(regexTable.getTableModel().getValueAt(i,0));
                }
                setRegexPartition(model, cmap, regex, labels);
              } catch (PatternSyntaxException ex) {
                                ExceptionHandler.popupException(""+ex);
              } catch (Exception ex) {
                                ExceptionHandler.popupException(""+ex);
              }
            }
                    });
      JPanel regexPanel = new JPanel(new BorderLayout());
      regexPanel.add(new JScrollPane(regexTable));
            regexPanel.add(regexApply, BorderLayout.SOUTH);
            tabs.addTab("Categorize by Pattern", null, regexPanel, "Categorize by Regular Expression Patterns");
    }
    add(tabs);
  }

  public ColumnMapBinModel getBinModel() {
    return model;
  }

  public static double[] getCalendarDividers(ColumnMap cmap, Date startDate, int calField, int incr) {
    if (cmap == null) {
      return null;
    }
    double cmin = cmap.getMin();
    double cmax = cmap.getMax();
    double[] divs;
    int ndiv;
    Calendar cal;
    cal = Calendar.getInstance();
    cal.setTime(startDate);
    ndiv = 1;
    while ((double)cal.getTime().getTime() > cmin) {
      cal.add(calField,-incr);
    }
    Date date = cal.getTime();
    while ((double)cal.getTime().getTime() <= cmax) {
      ndiv++;
      cal.add(calField,incr);
    }
    divs = new double[ndiv];
    cal.setTime(date);
    for (int i = 0; i < divs.length; i++) {
      divs[i] = (double)cal.getTime().getTime(); 
      cal.add(calField,incr);
    }
    return divs;
  }

  public static void setDateRange(MutableBinModel model, ColumnMap cmap, Date startDate, int calField, int incr) {
    if (model == null || cmap == null || !cmap.isDate()) {
      return;
    }
    double cmin = cmap.getMin();
    double cmax = cmap.getMax();
    double[] divs;
    int ndiv;
    Calendar cal;
    switch(calField) {
    case Calendar.YEAR:
      model.setBins(getCalendarDividers(cmap,startDate,Calendar.YEAR,incr));
      break;
    case Calendar.MONTH:
      model.setBins(getCalendarDividers(cmap,startDate,Calendar.MONTH,incr));
      break;
    case Calendar.WEEK_OF_YEAR:
      model.setBins(startDate.getTime() * 1., 1000. * 60 * 60 * 24 * 7 * incr);
      break;
    case Calendar.DAY_OF_MONTH:
      model.setBins(startDate.getTime() * 1., 1000. * 60 * 60 * 24 * incr);
      break;
    case Calendar.AM_PM:
      model.setBins(startDate.getTime() * 1., 1000. * 60 * 60 * 12 * incr);
      break;
    case Calendar.HOUR:
      model.setBins(startDate.getTime() * 1., 1000. * 60 * 60 * incr);
      break;
    case Calendar.MINUTE:
      model.setBins(startDate.getTime() * 1., 1000. * 60 * incr);
      break;
    case Calendar.SECOND:
      model.setBins(startDate.getTime() * 1., 1000. * incr);
      break;
    case Calendar.MILLISECOND:
      model.setBins(startDate.getTime() * 1., 1. * incr);
      break;
    default:
    }
    Date date = (Date)cmap.getMappedValue(model.getBinMin(0),0);
    model.setBinLabeler(new DateLabeler(calField, incr, date));
  }

  public static void setDatePartition(MutableBinModel model, ColumnMap cmap, int calField) {
    setDatePartition(model,cmap,calField,null);
  }
  /**
   * Partition Dates into Calendar units such as Months of the Year or Days of the Week. 
   * @param model The BinModel to be altered.
   * @param cmap The ColumnMap that contains the Date values.
   * @param calField The Calendar field on which to partition the values.
   * @param labels 
   */
  public static void setDatePartition(MutableBinModel model, ColumnMap cmap, int calField, String[] labels) {
    if (model == null || cmap == null || !cmap.isDate()) {
      return;
    }
    PartitionIndexMap pim = DefaultColumnMap.getDatePartition(cmap,calField,labels != null ? labels.length : 0);
    model.setBins(pim);
    if (labels != null) {
      model.setBinLabeler(new ListBinLabeler(Arrays.asList(labels)));
    } else {
      model.setBinLabeler(new DatePartitionLabeler(calField));
    }
  }

  public static void setDistinctPartition(MutableBinModel model, ColumnMap cmap) {
    PartitionIndexMap pim = cmap.getPartitionIndexMap();
    model.setBins(pim);
    model.setBinLabeler(new PartitionBinLabeler(cmap, pim));
  }

  public static void setNumberRange(MutableBinModel model, ColumnMap cmap, double start, double incr) {
    if (model == null || cmap == null || !cmap.isNumber()) {
      return;
    }
    double sval = start;
    if (incr != 0.) {
      while (sval > cmap.getMin()) {
        sval -= incr > 0 ? incr : -incr;
      }
      model.setBins(sval,incr);
      model.setBinLabeler(null);
    }
  }

  public static void setNumberStdDev(MutableBinModel model, ColumnMap cmap, double scale) {
    double avg = cmap.getAvg();
    double stdDev = cmap.getStdDev();
    double incr = stdDev * scale;
    double sval = avg;
    double eval = avg;
    int nbin = 0;
     while (sval > cmap.getMin() || eval < cmap.getMax()) {
        sval -= incr;
        eval += incr;
        nbin += 2;
     }
     model.setBins(sval,incr,nbin);
     model.setBinLabeler(null);
  }


  public static void setRegexPartition(MutableBinModel model, ColumnMap cmap, List regex, List labels) 
          throws PatternSyntaxException {
      String[] regexArray = new String[regex.size()];
      for (int i = 0; i < regexArray.length; i++) {
        regexArray[i] = (String)regex.get(i);
      }
      labels.add("Others");
      PartitionIndexMap pim = DefaultColumnMap.getRegexPartition(cmap,regexArray);
      model.setBins(pim);
      model.setBinLabeler(new ListBinLabeler(labels));
  }


  public static Date getStartDate(Date minDate, Date maxDate) {
    Calendar minCal = Calendar.getInstance();
    minCal.setTime(minDate);
    Calendar maxCal = Calendar.getInstance();
    maxCal.setTime(maxDate);
    long timeDiff = maxDate.getTime() - minDate.getTime();
    long yearDiff = (long)((maxDate.getTime() - minDate.getTime())/(1000 * 60 * 60 * 24 * 365.25));
    Calendar startCal = Calendar.getInstance();
    startCal.setTime(minDate);

    int sdmIncrIndex = calFields.length - 1;
    if (timeDiff > 1000) { // time is in milliseconds
      startCal.add(Calendar.MILLISECOND, -(startCal.get(Calendar.MILLISECOND) - startCal.getActualMinimum(Calendar.MILLISECOND)));
      sdmIncrIndex--;
    }
    if (timeDiff > 1000 * 60) {
      startCal.add(Calendar.SECOND, -(startCal.get(Calendar.SECOND) - startCal.getActualMinimum(Calendar.SECOND)));
      sdmIncrIndex--;
    }
    if (timeDiff > 1000 * 60 * 60) {
      startCal.add(Calendar.MINUTE, -(startCal.get(Calendar.MINUTE) - startCal.getActualMinimum(Calendar.MINUTE)));
      sdmIncrIndex--;
    }
    if (timeDiff > 1000 * 60 * 60 * 24) {
      startCal.add(Calendar.HOUR_OF_DAY, -(startCal.get(Calendar.HOUR_OF_DAY) - startCal.getActualMinimum(Calendar.HOUR_OF_DAY)));
      sdmIncrIndex--;
    }
    if (timeDiff > 1000 * 60 * 60 * 24 * 30) {
      startCal.add(Calendar.DAY_OF_MONTH, -(startCal.get(Calendar.DAY_OF_MONTH) - startCal.getActualMinimum(Calendar.DAY_OF_MONTH)));
      sdmIncrIndex--;
      sdmIncrIndex--;
    }
    if (timeDiff > 1000 * 60 * 60 * 24 * 30 * 12) {
      startCal.add(Calendar.MONTH, -(startCal.get(Calendar.MONTH) - startCal.getActualMinimum(Calendar.MONTH)));
      sdmIncrIndex--;
      sdmIncrIndex--;
    }
    return startCal.getTime();
  }
}



