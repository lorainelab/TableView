/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umn.genomics.table;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author James E Johnson <jj@umn.edu>
 * @version 
 */
public class DateLabeler implements BinLabeler, Serializable {
    SimpleDateFormat formatter = new SimpleDateFormat();
    Date startDate;
    int calendarField;
    int incr = 1;
    public DateLabeler(int calendarField, int incr, Date startDate) {
      this.calendarField = calendarField;
      this.startDate = startDate;
      this.incr = incr;
      String dateFormat = "yyyy/MM/dd_HH:mm:ss";
      switch(calendarField) {
      case Calendar.YEAR:
        dateFormat = "yyyy";
        break;
      case Calendar.MONTH:
        dateFormat = "MMM yyyy";
        break;
      case Calendar.WEEK_OF_YEAR:
        dateFormat = "yyyy w";
        break;
      case Calendar.DAY_OF_MONTH:
        dateFormat = "yyyy/MM/dd";
        break;
      case Calendar.AM_PM:
        dateFormat = "yyyy/MM/dd a";
        break;
      case Calendar.HOUR:
        dateFormat = "yyyy/MM/dd_HH:00";
        break;
      case Calendar.MINUTE:
        dateFormat = "HH:mm";
        break;
      case Calendar.SECOND:
        dateFormat = "HH:mm:ss";
        break;
      case Calendar.MILLISECOND:
        dateFormat = "HH:mm:ss.S";
        break;
      }
      formatter = new SimpleDateFormat(dateFormat);
    }
    /**
     * Return a label for the given value along an axis.
     * @param value the value on the axis.
     * @return a formatted label to display for the given value.
     */
    public String getLabel(int binIndex) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(startDate);
      if (binIndex > 0) {
        cal.add(calendarField,binIndex*incr);
      }
      return formatter.format(cal.getTime());
    }
  }



