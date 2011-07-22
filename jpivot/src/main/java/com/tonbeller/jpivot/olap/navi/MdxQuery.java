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
package com.tonbeller.jpivot.olap.navi;

import com.tonbeller.jpivot.core.Extension;
import com.tonbeller.wcf.format.FormatException;

/**
 * Lets the user view and modify the mdx
 * 
 * @author av
 */
public interface MdxQuery extends Extension {
  /**
   * name of the Extension for lookup
   */
  public static final String ID = "mdxQuery";

  /**
   * returns the current (valid) mdx query
   * @return String
   */  
  String getMdxQuery();
  
  /**
   * sets the mdx from user input
   * @throws com.tonbeller.wcf.format.FormatException if the syntax is invalid. The internal mdx is not updated in this
   * case
   * @param mdxQuery the query to set
   */
  void setMdxQuery(String mdxQuery) throws FormatException;
}
