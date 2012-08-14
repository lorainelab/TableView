/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umn.genomics.table.jfree;

import edu.umn.genomics.table.AbstractTableModelView;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author James E Johnson <jj@umn.edu>
 * @version 
 */
public class JFreeScatterPlot extends AbstractTableModelView {
    XYDataset dataset = new XYDataset() {

        @Override
        public DomainOrder getDomainOrder() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getItemCount(int series) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getX(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getXValue(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Number getY(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getYValue(int series, int item) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getSeriesCount() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Comparable getSeriesKey(int series) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int indexOf(Comparable seriesKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void addChangeListener(DatasetChangeListener listener) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void removeChangeListener(DatasetChangeListener listener) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public DatasetGroup getGroup() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void setGroup(DatasetGroup group) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    };

    public JFreeScatterPlot(TableModel tableModel, ListSelectionModel lsm) {
        super(tableModel, lsm);
    }
    
   private  JFreeChart createChart(XYDataset xydataset)
    {
        JFreeChart jfreechart = ChartFactory.createScatterPlot("Scatter Plot demo 2", "X", "Y", xydataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = jfreechart.getXYPlot();
        xyplot.setRenderer(new XYDotRenderer());
        NumberAxis numberaxis = (NumberAxis)xyplot.getDomainAxis();
        numberaxis.setAutoRangeIncludesZero(false);
        return jfreechart;
    }

    public  JPanel createdemoPanel()
    {
        JFreeChart jfreechart = createChart(dataset);
        ChartPanel chartpanel = new ChartPanel(jfreechart);
        chartpanel.setVerticalAxisTrace(true);
        chartpanel.setHorizontalAxisTrace(true);
        chartpanel.setPopupMenu(null);
        chartpanel.setDomainZoomable(true);
        chartpanel.setRangeZoomable(true);
        return chartpanel;
    }

}
