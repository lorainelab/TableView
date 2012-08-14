/*
 * @(#) $RCSfile: CopyToTemp.java,v $ $Revision: 1.1 $ $Date: 2003/05/15 16:16:24 $ $Name: TableView1_2 $
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


package edu.umn.genomics.file;

import edu.umn.genomics.table.ExceptionHandler;
import java.io.*;
import java.net.*;

/*
 mostly copied from:
 /home/jj/src/cbc/src/libsrc/java/edu/umn/genomics/file/OpenInputSource.java
*/

/**
 * CopyToTemp provides methods to open a named source 
 * that is either a URL or a File.
 * 
 * @author       J Johnson
 * @version $Revision: 1.1 $ $Date: 2003/05/15 16:16:24 $  $Name: TableView1_2 $ 
 * @since        1.0
 */
public class CopyToTemp {
  /**
   * Open the given URL or file pathname for reading.
   * @param source the URL or pathname to open.
   * @return an input stream opened on the source.
   */
  public static InputStream getInputStream(String source) throws IOException {
    // URL?
    try {
      URL url = new URL(source);
      try {
        InputStream is = url.openStream();
        return is;
      } catch (Exception se) {
          ExceptionHandler.popupException(""+se);
      }
    }  catch (Exception ue) {
        ExceptionHandler.popupException(""+ue);
    }
    // local file?
    try {
      InputStream is = new FileInputStream(source);
      return is;
    } catch (IOException e) {
      if (source.charAt(0) == '~') {
        // Try a Unix shell tilde expansion
        try {
          String shell = "/usr/bin/csh";
          if ((new File(shell)).exists()) {
            String args[] = new String[3];
            args[0] = shell;
            args[1] = "-c";
            args[2] = "echo " + source;
            Process p = Runtime.getRuntime().exec(args);
            BufferedReader br = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
            String path = "";
            String so;
            while ((so = br.readLine()) != null) {
              path += so;
            }
            InputStream is = new FileInputStream(path);
            return is;
          }
        } catch (Exception rte) {
          ExceptionHandler.popupException(""+rte);
        }
      }
      throw e;
    }
  }

  /**
   * Open the given URL or file pathname for reading.
   * @param source the URL or pathname to open.
   * @return a Reader opened on the source.
   */
  public static BufferedReader getBufferedReader(String source) throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream(source)));
  }

  /**
   * Copy data from a source to a temporary file.
   * @param source Data source location, filename or URL
   * @param prefix Prefix applied to temproary file name.
   * @param suffix Suffix applied to temproary file name.
   * @return The temporary file.
   */
  public static File copyToTempFile(String source, String prefix, String suffix) throws IOException {
    // Open Reader on source
    BufferedReader rdr = getBufferedReader(source);
    // Open Temp File
    File tmp = File.createTempFile(prefix,suffix);
    // Set to delete on exit 
    tmp.deleteOnExit();
    // Open a writer to the file
    FileWriter wtr = new FileWriter(tmp);
    // Copy source to Temp File
    int bufLen = 4096;  // Picked arbitrarily
    char[] cbuf = new char[bufLen];
    for (int n = rdr.read(cbuf, 0, bufLen); n >= 0; n = rdr.read(cbuf, 0, bufLen)) {
      wtr.write(cbuf, 0, n);
    }
    // Close the temp file.
     wtr.close();
    return tmp;
  }

  /**
   * Copy data from a source to a temporary file.
   * @param source Data source location, filename or URL
   * @return The path for the temporary file.
   */
  public static String copyToTempFile(String source) throws IOException {
    String prefix = "Crow_";
    String suffix = ".tmp";
    File tmp = copyToTempFile(source,prefix,suffix);
    return tmp != null ? tmp.getAbsolutePath() : null;
  }


  /**
   * Copy each data from each named source to a temporary file.
   * @param args Data source locations: filename or URL
   */
  public static void main(String[] args) {
    for (int i = 0; i < args.length; i++) {
      try { 
        String tmpName = copyToTempFile(args[i]);
        System.out.println("Temporary File:  " + tmpName);
      } catch (Exception ex) {
        ExceptionHandler.popupException("Error copying " + args[i] + "\t" + ex);
      }  
    }
  }
}
