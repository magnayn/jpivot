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

package com.tonbeller.jpivot.print;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.tonbeller.wcf.component.ComponentSupport;
import com.tonbeller.wcf.controller.Dispatcher;
import com.tonbeller.wcf.controller.DispatcherSupport;
import com.tonbeller.wcf.controller.RequestContext;
/**
 * @author Ati
 */
public class PrintComponent extends ComponentSupport {
    
    public final String PRINT_PAGE_WIDTH="pageWidth";
    public final String PRINT_PAPER_TYPE="paper.type";
    public final String PRINT_PAGE_ORIENTATION="pageOrientation";
    public final String PRINT_TABLE_WIDTH="tableWidth";
    public final String PRINT_PAGE_HEIGHT="pageHeight";
    public final String PRINT_TITLE="reportTitle";
    public final String PRINT_CHART_PAGEBREAK="chartPageBreak";    
        
    private PropertyChangeSupport propertySupport;
    //String ref;
    
    /**
     * Holds value of property reportTitle.
     */
    private String reportTitle="";
    Dispatcher dispatcher = new DispatcherSupport();
    
    /**
     * Holds value of property setPageWidth.
     */
    private boolean setPageWidth=false;
    
    
    /**
     * Holds value of property pageWidth.
     */
    private double pageWidth=21.0;
    
     /**
     * Holds value of property pageHeight.
     */
    private double pageHeight=29.7;
    
    /**
     * Holds value of property pageOrientation.
     */
    private String pageOrientation="portrait";
    
    /**
     * Holds value of property paperType.
     */
    private String paperType="A4";
    
    /**
     * Holds value of property setTableWidth.
     */
    private boolean setTableWidth=false;
    
    /**
     * Holds value of property chartPageBreak.
     */
    private boolean chartPageBreak=false;
    
    /**
     * Holds value of property tableWidth.
     */
    private double tableWidth;    
    
    public PrintComponent(String id,  RequestContext context) {
		super(id, null);
        propertySupport = new PropertyChangeSupport(this);
        //this.locale = context.getLocale();
	// extend the controller
	getDispatcher().addRequestListener(null, null, dispatcher);
    }
    
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * called once by the creating tag
     */
    public void initialize(RequestContext context) throws Exception {
            super.initialize(context);
    }
    /**
     * Getter for property reportTitle.
     * @return Value of property reportTitle.
     */
    public String getReportTitle() {
        return this.reportTitle;
    }
    
    /**
     * Setter for property reportTitle.
     * @param reportTitle New value of property reportTitle.
     */
    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }
    
    public org.w3c.dom.Document render(RequestContext context) throws Exception {
        return null;
    }
    
    /**
     * Getter for property setPageWidth.
     * @return Value of property setPageWidth.
     */
    public boolean isSetPageWidth() {
        return this.setPageWidth;
    }
    
    /**
     * Setter for property setPageWidth.
     * @param setPageWidth New value of property setPageWidth.
     */
    public void setSetPageWidth(boolean setPageWidth) {
        this.setPageWidth = setPageWidth;
    }
    
    /**
     * Getter for property pageWidth.
     * @return Value of property pageWidth.
     */
    public double getPageWidth() {
        return this.pageWidth;
    }
    
    /**
     * Setter for property pageWidth.
     * @param pageWidth New value of property pageWidth.
     */
    public void setPageWidth(double pageWidth) {
        this.pageWidth = pageWidth;
    }
    
    /**
     * Getter for property pageOrientation.
     * @return Value of property pageOrientation.
     */
    public String getPageOrientation() {
        return this.pageOrientation;
    }
    
    /**
     * Setter for property pageOrientation.
     * @param pageOrientation New value of property pageOrientation.
     */
    public void setPageOrientation(String pageOrientation) {
        this.pageOrientation = pageOrientation;
    }
    
    /**
     * Getter for property paperType.
     * @return Value of property paperType.
     */
    public String getPaperType() {
        return this.paperType;
    }
    
    /**
     * Setter for property paperType.
     * @param paperType New value of property paperType.
     */
    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }
    
    /**
     * Getter for property setTableWidth.
     * @return Value of property setTableWidth.
     */
    public boolean isSetTableWidth() {
        return this.setTableWidth;
    }
    
    /**
     * Setter for property setTableWidth.
     * @param setTableWidth New value of property setTableWidth.
     */
    public void setSetTableWidth(boolean setTableWidth) {
        this.setTableWidth = setTableWidth;
    }
    
    /**
     * Getter for property chartPageBreak.
     * @return Value of property chartPageBreak.
     */
    public boolean isChartPageBreak() {
        return this.chartPageBreak;
    }
    
    /**
     * Setter for property chartPageBreak.
     * @param chartPageBreak New value of property chartPageBreak.
     */
    public void setChartPageBreak(boolean chartPageBreak) {
        this.chartPageBreak = chartPageBreak;
    }
    
    /**
     * Getter for property pageHeight.
     * @return Value of property pageHeight.
     */
    public double getPageHeight() {
        return this.pageHeight;
    }
    
    /**
     * Setter for property pageHeight.
     * @param pageHeight New value of property pageHeight.
     */
    public void setPageHeight(double pageHeight) {
        this.pageHeight = pageHeight;
    }
    
    /**
     * Getter for property tableWidth.
     * @return Value of property tableWidth.
     */
    public double getTableWidth() {
        return this.tableWidth;
    }
    
    /**
     * Setter for property tableWidth.
     * @param tableWidth New value of property tableWidth.
     */
    public void setTableWidth(double tableWidth) {
        this.tableWidth = tableWidth;
    }
    
}
