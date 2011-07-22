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
package com.tonbeller.jpivot.tags;

import java.net.URL;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;

import com.tonbeller.jpivot.core.ModelFactory;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.test.olap.TestAxis;
import com.tonbeller.jpivot.test.olap.TestDimension;
import com.tonbeller.jpivot.test.olap.TestOlapModel;
import com.tonbeller.jpivot.test.olap.TestOlapModelUtils;
import com.tonbeller.wcf.controller.RequestContext;

/**
 * @author andreas
 */
public class TestOlapModelTag extends OlapModelTag {
  String onColumns = "Measures";
  String onRows = "Products";
  String config = null;
  
  /**
   * creates a test olap model
   */
  protected OlapModel getOlapModel(RequestContext context) throws Exception {
    URL url;
    if (config == null)
      url = getDefaultConfig();
    else
      url = pageContext.getServletContext().getResource(config);
    TestOlapModel model = (TestOlapModel) ModelFactory.instance(url);
    model.setAxis(0, createAxis(model, onColumns));
    model.setAxis(1, createAxis(model, onRows));
    return model;
  }

  /**
   * @return
   */
  protected URL getDefaultConfig() {
    return getClass().getResource("/com/tonbeller/jpivot/test/olap/config.xml");
  }

  TestAxis createAxis(TestOlapModel tom, String names) throws JspException {
    StringTokenizer st = new StringTokenizer(names);
    TestAxis a = createAxis1(tom, st.nextToken());
    while (st.hasMoreTokens()) {
      TestAxis b = createAxis1(tom, st.nextToken());
      a = TestOlapModelUtils.crossJoin(a, b);
    }
    return a;
  }
  
  TestAxis createAxis1(TestOlapModel tom, String name) throws JspException {
    TestDimension dim = (TestDimension)tom.getDimension(name);
    if (dim == null)
      throw new JspException("Dimension " + name + " not found");
    return TestOlapModelUtils.createAxis(dim);
  }

  
  /**
   * Returns the onColumns.
   * @return String
   */
  public String getOnColumns() {
    return onColumns;
  }

  /**
   * Returns the onRows.
   * @return String
   */
  public String getOnRows() {
    return onRows;
  }

  /**
   * Sets the onColumns.
   * @param onColumns The onColumns to set
   */
  public void setOnColumns(String onColumns) {
    this.onColumns = onColumns;
  }

  /**
   * Sets the onRows.
   * @param onRows The onRows to set
   */
  public void setOnRows(String onRows) {
    this.onRows = onRows;
  }

  /**
   * Returns the config.
   * @return String
   */
  public String getConfig() {
    return config;
  }

  /**
   * Sets the config.
   * @param config The config to set
   */
  public void setConfig(String config) {
    this.config = config;
  }

}
