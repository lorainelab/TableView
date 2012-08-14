/*
 * @(#) $RCSfile: TableView.java,v $ $Revision: 1.51 $ $Date: 2004/08/02 20:23:46 $ $Name: TableView1_3 $
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

import java.io.Serializable;
import java.io.*;
import java.net.*;
import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.*;
import java.util.*;
import java.util.prefs.*;
import edu.umn.genomics.graph.*;

public class TableViewPreferences {
  protected static String defaultPath = "edu/umn/genomics/table/defaultPreferences.xml";
  static {
    System.err.println("Checking for Preferences node: edu/umn/genomics/table");
    try {
      if (!Preferences.userRoot().nodeExists("edu/umn/genomics/table")) {
        importPreferences(defaultPath);
      }
    } catch (Exception ex) {
      ExceptionHandler.popupException(""+ex);
    }
  }

  public static void setDefaults() throws IOException, BackingStoreException,InvalidPreferencesFormatException {
    importPreferences(defaultPath);
  }

  public static void importPreferences(String path) throws IOException,InvalidPreferencesFormatException {
    ClassLoader cl = TableViewPreferences.class.getClassLoader();
    System.err.println("importPreferences : " + path);
    Preferences.userRoot().importPreferences(cl.getResourceAsStream(path));
  }

  public static void exportPreferences(String path) throws IOException, BackingStoreException {
      Preferences pref = Preferences.userNodeForPackage(edu.umn.genomics.table.TableViewPreferences.class);
      pref.exportSubtree(new FileOutputStream(path));
  }

  public static void savePreference(Object object, String attribute) {
    try {
      Class objClass = object.getClass();
      Preferences pref = Preferences.userNodeForPackage(object.getClass()).node(object.getClass().getSimpleName());
      System.err.println("savePreference " + pref + "  " + attribute);
      try {
        Method getMethod = object.getClass().getMethod("get"+attribute,new Class[0]);
        Class paramClass = getMethod.getReturnType();
        // String
        if (paramClass.isAssignableFrom(java.lang.String.class)) {
          pref.put(attribute, (String)getMethod.invoke(object));
        // Color
        } else if (paramClass.isAssignableFrom(java.awt.Color.class)) {
          pref.putLong(attribute, (long)((Color)getMethod.invoke(object)).getRGB());
        // Font
        } else if (paramClass.isAssignableFrom(java.awt.Font.class)) {
          pref.put(attribute, fontToString((Font)getMethod.invoke(object)));
        } else if (paramClass.isAssignableFrom(edu.umn.genomics.graph.Drawable.class)) {
          pref.put(attribute, drawableToString((Drawable)getMethod.invoke(object)));
        } else {
        // Date Format
        // Number
        // Boolean
        // Class
        }
      } catch (NoSuchMethodException ex) {
          ExceptionHandler.popupException(""+ex);
      } catch (InvocationTargetException ex) {
          ExceptionHandler.popupException(""+ex);
      } catch (IllegalAccessException ex) {
          ExceptionHandler.popupException(""+ex);
      }
      pref.sync();
    } catch (NullPointerException ex) {
      ExceptionHandler.popupException(""+ex);
    } catch (BackingStoreException ex) {
      ExceptionHandler.popupException(""+ex);
    }
  }

  public static void savePreference(Class objClass, String attribute, Object value) {
    try {
      Preferences pref = Preferences.userNodeForPackage(objClass).node(objClass.getSimpleName());
      System.err.println("savePreference " + pref + "  " + attribute);
      try {
        Method getMethod = objClass.getMethod("get"+attribute,new Class[0]);
        Class paramClass = getMethod.getReturnType();
        // String
        if (paramClass.isAssignableFrom(java.lang.String.class)) {
          pref.put(attribute, (String)value);
        // Color
        } else if (paramClass.isAssignableFrom(java.awt.Color.class)) {
          pref.putLong(attribute, (long)((Color)value).getRGB());
        // Font
        } else if (paramClass.isAssignableFrom(java.awt.Font.class)) {
          pref.put(attribute, fontToString((Font)value));
        } else if (paramClass.isAssignableFrom(edu.umn.genomics.graph.Drawable.class)) {
          pref.put(attribute, drawableToString((Drawable)value));
        } else {
        // Date Format
        // Number
        // Boolean
        // Class
        }
      } catch (NoSuchMethodException ex) {
          ExceptionHandler.popupException(""+ex);
      }
      pref.sync();
    } catch (NullPointerException ex) {
      ExceptionHandler.popupException(""+ex);
    } catch (BackingStoreException ex) {
      ExceptionHandler.popupException(""+ex);
    }
  }

  public static void setAttributes(Object object) {
    try {
      Class objClass = object.getClass();

      Preferences pref = Preferences.userNodeForPackage(object.getClass()).node(object.getClass().getSimpleName());
      for (String key : pref.keys()) {
        // System.err.println(objClass.getSimpleName() + " " + key);
        try {
          Method getMethod = object.getClass().getMethod("get"+key,new Class[0]);
          Method setMethod = object.getClass().getMethod("set"+key, new Class[]{getMethod.getReturnType()});
          Class paramClass = setMethod.getParameterTypes()[0];
          
          // String
          if (paramClass.isAssignableFrom(java.lang.String.class)) {
            String value = pref.get(key, (String)getMethod.invoke(object));
            setMethod.invoke(object,new Object[]{value});
          // Color
          } else if (paramClass.isAssignableFrom(java.awt.Color.class)) {
            Color color = new Color(new Long(pref.getLong(key,(long)((Color)getMethod.invoke(object)).getRGB())).intValue());
            setMethod.invoke(object,new Object[]{color});
          // Font
          } else if (paramClass.isAssignableFrom(java.awt.Font.class)) {
            String fontString = pref.get(key,fontToString((Font)getMethod.invoke(object)));
            setMethod.invoke(object,new Object[]{fontFromString(fontString)});
          } else if (paramClass.isAssignableFrom(edu.umn.genomics.graph.Drawable.class)) {
            String drawableString = pref.get(key,drawableToString((Drawable)getMethod.invoke(object)));
            setMethod.invoke(object,new Object[]{drawableFromString(drawableString)});
          } else {
          // Date Format
          // Number
          // Boolean
          // Class
          }
        } catch (NoSuchMethodException ex) {
          ExceptionHandler.popupException(""+ex);
        } catch (InvocationTargetException ex) {
          ExceptionHandler.popupException(""+ex);
        } catch (IllegalAccessException ex) {
          ExceptionHandler.popupException(""+ex);
        }
      }
    } catch (NullPointerException ex) {
      ExceptionHandler.popupException(""+ex);
    } catch (BackingStoreException ex) {
      ExceptionHandler.popupException(""+ex);
    }

  }

  public static void setAttribute(Object object, String attribute, String value) {
    try {
      Class objClass = object.getClass();
        try {
          Method getMethod = object.getClass().getMethod("get"+attribute,new Class[0]);
          Method setMethod = object.getClass().getMethod("set"+attribute, new Class[]{getMethod.getReturnType()});
          Class paramClass = setMethod.getParameterTypes()[0];
          
          // String
          if (paramClass.isAssignableFrom(java.lang.String.class)) {
            setMethod.invoke(object,new Object[]{value});
          // Color
          } else if (paramClass.isAssignableFrom(java.awt.Color.class)) {
            Color color = new Color(new Long(value).intValue());
            setMethod.invoke(object,new Object[]{color});
          // Font
          } else if (paramClass.isAssignableFrom(java.awt.Font.class)) {
            setMethod.invoke(object,new Object[]{fontFromString(value)});
          } else if (paramClass.isAssignableFrom(edu.umn.genomics.graph.Drawable.class)) {
            setMethod.invoke(object,new Object[]{drawableFromString(value)});
          // Number
          } else if (java.lang.Number.class.isAssignableFrom(paramClass)) {
          // Primitives
          } else if (paramClass.isPrimitive()) {
            if (paramClass.equals(java.lang.Integer.TYPE)) {
              setMethod.invoke(object,new Object[]{new Integer(value)});
            } else if (paramClass.equals(java.lang.Double.TYPE)) {
              setMethod.invoke(object,new Object[]{new Double(value)});
            } else if (paramClass.equals(java.lang.Float.TYPE)) {
              setMethod.invoke(object,new Object[]{new Float(value)});
            } else if (paramClass.equals(java.lang.Long.TYPE)) {
              setMethod.invoke(object,new Object[]{new Long(value)});
            } else if (paramClass.equals(java.lang.Short.TYPE)) {
              setMethod.invoke(object,new Object[]{new Short(value)});
            } else if (paramClass.equals(java.lang.Byte.TYPE)) {
              setMethod.invoke(object,new Object[]{new Byte(value)});
            } else if (paramClass.equals(java.lang.Character.TYPE)) {
              setMethod.invoke(object,new Object[]{new Character(value.charAt(0))});
            } else if (paramClass.equals(java.lang.Boolean.TYPE)) {
              setMethod.invoke(object,new Object[]{new Boolean(value)});
            }
          } else {
          // Date Format
          // Boolean
          // Class
          }
        } catch (NoSuchMethodException ex) {
          ExceptionHandler.popupException(""+ex);
        } catch (InvocationTargetException ex) {
          ExceptionHandler.popupException(""+ex);
        } catch (IllegalAccessException ex) {
          ExceptionHandler.popupException(""+ex);
        }
    } catch (NullPointerException ex) {
      ExceptionHandler.popupException(""+ex);
    }
  }

  public static List<String> getAttributeNames(Object object) {
    List<String> attributes = new ArrayList<String>();
    try {
      Class objClass = object.getClass();
      for (Method getMethod : objClass.getMethods()) {
        if (getMethod.getName().startsWith("get") && getMethod.getParameterTypes().length == 0) {
          try {
            Method setMethod = object.getClass().getMethod(getMethod.getName().replaceFirst("g","s"), 
                                                            new Class[]{getMethod.getReturnType()});
            if (setMethod != null) {
              attributes.add(getMethod.getName().substring(3));
            }
          } catch (NoSuchMethodException ex) {
              ExceptionHandler.popupException(""+ex);
          }
        }
      }
    } catch (NullPointerException ex) {
      ExceptionHandler.popupException(""+ex);
    }
    return attributes;
  }

  public static Object getAttributeValue(Object object, String attribute) 
    throws NoSuchMethodException,IllegalAccessException,InvocationTargetException {
    Method getMethod = object.getClass().getMethod("get"+attribute,new Class[0]);
    return getMethod.invoke(object).toString();
  }

  public static Object getAttributePreference(Class objClass, String attribute) 
    throws NoSuchMethodException,IllegalAccessException,InvocationTargetException {
    Object value = null;
    Preferences pref = Preferences.userNodeForPackage(objClass).node(objClass.getSimpleName());
    // System.err.println(objClass.getSimpleName() + " " + attribute);
    Method getMethod = objClass.getMethod("get"+attribute,new Class[0]);
    Method setMethod = objClass.getMethod("set"+attribute, new Class[]{getMethod.getReturnType()});
    Class paramClass = setMethod.getParameterTypes()[0];
    // String
    if (paramClass.isAssignableFrom(java.lang.String.class)) {
      value = pref.get(attribute, null);
    // Color
    } else if (paramClass.isAssignableFrom(java.awt.Color.class)) {
      value = new Color(new Long(pref.getLong(attribute,0L)).intValue());
    // Font
    } else if (paramClass.isAssignableFrom(java.awt.Font.class)) {
      String fontString = pref.get(attribute,null);
      value = fontFromString(fontString);
    } else if (paramClass.isAssignableFrom(edu.umn.genomics.graph.Drawable.class)) {
      String drawableString = pref.get(attribute,null);
      value = drawableFromString(drawableString);
    } else {
    // Date Format
    // Number
    // Boolean
    // Class
    }
    return value;
  }

  public static String fontToString(Font font) {
    return font.getFontName()+","+Integer.toString(font.getStyle())+","+Integer.toString(font.getSize());
  }
  public static Font fontFromString(String fontString) {
    // name,style,size
    String[] values = fontString.split(",");
    String name = values[0];
    int style = Integer.parseInt(values[1]);
    int size = Integer.parseInt(values[2]);
    Font font = new Font(name,style,size);
    return font;
  }
  public static String drawableToString(Drawable drawable) {
    String s = drawable.getClass().getName();
    for (String name : getAttributeNames(drawable)) {
      try {
        s += ","+name+"="+getAttributeValue(drawable,name);
      } catch(Exception ex) {
          ExceptionHandler.popupException(""+ex);
      }
    }
    return s;
  }
  public static Drawable drawableFromString(String drawableString) {
    String[] values = drawableString.split(",");
    try {
      ClassLoader cl = TableViewPreferences.class.getClassLoader();
      Drawable drawable = (Drawable)cl.loadClass(values[0]).newInstance(); 
      for(int i = 1; i < values.length; i++) {
        String[] v = values[i].split("=");
        setAttribute(drawable, v[0], v[1]);
      }
      return drawable;
    } catch (Exception ex) {
      ExceptionHandler.popupException(""+ex);
    }
    return null;
  }

}
