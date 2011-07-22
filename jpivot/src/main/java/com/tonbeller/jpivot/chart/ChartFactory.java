/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 */
package com.tonbeller.jpivot.chart;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryAxis3D;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.TableOrder;

/**
 * Utility methods for creating charts.
 * This class is derived from JFreeChart ChartFactory class.
 *
 */
public class ChartFactory {
    // set url prefix to empty string
    static String urlPrefix = "";

    /**
     * Creates a chart containing multiple pie charts, from a TableDataset.
     *
     * @param title  the chart title.
     * @param data  the dataset for the chart.
     * @param extractType  <code>PER_ROW</code> or <code>PER_COLUMN</code> (defined in 
     *                     {@link PiePlot}).
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a pie chart.
     */
    public static JFreeChart createPieChart(String title,
            java.awt.Font titleFont,
            CategoryDataset data,
            TableOrder order,
            boolean legend,
            boolean tooltips,
            boolean urls,
            PieURLGenerator urlGenerator) {

        MultiplePiePlot plot = new MultiplePiePlot(data);
        plot.setDataExtractOrder(order);

        PiePlot pp = (PiePlot) plot.getPieChart().getPlot();
        //pp.setInsets(new Insets(0, 5, 5, 5));
        pp.setBackgroundPaint(null);
        // no outline around each piechart
        pp.setOutlineStroke(null);
        //plot.setOutlineStroke(null);
        PieToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardPieToolTipGenerator();
        }

        //PieURLGenerator urlGenerator = null;
        if (!urls) {
            urlGenerator = null;
        }

        pp.setToolTipGenerator(tooltipGenerator);
        pp.setLabelGenerator(null);
        pp.setURLGenerator(urlGenerator);

        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates a sample dataset for the demo.
     * 
     * @return A sample dataset.
     */
    public static JFreeChart create3DPieChart(String title,
            java.awt.Font titleFont,
            CategoryDataset data,
            TableOrder order,
            boolean legend,
            boolean tooltips,
            boolean urls,
            PieURLGenerator urlGenerator) {

        MultiplePiePlot plot = new MultiplePiePlot(data);
        plot.setDataExtractOrder(order);

        //plot.setOutlineStroke(null);

        JFreeChart pieChart = new JFreeChart(new PiePlot3D(null));
        pieChart.setBackgroundPaint(null);
        plot.setPieChart(pieChart);

        PiePlot3D pp = (PiePlot3D) plot.getPieChart().getPlot();
        pp.setBackgroundPaint(null);
        //pp.setInsets(new Insets(0, 5, 5, 5));

        // no outline around each piechart
        pp.setOutlineStroke(null);

        PieToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = new StandardPieToolTipGenerator();
        }

        if (!urls) {
            urlGenerator = null;
        }

        pp.setToolTipGenerator(tooltipGenerator);
        pp.setLabelGenerator(null);
        pp.setURLGenerator(urlGenerator);
        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates a vertical bar chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a vertical bar chart.
     */
    public static JFreeChart createBarChart(String title,
            java.awt.Font titleFont,
            String categoryAxisLabel,
            String valueAxisLabel,
            CategoryDataset data,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls,
            CategoryURLGenerator urlGenerator) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        BarRenderer renderer = new BarRenderer();

        if (tooltips) {
            renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(urlGenerator);
        }
        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates a vertical 3D-effect bar chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a vertical 3D-effect bar chart.
     */
    public static JFreeChart createBarChart3D(String title,
            java.awt.Font titleFont,
            String categoryAxisLabel,
            String valueAxisLabel,
            CategoryDataset data,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls,
            CategoryURLGenerator urlGenerator) {

        CategoryAxis categoryAxis = new CategoryAxis3D(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis3D(valueAxisLabel);

        BarRenderer3D renderer = new BarRenderer3D();

        //renderer.setLabelGenerator(new StandardCategoryLabelGenerator());
        if (tooltips) {
            renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(urlGenerator);
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        plot.setForegroundAlpha(0.75f);

        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates a stacked vertical bar chart with default settings.
     *
     * @param title  the chart title.
     * @param domainAxisLabel  the label for the category axis.
     * @param rangeAxisLabel  the label for the value axis.
     * @param data   the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return The chart.
     */
    public static JFreeChart createStackedBarChart(String title,
            java.awt.Font titleFont,
            String domainAxisLabel,
            String rangeAxisLabel,
            CategoryDataset data,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls,
            CategoryURLGenerator urlGenerator) {

        CategoryAxis categoryAxis = new CategoryAxis(domainAxisLabel);
        ValueAxis valueAxis = new NumberAxis(rangeAxisLabel);

        // create the renderer...
        StackedBarRenderer renderer = new StackedBarRenderer();
        if (tooltips) {
            renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(urlGenerator);
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates a stacked vertical bar chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a stacked vertical bar chart.
     */
    public static JFreeChart createStackedBarChart3D(String title,
            java.awt.Font titleFont,
            String categoryAxisLabel,
            String valueAxisLabel,
            CategoryDataset data,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls,
            CategoryURLGenerator urlGenerator) {

        // create the axes...
        CategoryAxis categoryAxis = new CategoryAxis3D(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis3D(valueAxisLabel);

        // create the renderer...
        CategoryItemRenderer renderer = new StackedBarRenderer3D();
        CategoryToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardCategoryToolTipGenerator();
        }
        if (urls) {
            renderer.setItemURLGenerator(urlGenerator);
        }
        renderer.setToolTipGenerator(toolTipGenerator);

        // create the plot...
        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);

        // create the chart...
        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates a line chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a line chart.
     */
    public static JFreeChart createLineChart(String title,
            java.awt.Font titleFont,
            String categoryAxisLabel,
            String valueAxisLabel,
            CategoryDataset data,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls,
            CategoryURLGenerator urlGenerator) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setLinesVisible(true);
        renderer.setShapesVisible(false);
        if (tooltips) {
            renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(urlGenerator);
        }
        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates an area chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an area chart.
     */
    public static JFreeChart createAreaChart(String title,
            java.awt.Font titleFont,
            String categoryAxisLabel,
            String valueAxisLabel,
            CategoryDataset data,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls,
            CategoryURLGenerator urlGenerator) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        AreaRenderer renderer = new AreaRenderer();
        if (tooltips) {
            renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(urlGenerator);
        }
        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates a stacked area chart with default settings.
     *
     * @param title  the chart title.
     * @param categoryAxisLabel  the label for the category axis.
     * @param valueAxisLabel  the label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return an area chart.
     */
    public static JFreeChart createStackedAreaChart(String title,
            java.awt.Font titleFont,
            String categoryAxisLabel,
            String valueAxisLabel,
            CategoryDataset data,
            PlotOrientation orientation,
            boolean legend,
            boolean tooltips,
            boolean urls,
            CategoryURLGenerator urlGenerator) {

        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

        StackedAreaRenderer renderer = new StackedAreaRenderer();
        if (tooltips) {
            renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setItemURLGenerator(urlGenerator);
        }

        CategoryPlot plot = new CategoryPlot(data, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, titleFont, plot, legend);

        return chart;

    }

    /**
     * Creates and returns a time series chart.
     * <P>
     * A time series chart is an XYPlot with a date axis (horizontal) and a number axis (vertical),
     * and each data item is connected with a line.
     * <P>
     * Note that you can supply a TimeSeriesCollection to this method, as it implements the
     * XYDataset interface.
     *
     * @param title  the chart title.
     * @param timeAxisLabel  a label for the time axis.
     * @param valueAxisLabel  a label for the value axis.
     * @param data  the dataset for the chart.
     * @param legend  a flag specifying whether or not a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return a time series chart.
     */
    public static JFreeChart createTimeSeriesChart(String title,
            java.awt.Font titleFont,
            String timeAxisLabel,
            String valueAxisLabel,
            XYDataset data,
            boolean legend,
            boolean tooltips,
            boolean urls) {

        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        timeAxis.setLowerMargin(0.02);  // reduce the default margins on the time axis
        timeAxis.setUpperMargin(0.02);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        XYPlot plot = new XYPlot(data, timeAxis, valueAxis, null);

        XYToolTipGenerator tooltipGenerator = null;
        if (tooltips) {
            tooltipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();
        //new StandardXYToolTipGenerator(DateFormat.getDateInstance());                                                
        }

        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }

        plot.setRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES, tooltipGenerator, urlGenerator));

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;

    }

}
