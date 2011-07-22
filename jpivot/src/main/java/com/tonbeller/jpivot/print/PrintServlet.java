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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Options;
import org.apache.fop.configuration.Configuration;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.tonbeller.jpivot.chart.ChartComponent;
import com.tonbeller.jpivot.table.TableComponent;
import com.tonbeller.wcf.component.RendererParameters;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.controller.RequestContextFactoryFinder;
import com.tonbeller.wcf.utils.XmlUtils;

/**
 * Expected HTTP GET Parameters:
 *  - cube - the jpivot cube id, used to lookup table, chart, and print references
 *  - type - the output type, 0 for xls, 1 for pdf
 *  - filenamePre - (optional) - defaults to xls_export, specifies the filename
 *  				 the browser will use to name the output.
 *
 * @author  arosselet
 * @version
 */
public class PrintServlet extends HttpServlet {
  private static Logger logger = Logger.getLogger(PrintServlet.class);
  private static final int XML = 0;
  private static final int PDF = 1;
  String basePath;

  /** Initializes the servlet.
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    try {
      // set base FOP FONT directory.  The font config  stuff will be looked for here
      Configuration.put("fontBaseDir", config.getServletContext().getRealPath("/WEB-INF/jpivot/print/"));
      // get the physical path for the config file
      String fopConfigPath = config.getServletContext().getRealPath("/WEB-INF/jpivot/print/userconfig.xml");
      // load the user proerties, contining the CustomFont font.
      new Options(new File(fopConfigPath));

    } catch (FOPException e) {
      e.printStackTrace();
      logger.info("FOP user config file not loaded");
    } catch (Exception e) {
      e.printStackTrace();
      logger.info("FOP user config file not loaded");
    }
  }

  /** Destroys the servlet.
   */
  public void destroy() {

  }

  /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
   * @param request servlet request
   * @param response servlet response
   */

  protected void processRequest(RequestContext context) throws ServletException, IOException {
    HttpServletRequest request = context.getRequest();
    HttpServletResponse response = context.getResponse();
    if (request.getParameter("cube") != null && request.getParameter("type") != null) {
      try {
        String xslUri = null;
        String filename = null;
        int type = Integer.parseInt(request.getParameter("type"));
        String filenamePre = "xls_export";
        if (request.getParameter("filenamePre") != null) {
        	filenamePre = request.getParameter("filenamePre");
        }
        switch (type) {
        case XML:
          xslUri = "/WEB-INF/jpivot/table/xls_mdxtable.xsl";
          RendererParameters.setParameter(context.getRequest(), "mode", "excel", "request");
          response.setContentType("application/vnd.ms-excel");
          filename = filenamePre + ".xls";
          break;
        case PDF:
          xslUri = "/WEB-INF/jpivot/table/fo_mdxtable.xsl";
          RendererParameters.setParameter(context.getRequest(), "mode", "print", "request");
          response.setContentType("application/pdf");
          filename = filenamePre + ".pdf";
          break;
        }
        if (xslUri != null) {
          boolean xslCache = true;
          // get references to needed elements
          String tableRef = "table" + request.getParameter("cube");
          String chartRef = "chart" + request.getParameter("cube");
          String printRef = "print" + request.getParameter("cube");

          Map parameters = new HashMap();

          OutputStream outStream = response.getOutputStream();
          PrintWriter out = new PrintWriter(outStream);
          HttpSession session = request.getSession();
          // set up filename for download.
          response.setHeader("Content-Disposition", "attachment; filename=" + filename);

          // get TableComponent
          TableComponent table = (TableComponent) context.getModelReference(tableRef);
          // only proceed if table component exists
          if (table != null) {
            // add parameters from printConfig
            PrintComponent printConfig = (PrintComponent) context.getModelReference(printRef);
            if (printConfig != null) {
              if (printConfig.isSetTableWidth()) {
                parameters.put(printConfig.PRINT_TABLE_WIDTH, new Double(printConfig.getTableWidth()));
              }
              if (printConfig.getReportTitle().trim().length() != 0) {
                parameters.put(printConfig.PRINT_TITLE, printConfig.getReportTitle().trim());
              }
              parameters.put(printConfig.PRINT_PAGE_ORIENTATION, printConfig.getPageOrientation());
              parameters.put(printConfig.PRINT_PAPER_TYPE, printConfig.getPaperType());
              if (printConfig.getPaperType().equals("custom")) {
                parameters.put(printConfig.PRINT_PAGE_WIDTH, new Double(printConfig.getPageWidth()));
                parameters.put(printConfig.PRINT_PAGE_HEIGHT, new Double(printConfig.getPageHeight()));
              }
              parameters.put(printConfig.PRINT_CHART_PAGEBREAK, new Boolean(printConfig.isChartPageBreak()));

            }

            // add parameters and image from chart if visible
            ChartComponent chart = (ChartComponent) request.getSession().getAttribute(chartRef);
            if (chart != null && chart.isVisible()) {

              String host = request.getServerName();
              int port = request.getServerPort();
              String location = request.getContextPath();
              String scheme = request.getScheme();
              if (type == PDF) {
            	  String chartFilename = chart.getFilename();
            	  if (chartFilename.indexOf("..") >= 0) {
            		  throw new ServletException("File '" + chartFilename + "' does not exist within temp directory.");
            	  }
            	  File file = new File(System.getProperty("java.io.tmpdir"), chartFilename);
                  if (!file.exists()) {
                      throw new ServletException("File '" + file.getAbsolutePath() + "' does not exist.");
                  }
            	  parameters.put("chartimage", "file:" + file.getCanonicalPath());
              } else { 
            	  String chartServlet = scheme + "://" + host + ":" + port + location + "/GetChart";
            	  parameters.put("chartimage", chartServlet + "?filename=" + chart.getFilename());
              }
              parameters.put("chartheight", new Integer(chart.getChartHeight()));
              parameters.put("chartwidth", new Integer(chart.getChartWidth()));
            }

            //parameters.put("message",table.getReportTitle());
            // add "context" and "renderId" to parameter map

            //parameters.put("renderId", renderId);
            parameters.put("context", context.getRequest().getContextPath());

            // Some FOP-PDF versions require a complete URL, not a path
            //parameters.put("contextUrl", createContextURLValue(context));

            table.setDirty(true);
            Document document = table.render(context);
            table.setDirty(true);

            DOMSource source = new DOMSource(document);
            // set up xml transformation
            Transformer transformer = XmlUtils.getTransformer(session, xslUri, xslCache);
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

            // if thisis XML, then we are done, so output xml file.
            if (type == XML) {
              System.out.println("Writing XLS");
              response.setContentLength(sw.toString().length());
              out.write(sw.toString());
              RendererParameters.removeParameter(context.getRequest(), "mode", "excel", "request");
              // process FO to PDF
            } else {
              // if this is PDF, then need to generate PDF from the FO xml
              System.out.println("Creating PDF!");
              try {
                ByteArrayInputStream bain = new ByteArrayInputStream(sw.toString().getBytes("UTF-8"));
                ByteArrayOutputStream baout = new ByteArrayOutputStream(16384);
                convertFO2PDF(bain, baout);
                final byte[] content = baout.toByteArray();
                response.setContentLength(content.length);
                outStream.write(content);
                RendererParameters.removeParameter(context.getRequest(), "mode", "print", "request");
                //convertXML2PDF(document.toString(), xslUri, outStream);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
            //close output streams
            out.flush();
            out.close();
            outStream.flush();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * converts FO xml into PDF using the FOP processor
   */
  public void convertFO2PDF(ByteArrayInputStream bain, ByteArrayOutputStream baout) throws IOException, FOPException {

    System.out.println("Construct driver");
    Driver driver = new Driver();

    System.out.println("Setup Renderer (output format)");
    driver.setRenderer(Driver.RENDER_PDF);

    try {
      driver.setOutputStream(baout);
      System.out.println("Setup input");
      try {
        driver.setInputSource(new InputSource(bain));

        System.out.println("Process FO");
        driver.run();
        System.out.println("PDF file generation completed");
      } finally {
      }
    } finally {
    }
  }

  /** Handles the HTTP <code>GET</code> method.
   * @param request servlet request
   * @param response servlet response
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  /** Handles the HTTP <code>POST</code> method.
   * @param request servlet request
   * @param response servlet response
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestContext context = RequestContextFactoryFinder.createContext(request, response, true);
    try {
      processRequest(context);
    } finally {
      context.invalidate();
    }
  }

  /** Returns a short description of the servlet.
   */
  public String getServletInfo() {
    return "Export OLAP table";
  }

}
