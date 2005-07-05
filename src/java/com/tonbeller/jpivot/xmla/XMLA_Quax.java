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
package com.tonbeller.jpivot.xmla;

import org.apache.log4j.Logger;

import com.tonbeller.jpivot.olap.mdxparse.Exp;
import com.tonbeller.jpivot.olap.mdxparse.QueryAxis;

/**
 * Quax implementation for XMLA
 */
public class XMLA_Quax extends com.tonbeller.jpivot.olap.query.Quax {

  private XMLA_Model model;
  private Exp originalSet;

  static Logger logger = Logger.getLogger(XMLA_Quax.class);

  /**
   * c'tor
   * @param monQuax
   */
  XMLA_Quax(int ordinal, QueryAxis queryAxis, XMLA_Model model) {
    super(ordinal);

    this.model = model;
    originalSet = queryAxis.getExp();
 
    super.setUti(new XMLA_QuaxUti());
  }


  /**
   * @return the original set
   */
  public Exp getOriginalSet() {
    return originalSet;
  }

 
} // End XMLA_Quax
