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

import javax.servlet.jsp.JspException;

import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.wcf.component.Component;
import com.tonbeller.wcf.component.ComponentTag;
import com.tonbeller.wcf.controller.RequestContext;


/**
 * creates a ChartComponent
 * @author Robin Bagot
 */
public class ChartComponentTag extends ComponentTag {
	String query;
    String baseDisplayURL;
    String controllerURL;

  /**
   * creates a ChartComponent
   */
  public Component createComponent(RequestContext context) throws Exception {
	// find the Olap Model
	OlapModel model = (OlapModel) context.getModelReference(getQuery());
	// check model exists
	if (model == null)
	  throw new JspException("component \"" + getQuery() + "\" not found");
/*
	// choose xml configuration
	URL configUrl;
	if (configXml != null)
	  configUrl = pageContext.getServletContext().getResource(configXml);
	else
	  configUrl = getClass().getResource("config.xml");
	return TableComponentFactory.instance(id, configUrl, olapModel);
*/
    return new ChartComponent(id, null, query, baseDisplayURL, controllerURL, context);
  }

  /**
   * Returns the query attribute (actually a reference to an Olap Model)
   * @return String
   */
  public String getQuery() {
	return query;
  }

  /**
   * Sets the query attribute (actually a reference to an Olap Model)
   * @param ref The ref to set
   */
  public void setQuery(String query) {
	this.query = query;
  }

  /**
   * Returns the baseDisplayURL
   * @return String
   */
  public String getBaseDisplayURL() {
    return baseDisplayURL;
  }

  /**
   * Sets the baseDisplayURL
   * @param baseDisplayURL The baseDisplayURL to set
   */
  public void setBaseDisplayURL(String baseDisplayURL) {
    this.baseDisplayURL = baseDisplayURL;
  }

public String getControllerURL() {
	return controllerURL;
}

public void setControllerURL(String controllerURL) {
	this.controllerURL = controllerURL;
}
  
}
