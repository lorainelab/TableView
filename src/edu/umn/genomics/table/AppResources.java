/*
 * @(#) $RCSfile$ $Revision$ $Date$ $Name$
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
import java.lang.*;
import java.util.*;

/**
 * AppResources supplies Messages and component labels to allow for 
 * internationalization.
 * 
 * @author       J Johnson
 * @version $Revision$ $Date$  $Name$ 
 * @since        1.0
 */
public class AppResources {
  // Preferences.userNodeForPackage(edu.umn.genomics.table.TableView.class).importPreferences(is);
  Locale locale = Locale.getDefault();
  ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", locale);
  private static AppResources instance = null;

  public static AppResources getInstance() {
    if (instance == null) {
      instance = new AppResources();
    }
    return instance;
  }
  public void setLocale(Locale locale) {
    locale = locale;
    messages = ResourceBundle.getBundle("MessagesBundle", locale);
  }

  public Object getObject(String key, Object defaultValue) {
    try {
      return messages.getObject(key);
    } catch (NullPointerException ex) {
        ExceptionHandler.popupException(""+ex);
    } catch (MissingResourceException ex) {
        ExceptionHandler.popupException(""+ex);
    }
    return defaultValue;
  }

  public Object getObject(Object obj,  String property, Object defaultValue) {
    String key = obj.getClass().toString() + "." + property;
    return getObject(key, defaultValue);
  }
  public String getString(String key, String defaultValue) {
    try {
      return messages.getString(key);
    } catch (NullPointerException ex) {
        ExceptionHandler.popupException(""+ex);
    } catch (MissingResourceException ex) {
        ExceptionHandler.popupException(""+ex);
    }
    return defaultValue;
  }
  public String getString(Object obj,  String property, String defaultValue) {
    String key = obj.getClass().toString() + "." + property;
    return getString(key, defaultValue);
  }
}
