/*
 * @(#) $RCSfile: GlyphLabelJ3D.java,v $ $Revision: 1.1 $ $Date: 2003/05/15 18:23:50 $ $Name: TableView1_2 $
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


package edu.umn.genomics.table.dv.j3d;  //DataViewer
import java.io.Serializable;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * @author       J Johnson
 * @version $Revision: 1.1 $ $Date: 2003/05/15 18:23:50 $  $Name: TableView1_2 $
 * @since        1.0
 */
public class GlyphLabelJ3D extends BranchGroup implements Serializable {
  GlyphLabelJ3D(String label) {
    Billboard bb = new Billboard();
  }
}

