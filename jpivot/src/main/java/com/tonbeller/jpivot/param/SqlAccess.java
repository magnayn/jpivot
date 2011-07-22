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
package com.tonbeller.jpivot.param;

import javax.sql.DataSource;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.wcf.param.SessionParam;

/**
 * provides information about members that may
 * be used in (drill-thru) SQL queries
 * 
 * @author av
 */
public interface SqlAccess extends Extension {
  public static final String ID = "sqlAccess";

  /**
   * returns the data source to access the database
   */
  DataSource getDataSource();

  /**
   * returns a parameter that describes the member.
   * returns null, if the member does not represent a column
   * in the database, for example because its the ALL member
   * or a calculated member.
   * @paramName the name of the new parameter. If null, the 
   * unique name of the members dimension with ".param" appended is used.
   */
  SessionParam createParameter(Member m, String paramName);

  /**
   * returns a parameter that describes the member.
   * returns null, if the member does not represent a column
   * in the database, for example because its the ALL member
   * or a calculated member.
   * @param m the member
   * @param propertyName name of a member property whose value
   * will be used as sqlValue of the parameter. This may be used
   * in situations where the members key is for example "John Smith" 
   * and the member property is the customer ID "123" which we want to use
   * in the SQL queries.
   * @param paramName teh name of the new paramter
   */
  SessionParam createParameter(Member m, String paramName, String propertyName);
}
