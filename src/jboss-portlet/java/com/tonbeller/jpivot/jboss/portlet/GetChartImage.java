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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jboss.portal.portlet.impl.ActionResponseImpl;
import org.jboss.portal.server.WindowContext;
import org.jboss.portlet.JBossActionResponse;

import com.tonbeller.jpivot.chart.GetChart;

public class GetChartImage {
	private static Logger logger = Logger.getLogger(GetChartImage.class);
    
    /** Processes requests for displaying generated chart images.
     * @param request servlet request
     * @param response servlet response
     * @throws PortletException
     */
    public static void processRequest(
    		HttpServletRequest request,
			HttpServletResponse response, 
			WindowContext windowCtx, 
			ActionResponse portletResponse) throws PortletException {

        String filename = request.getParameter("filename");
        logger.info("GetChart called: filename="+filename);
        if (filename == null) {
            throw new PortletException("Parameter 'filename' must be supplied");
        }

        //  Replace ".." with ""
        //  This is to prevent access to the rest of the file system
        filename = GetChart.searchReplace(filename, "..", "");

        //  Check the file exists
        File file = new File(System.getProperty("java.io.tmpdir"), filename);
        if (!file.exists()) {
            throw new PortletException("File '" + file.getAbsolutePath() + "' does not exist");
        }
        try {
			//  Serve it up
			sendTempFile(file, portletResponse, windowCtx, getMimeType(file));
			
		} catch (FileNotFoundException e) {
			throw new PortletException(e);
		} catch (IOException e) {
			throw new PortletException(e);
		}
    }

    public static String getMimeType(File file) {
		String mimeType = null;
		String filename = file.getName();
		if (filename.length() > 5) {
		    if (filename.substring(filename.length() - 5, filename.length()).equals(".jpeg") || 
		        filename.substring(filename.length() - 5, filename.length()).equals(".jpg")) {
		        return "image/jpeg";
		    }
		    else if (filename.substring(filename.length() - 4, filename.length()).equals(".png")) {
		        return "image/png";
		    }
		    else if (filename.substring(filename.length() - 4, filename.length()).equals(".gif")) {
		        return "image/gif";
		    }
		}
		return null;
	}
		
	/**
	* Binary streams the specified file to the portlet response
	*
	* @param file The file to be streamed.
	* @param response The HTTP response object.
	* @param windowCtx  the portlet window content
	* @param contentType The mime type of the file, null allowed.
	*
	* @throws IOException  if there is an I/O problem.
	* @throws FileNotFoundException  if the file is not found.
	*/
	public static void sendTempFile(
			File file, 
			PortletResponse portletResponse,
			WindowContext windowCtx,
	        String contentType)
	    throws IOException, FileNotFoundException {
	
		if (!file.exists()) {
		    throw new FileNotFoundException(file.getAbsolutePath());
		}

		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
	    BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			baos = new ByteArrayOutputStream(16384);
		    bos = new BufferedOutputStream(baos);
		    byte[] input = new byte[1024];
		    boolean eof = false;
		    while (!eof) {
		        int length = bis.read(input);
		        if (length == -1) {
		            eof = true;
		        } else {
		            bos.write(input, 0, length);
		        }
		    }
		} finally {
			if (bos != null)
				bos.flush();
		    if (bis != null)
		    	bis.close();
		    if (bos != null)
		    	bos.close();
		}
        
        // This will only work in JBoss
        //ActionResponseImpl responseImpl = (ActionResponseImpl) portletResponse;
        //org.jboss.portal.server.output.Result portletResult = new org.jboss.portal.server.output.StreamResult(windowCtx, contentType, baos.toByteArray());
        //responseImpl.setResult(portletResult);
        // This will only work in JBoss
        JBossActionResponse responseImpl = (JBossActionResponse) portletResponse;
        responseImpl.sendBytes(contentType, baos.toByteArray());
   }

}
