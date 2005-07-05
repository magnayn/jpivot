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

package com.tonbeller.jpivot.mondrian;

import mondrian.olap.Exp;
import mondrian.olap.QueryAxis;

import org.apache.log4j.Logger;


/**
 * Quax implementation for Mondrian
 */
public class MondrianQuax extends com.tonbeller.jpivot.olap.query.Quax {

  private MondrianModel model;
  private Exp originalSet;

  static Logger logger = Logger.getLogger(MondrianQuax.class);

  /**
   * c'tor
   * @param monQuax
   */
  MondrianQuax(int ordinal, QueryAxis queryAxis, MondrianModel model) {
    super(ordinal);

    this.model = model;
    originalSet = queryAxis.getSet();
 
    super.setUti(new MondrianQuaxUti(model));
  }


  /**
   * @return the original set
   */
  public Exp getOriginalSet() {
    return originalSet;
  }
 
} // End MondrianQuax
