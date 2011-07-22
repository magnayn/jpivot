/*
 * ====================================================================
 * This software is subject to the terms of the Common Public License
 * Agreement, available at the following URL:
 *   http://www.opensource.org/licenses/cpl.html .
 * Copyright (C) 2003-2004 TONBELLER AG.
 * All Rights Reserved.
 * You must accept the terms of that agreement to use this software.
 * ====================================================================
 *
 * 
 */
package com.tonbeller.jpivot.table;

import java.net.URL;

import javax.servlet.jsp.JspException;

import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.wcf.component.Component;
import com.tonbeller.wcf.component.ComponentTag;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * creates a TableComponentImpl
 * @author av
 */
public class TableComponentTag extends ComponentTag {
  String query;
  String configXml = null;

  /**
   * creates a TableComponentImpl
   */
  public Component createComponent(RequestContext context) throws Exception {
    // find the olap query
    OlapModel olapModel = (OlapModel) context.getModelReference(query);
    if (olapModel == null)
      throw new JspException("query \"" + query + "\" not found");

    // choose xml configuration
    URL configUrl;
    if (configXml != null)
      configUrl = pageContext.getServletContext().getResource(configXml);
    else
      configUrl = getClass().getResource("config.xml");

    return TableComponentFactory.instance(id, configUrl, olapModel);
   
  }

  /**
   * Returns the configXml.
   * @return String
   */
  public String getConfigXml() {
    return configXml;
  }

  /**
   * Sets the configXml.
   * @param configXml The configXml to set
   */
  public void setConfigXml(String configXml) {
    this.configXml = configXml;
  }

  /**
   * Returns the query.
   * @return String
   */
  public String getQuery() {
    return query;
  }

  /**
   * Sets the query.
   * @param query The query to set
   */
  public void setQuery(String query) {
    this.query = query;
  }

}
