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

import com.tonbeller.jpivot.olap.navi.MdxQuery;
import com.tonbeller.wcf.format.FormatException;

/**
 * Created on 06.12.2002
 * 
 * @author av
 */
public class TestMdxQuery extends TestExtensionSupport implements MdxQuery {
  
  String mdxQuery = "valid mdx does not start with 'x'";
 
  public String getMdxQuery() {
    return mdxQuery;
  }

  /**
   * @see com.tonbeller.jpivot.olap.navi.MdxQuery#setMdxQuery(String)
   */
  public void setMdxQuery(String mdxQuery) {
    if (mdxQuery.startsWith("x")) {
      // do not store invalid mdx
      throw new FormatException("mdx must not start with 'x'");
    }
    this.mdxQuery = mdxQuery;
  }

}
