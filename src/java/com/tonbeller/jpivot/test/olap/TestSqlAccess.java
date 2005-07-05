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
package com.tonbeller.jpivot.test.olap;

import javax.sql.DataSource;

import com.tonbeller.jpivot.core.ExtensionSupport;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.Property;
import com.tonbeller.jpivot.param.SqlAccess;
import com.tonbeller.wcf.param.SessionParam;

/**
 * @author av
 */
public class TestSqlAccess extends ExtensionSupport implements SqlAccess  {

  public DataSource getDataSource() {
    return null;
  }

  public SessionParam createParameter(Member m, String paramName) {
    SessionParam p = new SessionParam();
    p.setDisplayName(m.getLevel().getLabel());
    p.setDisplayValue(m.getLabel());
    p.setMdxValue(m.getLabel());
    p.setName(paramName);
    p.setSqlValue(m.getLabel());
    return p;
  }

  public SessionParam createParameter(Member m, String paramName, String propertyName) {
    SessionParam p = new SessionParam();
    p.setDisplayName(m.getLevel().getLabel());
    p.setDisplayValue(m.getLabel());
    p.setMdxValue(m.getLabel());
    p.setName(paramName);
    Property prop = m.getProperty(propertyName);
    p.setSqlValue(prop.getValue());
    return p;
  }

}
