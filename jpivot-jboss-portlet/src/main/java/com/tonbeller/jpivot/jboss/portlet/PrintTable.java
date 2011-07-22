/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2005 SHERMAN WOOD.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 */
package com.tonbeller.jpivot.jboss.portlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.log4j.Logger;
import org.jboss.portal.server.WindowContext;
import org.jboss.portlet.JBossActionResponse;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.tonbeller.jpivot.chart.ChartComponent;
import com.tonbeller.jpivot.print.PrintComponent;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.wcf.component.RendererParameters;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestContextFactoryFinder;
import com.tonbeller.wcf.utils.XmlUtils;

/**
 * @author swood
 */
public class PrintTable {
	private static Logger logger = Logger.getLogger(PrintTable.class);
    public static final int XLS = 0;
    public static final int PDF = 1;
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws PortletException
     */
    
    public static void processRequest(
    		HttpServletRequest request,
			HttpServletResponse response, 
			WindowContext windowCtx, 
			ActionResponse portletResponse) throws PortletException {
        if (request.getParameter("cube") == null || request.getParameter("type") == null){
        	return;
        }
        
        try {

            RequestContext context = RequestContextFactoryFinder.createContext(request,response,true);
            
            String xslUri = null;
            
            int type = Integer.parseInt(request.getParameter("type"));
            String contentType = null;
            
            switch (type){
                case XLS:
                	xslUri="/WEB-INF/jpivot/table/xls_mdxtable.xsl";
	                RendererParameters.setParameter(context.getRequest(), "mode","excel","request");
	                contentType = "application/vnd.ms-excel";
	                // filename = "xls_export.xls";
	                break;
                case PDF:
                	xslUri="/WEB-INF/jpivot/table/fo_mdxtable.xsl";
	                RendererParameters.setParameter(context.getRequest(), "mode","print","request");
	                contentType = "application/pdf";
	                // filename = "xls_export.pdf";
	                break;
	            default:
	            	return;
            }
            // get references to needed elements
            String tableRef = "table"+request.getParameter("cube");
            String chartRef = "chart"+request.getParameter("cube");
            String printRef = "print"+request.getParameter("cube");
            
            // get TableComponent
            TableComponent table = (TableComponent) context.getModelReference(tableRef);
            // only proceed if table component exists
            
            if (table == null) {
            	return;
            }
            // add parameters from printConfig
            PrintComponent printConfig = (PrintComponent) context.getModelReference(printRef);
            
            Map parameters = new HashMap();

            if (printConfig != null) {
                if (printConfig.isSetTableWidth()) {
                    parameters.put(printConfig.PRINT_TABLE_WIDTH, new Double(printConfig.getTableWidth()));
                }
                if (printConfig.getReportTitle().trim().length()!=0){
                    parameters.put(printConfig.PRINT_TITLE, printConfig.getReportTitle().trim());
                }
                parameters.put(printConfig.PRINT_PAGE_ORIENTATION, printConfig.getPageOrientation());
                parameters.put(printConfig.PRINT_PAPER_TYPE, printConfig.getPaperType());
                if (printConfig.getPaperType().equals("custom")){
                    parameters.put(printConfig.PRINT_PAGE_WIDTH, new Double(printConfig.getPageWidth()));
                    parameters.put(printConfig.PRINT_PAGE_HEIGHT, new Double(printConfig.getPageHeight()));
                }
                parameters.put(printConfig.PRINT_CHART_PAGEBREAK, new Boolean(printConfig.isChartPageBreak()));

            }
            
            // add parameters and image from chart if visible
            ChartComponent chart = (ChartComponent)request.getSession().getAttribute(chartRef);
            if (chart != null && chart.isVisible() ) {
                
                String host = request.getServerName();
                int port = request.getServerPort();
                
                StringBuffer sb = new StringBuffer();
                sb.append("http://")
                    .append(host)
                    .append(":")
                    .append(port)
                    .append(chart.getGraphURL(context));
                String chartURL = sb.toString();
                
                logger.debug("image is: " + chartURL);
                parameters.put("chartimage", chartURL);

                parameters.put("chartheight", new Integer(chart.getChartHeight()));
                parameters.put("chartwidth", new Integer(chart.getChartWidth()));
            }
            
            parameters.put("context", context.getRequest().getContextPath());
            
            // Some FOP-PDF versions require a complete URL, not a path
            //parameters.put("contextUrl", createContextURLValue(context));

            // Render the table to get the XML result
            table.setDirty(true);
            Document document = table.render(context);
            table.setDirty(true);
            
            DOMSource source = new DOMSource(document);
            
            // set up for the xml transformation
            HttpSession session = request.getSession();
            Transformer transformer = XmlUtils.getTransformer(session, xslUri, true);
            for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
                String name = (String) it.next();
                Object value = parameters.get(name);
                transformer.setParameter(name, value);
            }
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            //do transform
            transformer.transform(source, result);
            sw.flush();
            
            // This will only work in JBoss
            JBossActionResponse responseImpl = (JBossActionResponse) portletResponse;
            
            // if this is XLS, then we are done, so output xls file.
            if (type == XLS){
                logger.info("Creating XLS");
                //org.jboss.portal.server.output.Result portletResult = new org.jboss.portal.server.output.StreamResult(windowCtx, contentType, sw.toString().getBytes());
                //responseImpl.setResult(portletResult);
                responseImpl.sendBytes(contentType, sw.toString().getBytes());
                RendererParameters.removeParameter(context.getRequest(), "mode","excel","request");
                // process FO to PDF
            } else {
                // if this is PDF, then need to generate PDF from the FO xml
            	logger.info("Creating PDF!");
                ByteArrayInputStream bain = new ByteArrayInputStream(sw.toString().getBytes("UTF-8"));
                ByteArrayOutputStream baout = new ByteArrayOutputStream(16384);
                convertFO2PDF(bain,baout);
                final byte[] content = baout.toByteArray();
                //org.jboss.portal.server.output.Result portletResult = new org.jboss.portal.server.output.StreamResult(windowCtx, contentType, content);
                //responseImpl.setResult(portletResult);
                responseImpl.sendBytes(contentType, content);
                RendererParameters.removeParameter(context.getRequest(), "mode","print","request");
            }
        } catch (Exception e){
			throw new PortletException(e);
        }
    }
    
    /**
     * converts FO xml into PDF using the FOP processor
     */
    public static void convertFO2PDF(ByteArrayInputStream bain,ByteArrayOutputStream baout) throws IOException,FOPException {
        
    	logger.debug("Construct driver");
        Driver driver = new Driver();
        
        logger.debug("Setup Renderer (output format)");
        driver.setRenderer(Driver.RENDER_PDF);
        
        try {
            driver.setOutputStream(baout);
            logger.debug("Setup input");
            driver.setInputSource(new InputSource(bain));
            
            logger.debug("Process FO");
            driver.run();
            logger.debug("PDF file generation completed");
        } catch (Exception e){
            logger.error(e);
        }
    }

}
