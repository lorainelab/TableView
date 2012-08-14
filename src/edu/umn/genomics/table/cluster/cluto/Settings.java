/*
 * @(#) $RCSfile: ClutoMatrixView.java,v $ $Revision: 1.1 $ $Date: 2003/07/28 19:30:20 $ $Name: TableView1_2 $
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


package edu.umn.genomics.table.cluster.cluto;

import java.io.Serializable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.text.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import edu.umn.genomics.component.*;
import edu.umn.genomics.table.*;
import edu.umn.genomics.graph.*;
import edu.umn.genomics.graph.swing.*;
import jcluto.*;

/**
 * ClutoMatrixView displays a hierarchical clustering of rows from a table.
 * The clustering is displayed as a Dendogram which is drawn as line segments 
 * on a graph widget.  The axes of the graph are zoomable.
 * The row selection of the table is displayed on the dendogram.  
 * The user can trace out a rectangle on the dendogram to edit the 
 * row selection set for the table. 
 * @author       J Johnson
 * @version $Revision: 1.1 $ $Date: 2003/07/28 19:30:20 $  $Name: TableView1_2 $ 
 * @since        1.0
 * @see  ColumnMap
 * @see  TableContext
 * @see  javax.swing.table.TableModel
 * @see  javax.swing.ListSelectionModel
 */
public class Settings extends JPanel {
    private int rowHeight;
    private int colWidth;
    private float minValue = -1f;
    private float midValue = 0f;
    private float maxValue = 1f;
    private boolean doubleGradient = true;
    private Color minColor = Color.GREEN;
    private Color midColor = Color.BLACK;
    private Color maxColor = Color.RED;
    

    //
    //
    //
    //   DoubleGradient
    //
    //   MinValue MinColor 
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //

    class SettingsPanel extends JPanel {

      private String dblGradientCBTitle = "Use Double Gradient";
      private String minValueLabel = "Minimum Value";
      private String midValueLabel = "Midpoint Value";
      private String maxValueLabel = "Maximum Value";

      private JCheckBox dblGradientCB;
      private JFormattedTextField minValueField;
      private JFormattedTextField midValueField;
      private JFormattedTextField maxValueField;
      private JButton minColorBtn;
      private JButton midColorBtn;
      private JButton maxColorBtn;
      public SettingsPanel() {
        dblGradientCB = new JCheckBox(dblGradientCBTitle);
        dblGradientCB.addItemListener( new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            setDoubleGradient(e.getStateChange() == ItemEvent.SELECTED);
          }
        });
        minValueField = new JFormattedTextField(NumberFormat.getNumberInstance());
        minValueField.setValue(new Float(getMinValue()));
        minValueField.setColumns(10);
        minValueField.addPropertyChangeListener("value", new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            setMinValue(((Number)((JFormattedTextField)e.getSource()).getValue()).floatValue());
          }
        });
        midValueField = new JFormattedTextField(NumberFormat.getNumberInstance());
        midValueField.setValue(new Float(getMidValue()));
        midValueField.setColumns(10);
        midValueField.addPropertyChangeListener("value", new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            setMidValue(((Number)((JFormattedTextField)e.getSource()).getValue()).floatValue());
          }
        });
        maxValueField = new JFormattedTextField(NumberFormat.getNumberInstance());
        maxValueField.setValue(new Float(getMaxValue()));
        maxValueField.setColumns(10);
        maxValueField.addPropertyChangeListener("value", new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent e) {
            setMaxValue(((Number)((JFormattedTextField)e.getSource()).getValue()).floatValue());
          }
        });
        minColorBtn = new JButton();
        minColorBtn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Color newColor = JColorChooser.showDialog(
                                           minColorBtn.getTopLevelAncestor(),
                                           "Choose Minimum Value Color",
                                           minColorBtn.getBackground());
            if (newColor != null) {
                minColorBtn.setBackground(newColor);
                setMinColor(newColor);
            }
          }
        });
        midColorBtn = new JButton();
        midColorBtn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Color newColor = JColorChooser.showDialog(
                                           midColorBtn.getTopLevelAncestor(),
                                           "Choose Midimum Value Color",
                                           midColorBtn.getBackground());
            if (newColor != null) {
                midColorBtn.setBackground(newColor);
                setMidColor(newColor);
            }
          }
        });
        maxColorBtn = new JButton();
        maxColorBtn.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Color newColor = JColorChooser.showDialog(
                                           maxColorBtn.getTopLevelAncestor(),
                                           "Choose Maximum Value Color",
                                           maxColorBtn.getBackground());
            if (newColor != null) {
                maxColorBtn.setBackground(newColor);
                setMaxColor(newColor);
            }
          }
        });

        SpringLayout layout = new SpringLayout();
        setLayout(layout);
        JLabel label = new JLabel(AppResources.getInstance().getString("ClutoMinValueLabel","Minimum Value")); 
        
      }
    }

    public Settings() {
      super();
    }

    public void setDoubleGradient(boolean onOff) {
      doubleGradient = onOff;
      repaint();
    }
    public boolean getDoubleGradient() {
      return doubleGradient;
    }
      
    public float getMinValue() {
      return minValue;
    }
    public void setMinValue(float minValue) {
      this.minValue = minValue;
      repaint();
    }

    public float getMidValue() {
      return midValue;
    }
    public void setMidValue(float midValue) {
      this.midValue = midValue;
      repaint();
    }

    public float getMaxValue() {
      return maxValue;
    }
    public void setMaxValue(float maxValue) {
      this.maxValue = maxValue;
      repaint();
    }

    public Color getMinColor() {
      return minColor;
    }
    public void setMinColor(Color minColor) {
      this.minColor = minColor;
      repaint();
    }

    public Color getMidColor() {
      return midColor;
    }
    public void setMidColor(Color midColor) {
      this.midColor = midColor;
      repaint();
    }

    public Color getMaxColor() {
      return maxColor;
    }
    public void setMaxColor(Color maxColor) {
      this.maxColor = maxColor;
      repaint();
    }
    
    public Color getColor(float value) {
      return value <= minValue 
             ? minColor 
             : value >= maxValue 
               ? maxColor
               : doubleGradient 
                 ? value == midValue
                   ? midColor
                   : value < midValue
                     ? interpolate(minColor,midColor,minValue,midValue,value) 
                     : interpolate(midColor,maxColor,midValue,maxValue,value)
                 : interpolate(midColor,maxColor,minValue,maxValue,value);
    }
    public Color interpolate(Color c1, Color c2, float v1, float v2, float v) {
      int rgb1 = c1.getRGB();
      int rgb2 = c2.getRGB();
      int a1 = (rgb1 >> 24) & 0xff;
      int r1 = (rgb1 >> 16) & 0xff;
      int g1 = (rgb1 >>  8) & 0xff;
      int b1 = (rgb1      ) & 0xff;
      int da = ((rgb2 >> 24) & 0xff) - a1;
      int dr = ((rgb2 >> 16) & 0xff) - r1;
      int dg = ((rgb2 >>  8) & 0xff) - g1;
      int db = ((rgb2      ) & 0xff) - b1;
      float rel = (v - v1) / (v2 - v1);
      int rgb = (((int) (a1 + da * rel)) << 24) |
                (((int) (r1 + dr * rel)) << 16) |
                (((int) (g1 + dg * rel)) <<  8) |
                (((int) (b1 + db * rel))      );
      return new Color(rgb);
    }

    BufferedImage negImg = createGradientImage(Color.GREEN, Color.BLACK);
    BufferedImage posImg = createGradientImage(Color.BLACK, Color.RED);
    /**
     * Creates a gradient image given specified <CODE>Color</CODE>(s)
     * @param color1 <CODE>Color</CODE> to display at left side of gradient
     * @param color2 <CODE>Color</CODE> to display at right side of gradient
     * @return returns a gradient image
     */
    private BufferedImage createGradientImage(Color color1, Color color2) {
        BufferedImage image = (BufferedImage)java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(256,1);       
        Graphics2D graphics = image.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, color1, 255, 0, color2);
        graphics.setPaint(gp);
        graphics.drawRect(0, 0, 255, 1);
        return image;
    }
    public void paintComponent(Graphics g) {
      // g.drawImage(posImage, insets.left+negImage.getWidth()+1, insets.top, posImage.getWidth(), GRAD_HEIGHT, null);^M
      super.paintComponent(g);
      int mid = getWidth()/2;
      Graphics2D g2 = (Graphics2D)g;
      AffineTransform xform = g2.getTransform();
      DecimalFormat dfmt = new DecimalFormat("0.##E0");
      Font font = g2.getFont();
      Font derivedFont = null;
      FontMetrics fm = g2.getFontMetrics(font);
      Rectangle2D bnds = fm.getStringBounds(dfmt.format(getMinValue())+" "+dfmt.format(getMaxValue()), g2);
      //  Check that labels fit else derive smaller font
      double scale = Double.NaN;
      float invScale = 1f;
      if ((bnds.getWidth() > getWidth() || bnds.getHeight() > getHeight()) && (bnds.getWidth() > 0 && bnds.getHeight() > 0)) {
        scale = Math.min(getWidth()/bnds.getWidth(),getHeight()/bnds.getHeight());
        //invScale = (float)(1./scale);
        derivedFont = font.deriveFont(font.getSize2D() * (float)scale);
        // fm = g2.getFontMetrics(derivedFont);
        g2.setFont(derivedFont);
        fm = g2.getFontMetrics();
      } 
      if (doubleGradient) {
        GradientPaint gp = new GradientPaint(0, 0, minColor, mid, 0, midColor);
        g2.setPaint(gp);
        g2.fillRect(0, 0, mid+1, getHeight()+1);
        gp = new GradientPaint(mid, 0, midColor, getWidth()+1, 0, maxColor);
        g2.setPaint(gp);
        g2.fillRect(mid+1, 0, mid+1, getHeight()+1);
      } else {
        GradientPaint gp = new GradientPaint(0, 0, midColor, getWidth()+1, 0, maxColor);
        g2.setPaint(gp);
        g2.fillRect(0, 0,  getWidth()+1, getHeight()+1);

      }
        // Labels
        if (!Double.isNaN(scale) && scale > 0.) {
          // g2.scale(scale,scale);
          // fm = g2.getFontMetrics();
        }
        g2.setColor(getForeground()); 
        String v = dfmt.format(getMinValue());
        g2.drawString(v,1f*invScale, (getHeight()-fm.getDescent())*invScale);
        v = dfmt.format(getMaxValue());
        g2.drawString(v,(getWidth()-fm.stringWidth(v)-1f)*invScale, (getHeight() - fm.getDescent())*invScale);
      if (derivedFont != null) {
        g2.setFont(font);
      }
      g2.setTransform(xform);
    }
}
