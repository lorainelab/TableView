/*
 * @(#) $RCSfile: CacheColumnMap.java,v $ $Revision: 1.15 $ $Date: 2004/08/18 17:32:54 $ $Name: TableView1_3 $
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

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.lang.ref.*;
import java.lang.reflect.*;

/**
 * @author       J Johnson
 * @version $Revision$ $Date$  $Name$
 * @since        1.0
 */
public class FileBackedCache {
  private RandomAccessFile file = null;
  private FileChannel fileChannel = null;
  private MappedByteBuffer mappedBuffer = null;
  private final int scale = Integer.SIZE / Byte.SIZE;
  private long bufOffset = 0;
  private long bufSize = 10;
  private long len = 0;
  private IntBuffer buf = null;

  public FileBackedCache() {
  }

/*
  public FileBackedCache(Class itemType, Object dataArray) {
    // create a cache for the given itemType
    // if dataArray is not null, attempt to copy items to new cache
  }
*/
  private FileChannel getFileChannel()  throws IOException {
    if (fileChannel == null) {
      File tmpFile = File.createTempFile("int",".cache");
      tmpFile.deleteOnExit();
      file = new RandomAccessFile(tmpFile,"rw");
      fileChannel = file.getChannel();
    }
    return fileChannel;
  }
  private IntBuffer getBuffer(long position) throws IOException {
    if (buf == null || position < bufOffset || position >= bufOffset + bufSize) {
      bufOffset = position / bufSize * bufSize;
      if (mappedBuffer != null) {
        mappedBuffer.force();
      }
      mappedBuffer = getFileChannel().map(FileChannel.MapMode.READ_WRITE, bufOffset * scale,  bufSize * scale).load();
      buf = mappedBuffer.asIntBuffer();
    }
    return buf;
  }
  public int getInt(long position) throws IOException {
    IntBuffer ibuf = getBuffer((long)position);
    return ibuf.get((int)(position - bufOffset));
  }

  public void putInt(long position, int value) throws IOException {
    IntBuffer ibuf = getBuffer(position);
    ibuf.put((int)(position - bufOffset),value);
    len = Math.max(len,position);
  }

  public long length() {
    return len;
  }

}
