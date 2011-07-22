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
import com.tonbeller.jpivot.olap.model.Expression;
import com.tonbeller.jpivot.olap.model.Hierarchy;
import com.tonbeller.jpivot.olap.model.Member;
import com.tonbeller.jpivot.olap.model.PropertyExpr;

/**
 * erzeugt ein berechnetes Element
 * 
 * @author av
 */
public interface CalcMember extends Extension {
  public static final String ID = "calcMember";

  /**
   * creates a new Measure
   * @param name name of the new measure
   * @param expr calculation of new measure
   * @param format null or format_string property
   * @return
   * @throws InvalidExpressionException
   */
  Member createMeasure(String name, Expression expr, Expression format, PropertyExpr[] propex)
    throws InvalidExpressionException;

  Member createMember(String name, Expression expr, Hierarchy hier, Member parent)
    throws InvalidExpressionException;

}
